package dev.minceraft.sonus.plasmo.protocol.tcp.clientbound;


import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AnimatedActionBarPacket extends TcpPlasmoPacket<AnimatedActionBarPacket> {

    private @MonotonicNonNull String jsonComponent;

    public AnimatedActionBarPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        Utf8String.write(buf, this.jsonComponent);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.jsonComponent = Utf8String.readUnsignedShort(buf);
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handleAnimatedActionBarPacket(this);
    }

    public String getJsonComponent() {
        return this.jsonComponent;
    }

    public void setJsonComponent(String jsonComponent) {
        this.jsonComponent = jsonComponent;
    }
}
