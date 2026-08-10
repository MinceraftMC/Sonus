package dev.minceraft.sonus.common.util.codec;
// Created by booky10 in Sonus (01:17 17.07.2025)

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.jspecify.annotations.NullMarked;

// copied from https://github.com/PaperMC/Velocity/blob/81deb1fff82957705108755f420be621c9ba4f8f/proxy/src/main/java/com/velocitypowered/proxy/protocol/ProtocolUtils.java#L158-L254
@NullMarked
public final class VarInt {

    private static final int MAXIMUM_VARINT_SIZE = 5;
    private static final int[] VAR_INT_EXACT_BYTE_LENGTHS = new int[33];
    private static final QuietCodecException BAD_VARINT = new QuietCodecException("Bad VarInt decoded");

    static {
        initVarIntLengths();
    }

    private VarInt() {
    }

    @SuppressWarnings("MagicNumber")
    private static void initVarIntLengths() {
        for (int i = 0; i < VAR_INT_EXACT_BYTE_LENGTHS.length; ++i) {
            VAR_INT_EXACT_BYTE_LENGTHS[i] = (int) Math.ceil((31d - (i - 1)) / 7d);
        }
        // special case for the number 0
        VAR_INT_EXACT_BYTE_LENGTHS[32] = 1;
    }

    public static ByteBuf buffer(ByteBufAllocator alloc, int value) {
        ByteBuf buf = alloc.buffer(size(value));
        write(buf, value);
        return buf;
    }

    public static int size(int value) {
        return VAR_INT_EXACT_BYTE_LENGTHS[Integer.numberOfLeadingZeros(value)];
    }

    /**
     * Reads a Minecraft-style VarInt from the specified {@code buf}.
     *
     * @param buf the buffer to read from
     * @return the decoded VarInt
     */
    public static int read(ByteBuf buf) throws QuietCodecException {
        int readable = buf.readableBytes();
        if (readable == 0) {
            // special case for empty buffer
            throw BAD_VARINT;
        }

        // we can read at least one byte, and this should be a common case
        int k = buf.readByte();
        if ((k & 0x80) != 128) {
            return k;
        }

        // in case decoding one byte was not enough, use a loop to decode up to the next 4 bytes
        int maxRead = Math.min(MAXIMUM_VARINT_SIZE, readable);
        int i = k & 0x7F;
        for (int j = 1; j < maxRead; j++) {
            k = buf.readByte();
            i |= (k & 0x7F) << j * 7;
            if ((k & 0x80) != 128) {
                return i;
            }
        }
        throw BAD_VARINT;
    }

    /**
     * Writes a Minecraft-style VarInt to the specified {@code buf}.
     *
     * @param buf   the buffer to read from
     * @param value the integer to write
     */
    public static void write(ByteBuf buf, int value) {
        // Peel the one and two byte count cases explicitly as they are the most common VarInt sizes
        // that the proxy will write, to improve inlining.
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buf.writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            int w = (value & 0x7F | 0x80) << 8 | (value >>> 7);
            buf.writeShort(w);
        } else {
            writeFull(buf, value);
        }
    }

    private static void writeFull(ByteBuf buf, int value) {
        // See https://steinborn.me/posts/performance/how-fast-can-you-write-a-varint/

        // This essentially is an unrolled version of the "traditional" VarInt encoding.
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buf.writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            int w = (value & 0x7F | 0x80) << 8 | (value >>> 7);
            buf.writeShort(w);
        } else if ((value & (0xFFFFFFFF << 21)) == 0) {
            int w = (value & 0x7F | 0x80) << 16 | ((value >>> 7) & 0x7F | 0x80) << 8 | (value >>> 14);
            buf.writeMedium(w);
        } else if ((value & (0xFFFFFFFF << 28)) == 0) {
            int w = (value & 0x7F | 0x80) << 24 | (((value >>> 7) & 0x7F | 0x80) << 16)
                    | ((value >>> 14) & 0x7F | 0x80) << 8 | (value >>> 21);
            buf.writeInt(w);
        } else {
            int w = (value & 0x7F | 0x80) << 24 | ((value >>> 7) & 0x7F | 0x80) << 16
                    | ((value >>> 14) & 0x7F | 0x80) << 8 | ((value >>> 21) & 0x7F | 0x80);
            buf.writeInt(w);
            buf.writeByte(value >>> 28);
        }
    }
}
