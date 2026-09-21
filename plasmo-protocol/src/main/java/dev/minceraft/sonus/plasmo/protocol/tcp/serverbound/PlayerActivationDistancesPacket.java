package dev.minceraft.sonus.plasmo.protocol.tcp.serverbound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NullMarked
public class PlayerActivationDistancesPacket extends TcpPlasmoPacket<PlayerActivationDistancesPacket> {

    private @MonotonicNonNull Map<UUID, Integer> distances;

    public PlayerActivationDistancesPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        DataTypeUtil.INT.writeMap(buf, this.distances, DataTypeUtil::writeUniqueId, ByteBuf::writeInt);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.distances = DataTypeUtil.INT.readMap(
                buf, DataTypeUtil::readUniqueId, ByteBuf::readInt,
                count -> {
                    // the plasmo client only sends single-value maps for this packet
                    if (count != 1) {
                        throw new IllegalStateException("Expected single-entry map");
                    }
                    return new HashMap<>(1);
                });
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handlePlayerActivationDistancesPacket(this);
    }

    public Map<UUID, Integer> getDistances() {
        return this.distances;
    }

    public void setDistances(Map<UUID, Integer> distances) {
        this.distances = distances;
    }
}
