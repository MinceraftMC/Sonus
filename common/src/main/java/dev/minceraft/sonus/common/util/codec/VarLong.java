package dev.minceraft.sonus.common.util.codec;
// Created by booky10 in Sonus (00:09 17.11.2025)

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jspecify.annotations.NullMarked;

// inspired by from https://github.com/PaperMC/Velocity/blob/81deb1fff82957705108755f420be621c9ba4f8f/proxy/src/main/java/com/velocitypowered/proxy/protocol/ProtocolUtils.java#L158-L254
@NullMarked
public final class VarLong {

    private static final int MAXIMUM_VARLONG_SIZE = 10;
    private static final int[] VAR_LONG_EXACT_BYTE_LENGTHS = new int[64 + 1];

    static {
        initVarLongLengths();
    }

    private VarLong() {
    }

    @SuppressWarnings("MagicNumber")
    private static void initVarLongLengths() {
        for (int i = 0; i < VAR_LONG_EXACT_BYTE_LENGTHS.length; ++i) {
            VAR_LONG_EXACT_BYTE_LENGTHS[i] = (int) Math.ceil((63d - (i - 1)) / 7d);
        }
        // special case for the number 0
        VAR_LONG_EXACT_BYTE_LENGTHS[64] = 1;
    }

    public static ByteBuf buffer(long value) {
        int size = size(value);
        ByteBuf buf = Unpooled.wrappedBuffer(new byte[size]);
        buf.resetWriterIndex();
        write(buf, value);
        return buf;
    }

    static int size(long value) {
        return VAR_LONG_EXACT_BYTE_LENGTHS[Long.numberOfLeadingZeros(value)];
    }

    /**
     * Reads a Minecraft-style VarLong from the specified {@code buf}.
     *
     * @param buf the buffer to read from
     * @return the decoded VarLong
     */
    public static long read(ByteBuf buf) {
        int readable = buf.readableBytes();
        if (readable == 0) {
            // special case for empty buffer
            throw new IllegalStateException("Bad VarLong decoded");
        }

        // we can read at least one byte, and this should be a common case
        int k = buf.readByte();
        if ((k & 0x80) != 128) {
            return k;
        }

        // in case decoding one byte was not enough, use a loop to decode up to the next 8 bytes
        int maxRead = Math.min(MAXIMUM_VARLONG_SIZE, readable);
        long l = k & 0x7F;
        for (int i = 1; i < maxRead; i++) {
            k = buf.readByte();
            l |= ((long) (k & 0x7F)) << i * 7L;
            if ((k & 0x80) != 128) {
                return l;
            }
        }
        throw new IllegalStateException("Bad VarLong decoded");
    }

    /**
     * Writes a Minecraft-style VarLong to the specified {@code buf}.
     *
     * @param buf   the buffer to read from
     * @param value the long to write
     */
    public static void write(ByteBuf buf, long value) {
        // Peel the one and two byte count cases explicitly as they are the most common VarLong sizes
        // that the proxy will write, to improve inlining.
        if ((value & (0xFFFFFFFFFFFFFFFFL << 7L)) == 0L) {
            buf.writeByte((int) value);
        } else if ((value & (0xFFFFFFFFFFFFFFFFL << 14L)) == 0L) {
            int w = (int) ((value & 0x7F | 0x80) << 8 | (value >>> 7));
            buf.writeShort(w);
        } else {
            writeFull(buf, value);
        }
    }

    private static void writeFull(ByteBuf buf, long value) {
        while ((value & ~0x7FL) != 0L) {
            buf.writeByte((int) (value & 0x7FL) | 0x80);
            value >>>= 7;
        }
        buf.writeByte((int) value);
    }
}
