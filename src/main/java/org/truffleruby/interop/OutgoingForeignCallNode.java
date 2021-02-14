/*
 * Copyright (c) 2014, 2020 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 2.0, or
 * GNU General Public License version 2, or
 * GNU Lesser General Public License version 2.1.
 */
package org.truffleruby.interop;

import java.util.Arrays;

import org.truffleruby.RubyContext;
import org.truffleruby.RubyLanguage;
import org.truffleruby.core.cast.NameToJavaStringNode;
import org.truffleruby.core.cast.ToSymbolNode;
import org.truffleruby.language.RubyBaseNode;
import org.truffleruby.language.control.RaiseException;
import org.truffleruby.language.dispatch.DispatchNode;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Cached.Shared;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.profiles.ConditionProfile;

@GenerateUncached
/* This node is called either with cached name from CachedForeignDispatchNode or from DSLUncachedDispatchNode where it
 * uses uncached version of this node. */
public abstract class OutgoingForeignCallNode extends RubyBaseNode {

    // TODO (pitr-ch 01-Apr-2019): support to_int special form with new interop, consider others
    // TODO (pitr-ch 16-Sep-2019): merge into a dispatch node when it is migrated to DSL
    // FIXME (pitr 13-Sep-2019): @Shared("arity") does not work, It thinks "The cache initializer does not match"

    public abstract Object executeCall(Object receiver, String name, Object[] args);

    protected final static String INDEX_READ = "[]";
    protected final static String CALL = "call";
    protected final static String NEW = "new";
    protected final static String SEND = "__send__";

    @Specialization(
            guards = {
                    "name == cachedName",
                    "cachedName.equals(INDEX_READ)",
                    "args.length == 1",
                    "isBasicInteger(first(args))" },
            limit = "1")
    protected Object readArrayElement(Object receiver, String name, Object[] args,
            @Cached(value = "name", allowUncached = true) @Shared("name") String cachedName,
            @Cached InteropNodes.ReadArrayElementNode readNode) {
        return readNode.execute(receiver, args[0]);
    }

    @Specialization(
            guards = {
                    "name == cachedName",
                    "cachedName.equals(INDEX_READ)",
                    "args.length == 1",
                    "isRubySymbolOrString(first(args))" },
            limit = "1")
    protected Object readMember(Object receiver, String name, Object[] args,
            @Cached(value = "name", allowUncached = true) @Shared("name") String cachedName,
            @Cached InteropNodes.ReadMemberNode readNode) {
        return readNode.execute(receiver, args[0]);
    }

    protected static Object first(Object[] args) {
        return args[0];
    }

    @Specialization(guards = { "name == cachedName", "cachedName.equals(CALL)" }, limit = "1")
    protected Object call(Object receiver, String name, Object[] args,
            @Cached(value = "name", allowUncached = true) @Shared("name") String cachedName,
            @Cached InteropNodes.ExecuteNode executeNode) {
        return executeNode.execute(receiver, args);
    }

    @Specialization(guards = { "name == cachedName", "cachedName.equals(NEW)" }, limit = "1")
    protected Object newOutgoing(Object receiver, String name, Object[] args,
            @Cached(value = "name", allowUncached = true) @Shared("name") String cachedName,
            @Cached InteropNodes.InstantiateNode newNode) {
        return newNode.execute(receiver, args);
    }

    @Specialization(guards = { "name == cachedName", "cachedName.equals(SEND)", "args.length >= 1" }, limit = "1")
    protected Object sendOutgoing(Object receiver, String name, Object[] args,
            @Cached(value = "name", allowUncached = true) @Shared("name") String cachedName,
            @Cached DispatchNode dispatchNode,
            @Cached NameToJavaStringNode nameToJavaString) {

        final Object sendName = args[0];
        final Object[] sendArgs = Arrays.copyOfRange(args, 1, args.length);

        return dispatchNode.dispatch(null, receiver, nameToJavaString.execute(sendName), nil, sendArgs);
    }

