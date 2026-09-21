package dev.minceraft.sonus.web.pion.ipc;
// Created by booky10 in Sonus (6:38 PM 06.03.2026)

import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.common.util.codec.VarInt;
import dev.minceraft.sonus.web.pion.ipc.model.IceServer;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NullMarked
public final class IpcTypes {

    private IpcTypes() {
    }

    public static <T extends Enum<?>> T readEnum(ByteBuf buf, Class<T> clazz) {
        return readEnum(buf, clazz.getEnumConstants());
    }

    public static <T extends Enum<?>> T readEnum(ByteBuf buf, T[] values) {
        return values[VarInt.read(buf)];
    }

    public static void writeEnum(ByteBuf buf, Enum<?> val) {
        VarInt.write(buf, val.ordinal());
    }

    public static IceServer readIceServer(ByteBuf buf) {
        return new IceServer(
                Utf8String.read(buf),
                readNullable(buf, Utf8String::read),
                readNullable(buf, Utf8String::read)
        );
    }

    public static void writeIceServer(ByteBuf buf, IceServer iceServer) {
        Utf8String.write(buf, iceServer.url());
        writeNullable(buf, iceServer.user(), Utf8String::write);
        writeNullable(buf, iceServer.auth(), Utf8String::write);
    }

    public static <T> @Nullable T readNullable(ByteBuf buf, Decoder<T> decoder) {
        return buf.readBoolean() ? decoder.decode(buf) : null;
    }

    public static <T> void writeNullable(ByteBuf buf, @Nullable T val, Encoder<T> encoder) {
        if (val != null) {
            buf.writeBoolean(true);
            encoder.encode(buf, val);
        } else {
            buf.writeBoolean(false);
        }
    }

    public static <T> List<T> readList(ByteBuf buf, Decoder<T> decoder) {
        int len = VarInt.read(buf);
        List<T> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            list.add(decoder.decode(buf));
        }
        return list;
    }

    public static <T> void writeCollection(ByteBuf buf, Collection<T> collection, Encoder<T> encoder) {
        VarInt.write(buf, collection.size());
        for (T element : collection) {
            encoder.encode(buf, element);
        }
    }

    @FunctionalInterface
    public interface Decoder<T> {

        T decode(ByteBuf buf);
    }

    @FunctionalInterface
    public interface Encoder<T> {

        void encode(ByteBuf buf, T value);
    }
}
