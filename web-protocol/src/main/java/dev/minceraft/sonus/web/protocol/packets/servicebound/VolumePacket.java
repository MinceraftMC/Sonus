package dev.minceraft.sonus.web.protocol.packets.servicebound;
// Created by booky10 in Sonus (5:22 PM 02.03.2026)

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.VarInt;
import dev.minceraft.sonus.web.protocol.WsPacketContext;
import dev.minceraft.sonus.web.protocol.packets.IWebSocketHandler;
import dev.minceraft.sonus.web.protocol.packets.WebSocketPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class VolumePacket extends WebSocketPacket {

    private @MonotonicNonNull VolumeType type;
    private @MonotonicNonNull UUID entryId;
    private float volume;

    @Override
    public void encode(ByteBuf buf, WsPacketContext context) {
        VarInt.write(buf, this.type.ordinal());
        DataTypeUtil.writeUniqueId(buf, this.entryId);
        buf.writeFloat(this.volume);
    }

    @Override
    public void decode(ByteBuf buf, WsPacketContext context) {
        this.type = VolumeType.TYPES[VarInt.read(buf)];
        this.entryId = DataTypeUtil.readUniqueId(buf);
        this.volume = buf.readFloat();
    }

    @Override
    public void handle(IWebSocketHandler handler) {
        handler.handleVolume(this);
    }

    public VolumeType getType() {
        return this.type;
    }

    public void setType(VolumeType type) {
        this.type = type;
    }

    public UUID getEntryId() {
        return this.entryId;
    }

    public void setEntryId(UUID entryId) {
        this.entryId = entryId;
    }

    public float getVolume() {
        return this.volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public enum VolumeType {

        CATEGORY,
        PLAYER,
        ;

        private static final VolumeType[] TYPES = values();
    }
}
