package dev.minceraft.sonus.plasmo.protocol.tcp.clientbound;

import dev.minceraft.sonus.common.protocol.util.DataTypeUtil;
import dev.minceraft.sonus.common.protocol.util.Utf8String;
import dev.minceraft.sonus.common.protocol.util.GameProfile;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NullMarked
public class SourceLinePlayersListPacket extends TcpPlasmoPacket<SourceLinePlayersListPacket> {

    private @MonotonicNonNull UUID sourceLineId;
    private @MonotonicNonNull List<GameProfile> players;

    public SourceLinePlayersListPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        DataTypeUtil.writeUniqueId(buf, this.sourceLineId);
        DataTypeUtil.INT.writeCollection(buf, this.players, (b, profile) ->
                DataTypeUtil.INT.writeGameProfile(b, profile, Utf8String::writeUnsignedShort));
    }

    @Override
    public void decode(ByteBuf buf) {
        this.sourceLineId = DataTypeUtil.readUniqueId(buf);
        this.players = DataTypeUtil.INT.readCollection(buf, b ->
                DataTypeUtil.INT.readGameProfile(b, Utf8String::readUnsignedShort), ArrayList::new);
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handleSourceLinePlayersListPacket(this);
    }

    public UUID getSourceLineId() {
        return this.sourceLineId;
    }

    public void setSourceLineId(UUID sourceLineId) {
        this.sourceLineId = sourceLineId;
    }

    public List<GameProfile> getPlayers() {
        return this.players;
    }

    public void setPlayers(List<GameProfile> players) {
        this.players = players;
    }
}
