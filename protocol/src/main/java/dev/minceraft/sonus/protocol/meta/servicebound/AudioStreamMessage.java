package dev.minceraft.sonus.protocol.meta.servicebound;
// Created by booky10 in Sonus (00:05 17.11.2025)

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.VarInt;
import dev.minceraft.sonus.common.util.codec.VarLong;
import dev.minceraft.sonus.protocol.meta.IMetaHandler;
import dev.minceraft.sonus.protocol.meta.IMetaMessage;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NullMarked
public class AudioStreamMessage implements IMetaMessage {

    private @MonotonicNonNull UUID playerId;
    private @MonotonicNonNull UUID channelId;
    private @Nullable UUID categoryId;
    private @MonotonicNonNull List<Frame> frames;

    @Override
    public void encode(ByteBuf buf) {
        DataTypeUtil.writeUniqueId(buf, this.playerId);
        DataTypeUtil.writeUniqueId(buf, this.channelId);
        DataTypeUtil.writeNullable(buf, this.categoryId, DataTypeUtil::writeUniqueId);
        DataTypeUtil.VAR_INT.writeCollection(buf, this.frames, Frame::encode);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.playerId = DataTypeUtil.readUniqueId(buf);
        this.channelId = DataTypeUtil.readUniqueId(buf);
        this.categoryId = DataTypeUtil.readNullable(buf, DataTypeUtil::readUniqueId);
        this.frames = DataTypeUtil.VAR_INT.readCollection(buf, Frame::decode, ArrayList::new);
    }

    @Override
    public void handle(IMetaHandler handler) {
        handler.handleAudioStream(this);
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getChannelId() {
        return this.channelId;
    }

    public void setChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public @Nullable UUID getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(@Nullable UUID categoryId) {
        this.categoryId = categoryId;
    }

    public List<Frame> getFrames() {
        return this.frames;
    }

    public void setFrames(List<Frame> frames) {
        this.frames = frames;
    }

    public record Frame(byte[] data, long sequence) {

        public static void encode(ByteBuf buf, Frame frame) {
            VarInt.write(buf, frame.data.length);
            buf.writeBytes(frame.data);
            VarLong.write(buf, frame.sequence);
        }

        public static Frame decode(ByteBuf buf) {
            byte[] data = new byte[VarInt.read(buf)];
            buf.readBytes(data);
            long sequence = VarLong.read(buf);
            return new Frame(data, sequence);
        }
    }
}
