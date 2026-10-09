package dev.minceraft.sonus.web.pion.ipc.sonusbound;
// Created by booky10 in Sonus (8:37 PM 06.03.2026)

import dev.minceraft.sonus.common.util.codec.VarInt;
import dev.minceraft.sonus.web.pion.ipc.IpcMessage;
import dev.minceraft.sonus.web.pion.ipc.IpcTypes;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class IpcPeerOnAudioTrack extends IpcMessage {

    private final Type type;
    private final int trackId;
    private final int sampleRate;
    private final short channels;

    public IpcPeerOnAudioTrack(ByteBuf buf) {
        this(
                VarInt.read(buf), IpcTypes.readEnum(buf, Type.TYPES),
                VarInt.read(buf), VarInt.read(buf), buf.readShort()
        );
    }

    public IpcPeerOnAudioTrack(int handlerId, Type type, int trackId, int sampleRate, short channels) {
        super(handlerId);
        this.type = type;
        this.trackId = trackId;
        this.sampleRate = sampleRate;
        this.channels = channels;
    }

    @Override
    public void encode(ByteBuf buf) {
        throw new UnsupportedOperationException();
    }

    public int getTrackId() {
        return this.trackId;
    }

    public Type getType() {
        return this.type;
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public short getChannels() {
        return this.channels;
    }

    public enum Type {

        LOCAL,
        REMOTE,
        ;

        private static final Type[] TYPES = values();
    }
}
