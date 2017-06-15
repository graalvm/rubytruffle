/*
 * Copyright (c) 2015, 2016 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.truffleruby.core.rope;

import org.jcodings.Encoding;
import org.truffleruby.core.Hashing;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import java.util.Arrays;

public abstract class Rope {

    private final Encoding encoding;
    private final CodeRange codeRange;
    private final boolean singleByteOptimizable;
    private final int byteLength;
    private final int characterLength;
    private final int ropeDepth;
    private int hashCode = 0;
    protected byte[] bytes;

    protected Rope(Encoding encoding, CodeRange codeRange, boolean singleByteOptimizable, int byteLength, int characterLength, int ropeDepth, byte[] bytes) {
        assert encoding != null;

        this.encoding = encoding;
        this.codeRange = codeRange;
        this.singleByteOptimizable = singleByteOptimizable;
        this.byteLength = byteLength;
        this.characterLength = characterLength;
        this.ropeDepth = ropeDepth;
        this.bytes = bytes;
    }

    public abstract Rope withEncoding(Encoding newEncoding, CodeRange newCodeRange);

    public final int characterLength() {
        return characterLength;
    }

    public final int byteLength() {
        return byteLength;
    }

    public final boolean isEmpty() {
        return byteLength == 0;
    }

    protected abstract byte getByteSlow(int index);

    public final byte[] getRawBytes() {
        return bytes;
    }

    public abstract byte[] getBytes();

    protected byte[] getBytesSlow() {
        return RopeOperations.flattenBytes(this);
    }

    public final byte[] getBytesCopy() {
        return getBytes().clone();
    }

    public final Encoding getEncoding() {
        return encoding;
    }

    public final CodeRange getCodeRange() {
        return codeRange;
    }

    public final boolean isSingleByteOptimizable() {
        return singleByteOptimizable;
    }

    public final boolean isAsciiOnly() {
        return codeRange == CodeRange.CR_7BIT;
    }

    public final int depth() {
        return ropeDepth;
    }

    protected static final int MURMUR_SEED = System.identityHashCode(Rope.class);

    @Override
    @TruffleBoundary
    public int hashCode() {
        if (!isHashCodeCalculated()) {
            long hash = Hashing.hash(MURMUR_SEED, RopeOperations.hashForRange(this, 1, 0, byteLength));
            hashCode = Long.hashCode(hash);
        }

        return hashCode;
    }

    public final boolean isHashCodeCalculated() {
        return hashCode != 0;
    }

    public final int calculatedHashCode() {
        return hashCode;
    }

    public final boolean hashesMatch(Rope other) {
        return (isHashCodeCalculated() && hashCode == other.hashCode) || (hashCode() == other.hashCode());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof Rope) {
            final Rope other = (Rope) o;

            if (isHashCodeCalculated() && other.isHashCodeCalculated() && (hashCode != other.hashCode)) {
                return false;
            }

            // TODO (nirvdrum 21-Jan-16): We really should be taking the encoding into account here. We're currently not because it breaks the symbol table.
            return byteLength() == other.byteLength() && Arrays.equals(getBytes(), other.getBytes());
        }

        return false;
    }

    public byte get(int index) {
        if (bytes != null) {
            return bytes[index];
        }

        return getByteSlow(index);
    }

}
