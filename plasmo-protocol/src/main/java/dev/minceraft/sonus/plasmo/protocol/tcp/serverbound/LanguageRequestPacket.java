package dev.minceraft.sonus.plasmo.protocol.tcp.serverbound;


import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LanguageRequestPacket extends TcpPlasmoPacket<LanguageRequestPacket> {

    private @MonotonicNonNull String language;

    public LanguageRequestPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        Utf8String.writeUnsignedShort(buf, this.language);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.language = Utf8String.readUnsignedShort(buf, 12);
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handleLanguageRequestPacket(this);
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
