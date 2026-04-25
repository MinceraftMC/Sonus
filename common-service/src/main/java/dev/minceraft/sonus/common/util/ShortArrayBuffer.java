package dev.minceraft.sonus.common.util;

import java.util.Arrays;

// based on https://github.com/henkelmax/lame4j/blob/aa02bca4c389e3c5efe464a57a6741dfc1ddfc4e/src/main/java/de/maxhenkel/lame4j/ShortArrayBuffer.java, LGPL
public final class ShortArrayBuffer {

    public static final int SOFT_MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

    private short[] buf;
    private int count;

    public ShortArrayBuffer(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        this.buf = new short[size];
    }

    private void ensureCapacity(int minCapacity) {
        int oldCapacity = this.buf.length;
        int minGrowth = minCapacity - oldCapacity;
        if (minGrowth > 0) {
            int newLen = newLength(oldCapacity, minGrowth, oldCapacity);
            this.buf = Arrays.copyOf(this.buf, newLen);
        }
    }

    private static int newLength(int oldLength, int minGrowth, int prefGrowth) {
        int prefLength = oldLength + Math.max(minGrowth, prefGrowth);
        if (0 < prefLength && prefLength <= SOFT_MAX_ARRAY_LENGTH) {
            return prefLength;
        } else {
            return hugeLength(oldLength, minGrowth);
        }
    }

    private static int hugeLength(int oldLength, int minGrowth) {
        int minLength = oldLength + minGrowth;
        if (minLength < 0) {
            throw new OutOfMemoryError("Required array length " + oldLength + " + " + minGrowth + " is too large");
        }
        return Math.max(minLength, SOFT_MAX_ARRAY_LENGTH);
    }

    public void write(short[] b, int off, int len) {
        assert off + len <= b.length;
        this.ensureCapacity(this.count + len);
        System.arraycopy(b, off, this.buf, this.count, len);
        this.count += len;
    }

    public void writeShorts(short[] b) {
        this.write(b, 0, b.length);
    }

    public short[] getBuf() {
        return this.buf;
    }
}

