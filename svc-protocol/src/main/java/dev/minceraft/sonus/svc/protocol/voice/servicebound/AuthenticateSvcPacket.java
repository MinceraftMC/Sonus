package dev.minceraft.sonus.svc.protocol.voice.servicebound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.svc.protocol.SvcPacketContext;
import dev.minceraft.sonus.svc.protocol.voice.IVoiceSvcHandler;
import dev.minceraft.sonus.svc.protocol.voice.SvcVoicePacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class AuthenticateSvcPacket extends SvcVoicePacket {

    private @MonotonicNonNull UUID playerId;
    private @MonotonicNonNull UUID secret;

    public AuthenticateSvcPacket() {
    }

    @Override
    public void encode(ByteBuf buf, SvcPacketContext ctx) {
        DataTypeUtil.writeUniqueId(buf, this.playerId);
        DataTypeUtil.writeUniqueId(buf, this.secret);
    }

    @Override
    public void decode(ByteBuf buf, SvcPacketContext ctx) {
        this.playerId = DataTypeUtil.readUniqueId(buf);
        this.secret = DataTypeUtil.readUniqueId(buf);
    }

    @Override
    public void handle(IVoiceSvcHandler handler) {
        handler.handleAuthenticate(this);
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getSecret() {
        return this.secret;
    }

    public void setSecret(UUID secret) {
        this.secret = secret;
    }
}
