package dev.minceraft.sonus.plasmo.protocol.udp.serverbound;


import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.plasmo.protocol.udp.UdpHandler;
import dev.minceraft.sonus.plasmo.protocol.udp.bothbound.BaseAudioPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class PlayerAudioPlasmoPacket extends BaseAudioPlasmoPacket<PlayerAudioPlasmoPacket> {

    private @MonotonicNonNull UUID activationId;
    private short distance;
    private boolean stereo;

    public PlayerAudioPlasmoPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        super.encode(buf);
        DataTypeUtil.writeUniqueId(buf, this.activationId);
        buf.writeShort(this.distance);
        buf.writeBoolean(this.stereo);
    }

    @Override
    public void decode(ByteBuf buf) {
        super.decode(buf);
        this.activationId = DataTypeUtil.readUniqueId(buf);
        this.distance = buf.readShort();
        this.stereo = buf.readBoolean();
    }

    @Override
    public void handle(UdpHandler handler) {
        handler.handlePlayerAudioPacket(this);
    }

    public UUID getActivationId() {
        return this.activationId;
    }

    public void setActivationId(UUID activationId) {
        this.activationId = activationId;
    }

    public short getDistance() {
        return this.distance;
    }

    public void setDistance(short distance) {
        this.distance = distance;
    }

    public boolean isStereo() {
        return this.stereo;
    }

    public void setStereo(boolean stereo) {
        this.stereo = stereo;
    }
}
