package dev.minceraft.sonus.plasmo.protocol.tcp.clientbound;

import dev.minceraft.sonus.common.protocol.util.DataTypeUtil;
import dev.minceraft.sonus.common.protocol.util.Utf8String;
import dev.minceraft.sonus.common.protocol.util.GameProfile;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class SourceLinePlayerAddPacket extends TcpPlasmoPacket<SourceLinePlayerAddPacket> {

    private @MonotonicNonNull UUID sourceLineId;
    private @MonotonicNonNull GameProfile player;

    public SourceLinePlayerAddPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        DataTypeUtil.writeUniqueId(buf, this.sourceLineId);
        DataTypeUtil.INT.writeGameProfile(buf, this.player, Utf8String::writeUnsignedShort);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.sourceLineId = DataTypeUtil.readUniqueId(buf);
        this.player = DataTypeUtil.INT.readGameProfile(buf, Utf8String::readUnsignedShort);
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handleSourceLinePlayerAddPacket(this);
    }

    public UUID getSourceLineId() {
        return this.sourceLineId;
    }

    public void setSourceLineId(UUID sourceLineId) {
        this.sourceLineId = sourceLineId;
    }

    public GameProfile getPlayer() {
        return this.player;
    }

    public void setPlayer(GameProfile player) {
        this.player = player;
    }
}
