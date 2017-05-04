/*
 * Copyright (c) 2014, 2017 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.truffleruby.language.dispatch;

import org.truffleruby.RubyContext;
import org.truffleruby.builtins.CallerFrameAccess;
import org.truffleruby.core.string.StringOperations;
import org.truffleruby.language.RubyGuards;
import org.truffleruby.language.RubyRootNode;
import org.truffleruby.language.arguments.ReadCallerFrameNode;
import org.truffleruby.language.arguments.RubyArguments;
import org.truffleruby.language.methods.DeclarationContext;
import org.truffleruby.language.methods.InternalMethod;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.InvalidAssumptionException;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.utilities.AlwaysValidAssumption;

public abstract class CachedDispatchNode extends DispatchNode {

    protected static enum SendsFrame {
        NO_FRAME,
        MY_FRAME,
        CALLER_FRAME;
    }

    private final Object cachedName;
    private final DynamicObject cachedNameAsSymbol;

    @Child protected DispatchNode next;

    private final BranchProfile moreThanReferenceCompare = BranchProfile.create();
    @CompilationFinal protected SendsFrame sendsFrame = SendsFrame.NO_FRAME;
    @Child private ReadCallerFrameNode readCaller;
    @CompilationFinal private Assumption needsCallerAssumption;

    public CachedDispatchNode(
            RubyContext context,
            Object cachedName,
            DispatchNode next,
            DispatchAction dispatchAction) {
        super(dispatchAction);

        assert (cachedName instanceof String) || (RubyGuards.isRubySymbol(cachedName)) || (RubyGuards.isRubyString(cachedName));
        this.cachedName = cachedName;

        if (RubyGuards.isRubySymbol(cachedName)) {
            cachedNameAsSymbol = (DynamicObject) cachedName;
        } else if (RubyGuards.isRubyString(cachedName)) {
            cachedNameAsSymbol = context.getSymbolTable().getSymbol(StringOperations.rope((DynamicObject) cachedName));
        } else if (cachedName instanceof String) {
            cachedNameAsSymbol = context.getSymbolTable().getSymbol((String) cachedName);
        } else {
            throw new UnsupportedOperationException();
        }

        this.next = next;
    }

    private void resetNeedsCallerAssumption() {
        Node root = getRootNode();
        if (root instanceof RubyRootNode && !sendingFrames()) {
            needsCallerAssumption = ((RubyRootNode) root).getNeedsCallerAssumption();
        } else {
            needsCallerAssumption = AlwaysValidAssumption.INSTANCE;
        }
    }

    protected boolean sendingFrames() {
        return sendsFrame != SendsFrame.NO_FRAME;
    }

    public void startSendingOwnFrame() {
        assert sendsFrame != SendsFrame.CALLER_FRAME;
        if (sendsFrame == SendsFrame.NO_FRAME) {
            startSendingFrame(SendsFrame.MY_FRAME);
        }
    }

    public void startSendingCallerFrame(CallerFrameAccess access) {
        assert sendsFrame != SendsFrame.MY_FRAME;
        if (sendsFrame == SendsFrame.NO_FRAME) {
            startSendingFrame(SendsFrame.CALLER_FRAME);
        }
    }

    private void startSendingFrame(SendsFrame frameToSend) {
        assert needsCallerAssumption != AlwaysValidAssumption.INSTANCE;
        this.sendsFrame = frameToSend;
        if (frameToSend == SendsFrame.CALLER_FRAME) {
            this.readCaller = new ReadCallerFrameNode(CallerFrameAccess.MATERIALIZE);
        }
        Node root = getRootNode();
        if (root instanceof RubyRootNode) {
            ((RubyRootNode) root).replaceAndInvalidateNeedsCallerAssumption(needsCallerAssumption);
        } else {
            throw new Error();
        }
    }

    @Override
    protected DispatchNode getNext() {
        return next;
    }

    @ExplodeLoop
    protected static void checkAssumptions(Assumption[] assumptions) throws InvalidAssumptionException {
        for (Assumption assumption : assumptions) {
            CompilerAsserts.compilationConstant(assumption);
            assumption.check();
        }
    }

    protected final boolean guardName(Object methodName) {
        if (cachedName == methodName) {
            return true;
        }

        moreThanReferenceCompare.enter();

        if (cachedName instanceof String) {
            return cachedName.equals(methodName);
        } else if (RubyGuards.isRubySymbol(cachedName)) {
            // TODO(CS, 11-Jan-15) this just repeats the above guard...
            return cachedName == methodName;
        } else if (RubyGuards.isRubyString(cachedName)) {
            return (RubyGuards.isRubyString(methodName)) && StringOperations.rope((DynamicObject) cachedName).equals(StringOperations.rope((DynamicObject) methodName));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    protected DynamicObject getCachedNameAsSymbol() {
        return cachedNameAsSymbol;
    }

    protected abstract void reassessSplittingInliningStrategy();

    protected void applySplittingInliningStrategy(DirectCallNode callNode, InternalMethod method) {
        if (callNode.isCallTargetCloningAllowed() && method.getSharedMethodInfo().shouldAlwaysClone()) {
            insert(callNode);
            callNode.cloneCallTarget();
        }

        if (sendingFrames() && getContext().getOptions().INLINE_NEEDS_CALLER_FRAME && callNode.isInlinable()) {
            callNode.forceInlining();
        }
    }

    protected Object call(DirectCallNode callNode, VirtualFrame frame, InternalMethod method, Object receiver, DynamicObject block, Object[] arguments) {
        if (needsCallerAssumption == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            resetNeedsCallerAssumption();
        }
        try {
            needsCallerAssumption.check();
        } catch (InvalidAssumptionException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            return resetAndCa1ll(
                    callNode,
                    frame,
                    method,
                    receiver,
                    block,
                    arguments);
        }
        return callWithoutAssumption(callNode, frame, method, receiver, block, arguments);
    }

    private Object callWithoutAssumption(DirectCallNode callNode, VirtualFrame frame, InternalMethod method, Object receiver, DynamicObject block, Object[] arguments) {
        MaterializedFrame callerFrame = getFrameIfRequired(frame);
        return callNode.call(RubyArguments.pack(null, callerFrame, method, DeclarationContext.METHOD, null, receiver, block, arguments));
    }

    private Object resetAndCa1ll(DirectCallNode callNode, VirtualFrame frame, InternalMethod method, Object receiver, DynamicObject block, Object[] arguments) {
        resetNeedsCallerAssumption();
        reassessSplittingInliningStrategy();
        return callWithoutAssumption(callNode, frame, method, receiver, block, arguments);
    }

    private MaterializedFrame getFrameIfRequired(VirtualFrame frame) {
        switch (sendsFrame) {
            case MY_FRAME:
                return frame.materialize();
            case CALLER_FRAME:
                return readCaller.execute(frame).materialize();
            default:
                return null;
        }
    }
}
