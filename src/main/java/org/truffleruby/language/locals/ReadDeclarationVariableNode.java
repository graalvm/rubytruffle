/*
 * Copyright (c) 2013, 2017 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.truffleruby.language.locals;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.truffleruby.language.RubyNode;
import org.truffleruby.language.arguments.RubyArguments;
import org.truffleruby.parser.ReadLocalNode;

public class ReadDeclarationVariableNode extends ReadLocalNode {

    private final LocalVariableType type;
    private final int frameDepth;
    private final FrameSlot frameSlot;

    @Child private ReadFrameSlotNode readFrameSlotNode;

    public ReadDeclarationVariableNode(LocalVariableType type,
                                       int frameDepth, FrameSlot frameSlot) {
        this.type = type;
        this.frameDepth = frameDepth;
        this.frameSlot = frameSlot;
    }

    public int getFrameDepth() {
        return frameDepth;
    }

    public FrameSlot getFrameSlot() {
        return frameSlot;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return readFrameSlot(frame);
    }

    @Override
    public Object isDefined(VirtualFrame frame) {
        switch (type) {
            case FRAME_LOCAL:
                return coreStrings().LOCAL_VARIABLE.createInstance();

            case FRAME_LOCAL_GLOBAL:
                if (readFrameSlot(frame) != nil()) {
                    return coreStrings().GLOBAL_VARIABLE.createInstance();
                } else {
                    return nil();
                }

            default:
                throw new UnsupportedOperationException("didn't expect local type " + type);
        }
    }

    private Object readFrameSlot(VirtualFrame frame) {
        if (readFrameSlotNode == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            readFrameSlotNode = insert(ReadFrameSlotNodeGen.create(frameSlot));
        }

        final MaterializedFrame declarationFrame = RubyArguments.getDeclarationFrame(frame, frameDepth);
        return readFrameSlotNode.executeRead(declarationFrame);
    }

    @Override
    public RubyNode makeWriteNode(RubyNode rhs) {
        return new WriteDeclarationVariableNode(frameSlot, frameDepth, rhs);
    }

}
