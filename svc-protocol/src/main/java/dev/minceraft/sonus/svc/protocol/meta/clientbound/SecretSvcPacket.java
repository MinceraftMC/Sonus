package dev.minceraft.sonus.svc.protocol.meta.clientbound;


import dev.minceraft.sonus.common.protocol.codec.OpusCodec;
import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.svc.protocol.SvcPacketContext;
import dev.minceraft.sonus.svc.protocol.meta.IMetaSvcHandler;
import dev.minceraft.sonus.svc.protocol.meta.SvcMetaPacket;
import dev.minceraft.sonus.svc.protocol.util.SvcPluginChannels;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class SecretSvcPacket extends SvcMetaPacket {

    private @MonotonicNonNull UUID secret;
    private int serverPort;
    private @MonotonicNonNull UUID playerId;
    private @MonotonicNonNull OpusCodec codec;
    private int mtuSize;
    private double voiceChatDistance;
    private int keepAlive;
    private boolean groupsEnabled;
    private @MonotonicNonNull String voiceHost;
    private boolean allowRecording;

    public SecretSvcPacket() {
        super(SvcPluginChannels.SECRET);
    }

    @Override
    public void encode(ByteBuf buf, SvcPacketContext ctx) {
        DataTypeUtil.writeUniqueId(buf, this.secret);
        buf.writeInt(this.serverPort);
        DataTypeUtil.writeUniqueId(buf, this.playerId);
        buf.writeByte(this.codec.ordinal());
        buf.writeInt(this.mtuSize);
        buf.writeDouble(this.voiceChatDistance);
        buf.writeInt(this.keepAlive);
        buf.writeBoolean(this.groupsEnabled);
        Utf8String.write(buf, this.voiceHost);
        buf.writeBoolean(this.allowRecording);
    }

    @Override
    public void decode(ByteBuf buf, SvcPacketContext ctx) {
        this.secret = DataTypeUtil.readUniqueId(buf);
        this.serverPort = buf.readInt();
        this.playerId = DataTypeUtil.readUniqueId(buf);
        this.codec = OpusCodec.values()[buf.readByte()];
        this.mtuSize = buf.readInt();
        this.voiceChatDistance = buf.readDouble();
        this.keepAlive = buf.readInt();
        this.groupsEnabled = buf.readBoolean();
        this.voiceHost = Utf8String.read(buf);
        this.allowRecording = buf.readBoolean();
    }

    @Override
    public void handle(IMetaSvcHandler handler) {
        handler.handleSecretPacket(this);
    }

    public UUID getSecret() {
        return this.secret;
    }

    public void setSecret(UUID secret) {
        this.secret = secret;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public OpusCodec getCodec() {
        return this.codec;
    }

    public void setCodec(OpusCodec codec) {
        this.codec = codec;
    }

    public int getMtuSize() {
        return this.mtuSize;
    }

    public void setMtuSize(int mtuSize) {
        this.mtuSize = mtuSize;
    }

    public double getVoiceChatDistance() {
        return this.voiceChatDistance;
    }

    public void setVoiceChatDistance(double voiceChatDistance) {
        this.voiceChatDistance = voiceChatDistance;
    }

    public int getKeepAlive() {
        return this.keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isGroupsEnabled() {
        return this.groupsEnabled;
    }

    public void setGroupsEnabled(boolean groupsEnabled) {
        this.groupsEnabled = groupsEnabled;
    }

    public String getVoiceHost() {
        return this.voiceHost;
    }

    public void setVoiceHost(String voiceHost) {
        this.voiceHost = voiceHost;
    }

    public boolean isAllowRecording() {
        return this.allowRecording;
    }

    public void setAllowRecording(boolean allowRecording) {
        this.allowRecording = allowRecording;
    }
}
