package dev.minceraft.sonus.common.util.codec;
// Created by booky10 in Sonus (01:19 17.07.2025)

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import org.jspecify.annotations.NullMarked;

import java.nio.charset.StandardCharsets;

// copied from https://github.com/PaperMC/Velocity/blob/81deb1fff82957705108755f420be621c9ba4f8f/proxy/src/main/java/com/velocitypowered/proxy/protocol/ProtocolUtils.java#L256-L298
@NullMarked
public class Utf8String {

    private static final int DEFAULT_MAX_STRING_SIZE = 32767;

    private Utf8String() {
    }

    private static void checkFrame(boolean state, String message, Object... args) {
        if (!state) {
            throw new IllegalStateException(message.formatted(args));
        }
    }

    public static String read(ByteBuf buf) {
        return read(buf, DEFAULT_MAX_STRING_SIZE);
    }

    /**
     * Reads a VarInt length-prefixed UTF-8 string from the {@code buf}, making sure to not go over
     * {@code cap} size.
     *
     * @param buf the buffer to read from
     * @param cap the maximum size of the string, in UTF-8 character length
     * @return the decoded string
     */
    public static String read(ByteBuf buf, int cap) {
        int length = dev.minceraft.sonus.common.util.codec.VarInt.read(buf);
        return read(buf, cap, length);
    }

    public static String readUnsignedShort(ByteBuf buf) {
        return readUnsignedShort(buf, DEFAULT_MAX_STRING_SIZE);
    }

    public static String readUnsignedShort(ByteBuf buf, int limit) {
        int length = buf.readUnsignedShort();
        return read(buf, limit, length);
    }

    private static String read(ByteBuf buf, int cap, int length) {
        checkFrame(length >= 0, "Got a negative-length string (%s)", length);
        // `cap` is interpreted as a UTF-8 character length. To cover the full Unicode plane, we must
        // consider the length of a UTF-8 character, which can be up to 3 bytes. We do an initial
        // sanity check and then check again to make sure our optimistic guess was good.
        checkFrame(length <= cap * 3, "Bad string size (got %s, maximum is %s)", length, cap);
        checkFrame(buf.isReadable(length),
                "Trying to read a string that is too long (wanted %s, only have %s)", length,
                buf.readableBytes());
        String str = buf.toString(buf.readerIndex(), length, StandardCharsets.UTF_8);
        buf.skipBytes(length);
        checkFrame(str.length() <= cap, "Got a too-long string (got %s, max %s)", str.length(), cap);
        return str;
    }

    /**
     * Writes the specified {@code str} to the {@code buf} with a VarInt prefix.
     *
     * @param buf the buffer to write to
     * @param str the string to write
     */
    public static void write(ByteBuf buf, CharSequence str) {
        int size = ByteBufUtil.utf8Bytes(str);
        dev.minceraft.sonus.common.util.codec.VarInt.write(buf, size);
        buf.writeCharSequence(str, StandardCharsets.UTF_8);
    }

    public static void writeUnsignedShort(ByteBuf buf, String str) {
        int size = ByteBufUtil.utf8Bytes(str);
        if (size > 65535) {
            throw new IllegalArgumentException("String is too long to fit in an unsigned short: " + size);
        }
        buf.writeShort(size);
        buf.writeCharSequence(str, StandardCharsets.UTF_8);
    }
}
