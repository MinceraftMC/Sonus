package dev.minceraft.sonus.plasmo.protocol.tcp.data.source;


import dev.minceraft.sonus.common.util.codec.Utf8String;
import io.netty.buffer.ByteBuf;

import java.util.function.Function;

public enum SourceType {

    PLAYER(PlayerSourceInfo::new),
    ENTITY(EntitySourceInfo::new),
    STATIC(StaticSourceInfo::new),
    DIRECT(DirectSourceInfo::new);

    private final Function<ByteBuf, SourceInfo> decoder;

    SourceType(Function<ByteBuf, SourceInfo> decoder) {
        this.decoder = decoder;
    }

    public static SourceInfo decode(ByteBuf buf) {
        String read = Utf8String.readUnsignedShort(buf);
        try {
            SourceType type = valueOf(read);
            return type.decoder.apply(buf);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown source type: " + read);
        }
    }

    public static void encode(ByteBuf buf, SourceInfo info) {
        Utf8String.writeUnsignedShort(buf, info.getSourceType().name());
        info.write(buf);
    }
}
