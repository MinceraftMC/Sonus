package dev.minceraft.sonus.plasmo.protocol.udp.bothbound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.protocol.util.PacketDirection;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.plasmo.protocol.udp.UdpHandler;
import dev.minceraft.sonus.plasmo.protocol.udp.UdpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CustomPlasmoPacket extends UdpPlasmoPacket<CustomPlasmoPacket> {

    private @MonotonicNonNull PacketDirection direction;
    private @MonotonicNonNull String addonId;
    private byte @MonotonicNonNull [] payload;

    public CustomPlasmoPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        Utf8String.writeUnsignedShort(buf, this.addonId);
        DataTypeUtil.INT.writeByteArray(buf, this.payload);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.addonId = Utf8String.readUnsignedShort(buf);
        this.payload = DataTypeUtil.INT.readByteArray(buf);
    }

    @Override
    public void handle(UdpHandler handler) {
        handler.handleCustomPacket(this);
    }

    public PacketDirection getDirection() {
        return this.direction;
    }

    public void setDirection(PacketDirection direction) {
        this.direction = direction;
    }

    public String getAddonId() {
        return this.addonId;
    }

    public void setAddonId(String addonId) {
        this.addonId = addonId;
    }

    public byte[] getPayload() {
        return this.payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
