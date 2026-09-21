package dev.minceraft.sonus.plasmo.protocol.tcp.clientbound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.VoicePlayerInfo;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class PlayerListPacket extends TcpPlasmoPacket<PlayerListPacket> {

    private @MonotonicNonNull List<VoicePlayerInfo> players;

    public PlayerListPacket() {

    }

    @Override
    public void encode(ByteBuf buf) {
        DataTypeUtil.INT.writeCollection(buf, this.players, (buffer, info) -> info.write(buffer));
    }

    @Override
    public void decode(ByteBuf buf) {
        this.players = DataTypeUtil.INT.readCollection(buf, VoicePlayerInfo::new, ArrayList::new);
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handlePlayerListPacket(this);
    }

    public List<VoicePlayerInfo> getPlayers() {
        return this.players;
    }

    public void setPlayers(List<VoicePlayerInfo> players) {
        this.players = players;
    }
}
