/*
 * Copyright (c) 2013, 2017 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.truffleruby.language.arguments;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.BranchProfile;
import org.truffleruby.language.RubyNode;
import org.truffleruby.language.control.RaiseException;
import org.truffleruby.language.methods.Arity;

public class CheckArityNode extends RubyNode {

    private final Arity arity;

    private final BranchProfile checkFailedProfile = BranchProfile.create();

    public CheckArityNode(Arity arity) {
        this.arity = arity;
    }

    @Override
    public void doExecuteVoid(VirtualFrame frame) {
        final int given = RubyArguments.getArgumentsCount(frame);

        if (!checkArity(arity, given)) {
            checkFailedProfile.enter();
            if (arity.getOptional() > 0) {
                throw new RaiseException(coreExceptions().argumentError(given, arity.getRequired(), arity.getOptional(), this));
            } else {
                throw new RaiseException(coreExceptions().argumentError(given, arity.getRequired(), this));
            }
        }
    }

    @Override
    public Object execute(VirtualFrame frame) {
        doExecuteVoid(frame);
        return nil();
    }

    public static boolean checkArity(Arity arity, int given) {
        final int required = arity.getRequired();

        if (required != 0 && given < required) {
            return false;
        }

        if (!arity.hasRest() && given > required + arity.getOptional()) {
            return false;
        }

        return true;
    }

}
