/*
 * Copyright (c) 2014, 2016 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.truffleruby.language.objects;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.FinalLocationException;
import com.oracle.truffle.api.object.IncompatibleLocationException;
import com.oracle.truffle.api.object.Location;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.utilities.NeverValidAssumption;

import org.truffleruby.language.RubyBaseNode;
import org.truffleruby.language.RubyGuards;
import org.truffleruby.language.objects.shared.SharedObjects;
import org.truffleruby.language.objects.shared.WriteBarrierNode;

@ImportStatic({ RubyGuards.class, ShapeCachingGuards.class })
public abstract class WriteObjectFieldNode extends RubyBaseNode {

    private final Object name;

    public WriteObjectFieldNode(Object name) {
        this.name = name;
    }

    public Object getName() {
        return name;
    }

    public void write(DynamicObject object, Object value) {
        executeWithGeneralize(object, value, false);
    }

    public abstract void executeWithGeneralize(DynamicObject object, Object value, boolean generalize);

    @Specialization(
            guards = {
                    "location != null",
                    "object.getShape() == cachedShape"
            },
            assumptions = { "cachedShape.getValidAssumption()", "validLocation" },
            limit = "getCacheLimit()")
    public void writeExistingField(DynamicObject object, Object value, boolean generalize,
            @Cached("getLocation(object, value)") Location location,
            @Cached("object.getShape()") Shape cachedShape,
            @Cached("createAssumption(cachedShape)") Assumption validLocation,
            @Cached("isShared(cachedShape)") boolean shared,
            @Cached("createWriteBarrierNode(shared)") WriteBarrierNode writeBarrierNode) {
        try {
            if (shared) {
                writeBarrierNode.executeWriteBarrier(value);
                synchronized (object) {
                    // Re-check the shape under the monitor as another thread might have changed it
                    // by adding a field (fine) or upgrading an existing field to Object storage
                    // (need to use the new storage)
                    if (object.getShape() != cachedShape) {
                        CompilerDirectives.transferToInterpreter();
                        executeWithGeneralize(object, value, generalize);
                        return;
                    }
                    location.set(object, value, cachedShape);
                }
            } else {
                location.set(object, value, cachedShape);
            }
        } catch (IncompatibleLocationException | FinalLocationException e) {
            // remove this entry
            validLocation.invalidate();
            // Generalization is handled by Shape#defineProperty as the field already exists
            executeWithGeneralize(object, value, generalize);
        }
    }

    @Specialization(
            guards = {
                    "location == null",
                    "object.getShape() == oldShape" },
            assumptions = { "oldShape.getValidAssumption()", "newShape.getValidAssumption()", "validLocation" },
            limit = "getCacheLimit()")
    public void writeNewField(DynamicObject object, Object value, boolean generalize,
            @Cached("getLocation(object, value)") Location location,
            @Cached("object.getShape()") Shape oldShape,
            @Cached("defineProperty(oldShape, value, generalize)") Shape newShape,
            @Cached("getNewLocation(newShape)") Location newLocation,
            @Cached("createAssumption(oldShape)") Assumption validLocation,
            @Cached("isShared(oldShape)") boolean shared,
            @Cached("createWriteBarrierNode(shared)") WriteBarrierNode writeBarrierNode) {
        try {
            if (shared) {
                writeBarrierNode.executeWriteBarrier(value);
                synchronized (object) {
                    // Re-check the shape under the monitor as another thread might have changed it
                    // by adding a field or upgrading an existing field to Object storage
                    // (we need to make sure to have the right shape to add the new field)
                    if (object.getShape() != oldShape) {
                        CompilerDirectives.transferToInterpreter();
                        executeWithGeneralize(object, value, generalize);
                        return;
                    }
                    newLocation.set(object, value, oldShape, newShape);
                }
            } else {
                newLocation.set(object, value, oldShape, newShape);
            }
        } catch (IncompatibleLocationException e) {
            // remove this entry
            validLocation.invalidate();
            // Make sure to generalize when adding a new field and the value is incompatible.
            // So writing an int and then later a double generalizes to adding an Object field.
            executeWithGeneralize(object, value, true);
        }
    }

    @Specialization(guards = "updateShape(object)")
    public void updateShapeAndWrite(DynamicObject object, Object value, boolean generalize) {
        executeWithGeneralize(object, value, generalize);
    }

    @TruffleBoundary
    @Specialization(replaces = { "writeExistingField", "writeNewField", "updateShapeAndWrite" })
    public void writeUncached(DynamicObject object, Object value, boolean generalize) {
        final boolean shared = SharedObjects.isShared(getContext(), object);
        if (shared) {
            SharedObjects.writeBarrier(getContext(), value);
            synchronized (object) {
                Shape shape = object.getShape();
                Shape newShape = defineProperty(shape, value, false);
                newShape.getProperty(name).setSafe(object, value, shape, newShape);
            }
        } else {
            object.define(name, value);
        }
    }

    protected Location getLocation(DynamicObject object, Object value) {
        final Shape oldShape = object.getShape();
        final Property property = oldShape.getProperty(name);

        if (PropertyFlags.isDefined(property) && property.getLocation().canSet(object, value)) {
            return property.getLocation();
        } else {
            return null;
        }
    }

    private static final Object SOME_OBJECT = new Object();

    protected Shape defineProperty(Shape oldShape, Object value, boolean generalize) {
        if (generalize) {
            value = SOME_OBJECT;
        }
        Property property = oldShape.getProperty(name);
        if (property != null && PropertyFlags.isRemoved(property)) {
            // Do not reuse location of removed properties
            Location location = oldShape.allocator().locationForValue(value);
            return oldShape.replaceProperty(property, property.relocate(location).copyWithFlags(0));
        } else {
            return oldShape.defineProperty(name, value, 0);
        }
    }

    protected Location getNewLocation(Shape newShape) {
        return newShape.getProperty(name).getLocation();
    }

    protected Assumption createAssumption(Shape shape) {
        if (!shape.isValid()) {
            return NeverValidAssumption.INSTANCE;
        }
        return Truffle.getRuntime().createAssumption("object location is valid");
    }

    protected int getCacheLimit() {
        return getContext().getOptions().INSTANCE_VARIABLE_CACHE;
    }

    protected boolean isShared(Shape shape) {
        return SharedObjects.isShared(getContext(), shape);
    }

    protected WriteBarrierNode createWriteBarrierNode(boolean shared) {
        if (shared) {
            return WriteBarrierNode.create();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return name + " =";
    }

}