    @TruffleBoundary
    protected static int expectedArity(String name) {
        switch (name) {
            case INDEX_READ:
            case SEND:
                return 1;
            default:
                throw new IllegalStateException();
        }
    }

    @Specialization(guards = { "name == cachedName", "isOperatorMethod(cachedName)" }, limit = "1")
    protected Object operator(Object receiver, String name, Object[] args,
            @Cached(value = "name", allowUncached = true) @Shared("name") String cachedName,
            @Cached PrimitiveConversionForOperatorAndReDispatchOutgoingNode node) {
        return node.executeCall(receiver, name, args);
    }

    @Specialization(guards = {
            "name == cachedName",
            "!isOperatorMethod(cachedName)",
            "isAssignmentMethod(cachedName)",
            "args.length != 1"
    }, limit = "1")
    protected Object assignmentBadArgs(Object receiver, String name, Object[] args,
            @Cached(value = "name", allowUncached = true) @Shared("name") String cachedName,
            @CachedContext(RubyLanguage.class) RubyContext context) {
        throw new RaiseException(
                context,
                context.getCoreExceptions().argumentError(args.length, 1, this));
    }

    @Specialization(guards = {
            "name == cachedName",
            "!isOperatorMethod(cachedName)",
            "isAssignmentMethod(cachedName)",
            "args.length == 1"
    }, limit = "1")
    protected Object assignment(Object receiver, String name, Object[] args,
            @Cached(value = "name", allowUncached = true) @Shared("name") String cachedName,
            @Cached(value = "getPropertyFromName(name)", allowUncached = true) String propertyName,
            @CachedLibrary("receiver") InteropLibrary receivers,
            @Cached TranslateInteropExceptionNode translateInteropException) {
        try {
            receivers.writeMember(receiver, propertyName, args[0]);
        } catch (InteropException e) {
            throw translateInteropException.execute(e);
        }
        return args[0];
    }

    @Specialization(
            guards = {
                    "name == cachedName",
                    "!cachedName.equals(INDEX_READ)",
                    "!cachedName.equals(CALL)",
                    "!cachedName.equals(NEW)",
                    "!cachedName.equals(SEND)",
                    "!isOperatorMethod(cachedName)",
                    "!isAssignmentMethod(cachedName)",
                    "args.length == 0"
            },
            limit = "1")
    protected Object readOrInvoke(Object receiver, String name, Object[] args,
            @Cached(value = "name", allowUncached = true) @Shared("name") String cachedName,
            @Cached ToSymbolNode toSymbolNode,
            @Cached InteropNodes.InvokeMemberNode invokeNode,
            @Cached InteropNodes.ReadMemberNode readNode,
            @Cached ConditionProfile invocable,
            @CachedLibrary("receiver") InteropLibrary receivers) {
        if (invocable.profile(receivers.isMemberInvocable(receiver, name))) {
            return invokeNode.execute(receiver, name, args);
        } else {
            return readNode.execute(receiver, toSymbolNode.execute(cachedName));
        }
    }

    @Specialization(
            guards = {
                    "name == cachedName",
                    "!cachedName.equals(INDEX_READ)",
                    "!cachedName.equals(CALL)",
                    "!cachedName.equals(NEW)",
                    "!cachedName.equals(SEND)",
                    "!isOperatorMethod(cachedName)",
                    "!isAssignmentMethod(cachedName)",
                    "args.length != 0"
            },
            limit = "1")
    protected Object notOperatorOrAssignment(Object receiver, String name, Object[] args,
            @Cached(value = "name", allowUncached = true) @Shared("name") String cachedName,
            @Cached InteropNodes.InvokeMemberNode invokeNode) {
        return invokeNode.execute(receiver, name, args);
    }

    @TruffleBoundary
    protected static boolean isOperatorMethod(String name) {
        return !name.isEmpty() && !Character.isLetter(name.charAt(0));
    }

    @TruffleBoundary
    protected static boolean isAssignmentMethod(String name) {
        return !name.isEmpty() && '=' == name.charAt(name.length() - 1);
    }

