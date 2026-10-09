package dev.minceraft.sonus.plasmo.protocol.tcp.clientbound;

import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class ConfigPlayerInfoPacket<T extends ConfigPlayerInfoPacket<T>> extends TcpPlasmoPacket<T> {

    protected @MonotonicNonNull Map<String, Boolean> permissions;

    @Override
    public void encode(ByteBuf buf) {
        DataTypeUtil.INT.writeMap(buf, this.permissions, Utf8String::writeUnsignedShort, ByteBuf::writeBoolean);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.permissions = DataTypeUtil.INT.readMap(buf, Utf8String::readUnsignedShort, ByteBuf::readBoolean, HashMap::new);
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handleConfigPlayerInfoPacket(this);
    }

    public Map<String, Boolean> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(Map<String, Boolean> permissions) {
        this.permissions = permissions;
    }
}