    protected static String getPropertyFromName(String name) {
        return name.substring(0, name.length() - 1);
    }

    @GenerateUncached
    protected abstract static class PrimitiveConversionForOperatorAndReDispatchOutgoingNode
            extends
            RubyBaseNode {

        protected int getCacheLimit() {
            return RubyLanguage.getCurrentLanguage().options.METHOD_LOOKUP_CACHE;
        }

        protected abstract Object executeCall(Object receiver, String name, Object[] args);

        @Specialization(guards = "receivers.isBoolean(receiver)", limit = "getCacheLimit()")
        protected Object callBoolean(Object receiver, String name, Object[] args,
                @CachedLibrary("receiver") InteropLibrary receivers,
                @Cached TranslateInteropExceptionNode translateInteropException,
                @Cached DispatchNode dispatch) {
            try {
                return dispatch.call(receivers.asBoolean(receiver), name, args);
            } catch (InteropException e) {
                throw translateInteropException.execute(e);
            }
        }

        @Specialization(guards = "receivers.isString(receiver)", limit = "getCacheLimit()")
        protected Object callString(Object receiver, String name, Object[] args,
                @Cached ForeignToRubyNode foreignToRubyNode,
                @CachedLibrary("receiver") InteropLibrary receivers,
                @Cached TranslateInteropExceptionNode translateInteropException,
                @Cached DispatchNode dispatch) {
            try {
                Object rubyString = foreignToRubyNode.executeConvert(receivers.asString(receiver));
                return dispatch.call(rubyString, name, args);
            } catch (InteropException e) {
                throw translateInteropException.execute(e);
            }
        }

        @Specialization(
                guards = { "receivers.isNumber(receiver)", "receivers.fitsInInt(receiver)" },
                limit = "getCacheLimit()")
        protected Object callInt(Object receiver, String name, Object[] args,
                @CachedLibrary("receiver") InteropLibrary receivers,
                @Cached TranslateInteropExceptionNode translateInteropException,
                @Cached DispatchNode dispatch) {
            try {
                return dispatch.call(receivers.asInt(receiver), name, args);
            } catch (InteropException e) {
                throw translateInteropException.execute(e);
            }
        }

        @Specialization(
                guards = {
                        "receivers.isNumber(receiver)",
                        "!receivers.fitsInInt(receiver)",
                        "receivers.fitsInLong(receiver)" },
                limit = "getCacheLimit()")
        protected Object callLong(Object receiver, String name, Object[] args,
                @CachedLibrary("receiver") InteropLibrary receivers,
                @Cached TranslateInteropExceptionNode translateInteropException,
                @Cached DispatchNode dispatch) {
            try {
                return dispatch.call(receivers.asLong(receiver), name, args);
            } catch (InteropException e) {
                throw translateInteropException.execute(e);
            }
        }

        @Specialization(
                guards = {
                        "receivers.isNumber(receiver)",
                        "!receivers.fitsInLong(receiver)",
                        "receivers.fitsInDouble(receiver)" },
                limit = "getCacheLimit()")
        protected Object callDouble(Object receiver, String name, Object[] args,
                @CachedLibrary("receiver") InteropLibrary receivers,
                @Cached TranslateInteropExceptionNode translateInteropException,
                @Cached DispatchNode dispatch) {
            try {
                return dispatch.call(receivers.asDouble(receiver), name, args);
            } catch (InteropException e) {
                throw translateInteropException.execute(e);
            }
        }

        @Specialization(
                guards = {
                        "!receivers.isBoolean(receiver)",
                        "!receivers.isString(receiver)",
                        "!receivers.isNumber(receiver)" },
                limit = "getCacheLimit()")
        protected Object call(Object receiver, String name, Object[] args,
                @CachedLibrary("receiver") InteropLibrary receivers,
                @Cached InteropNodes.InvokeMemberNode invokeNode) {
            return invokeNode.execute(receiver, name, args);
        }

    }

}
