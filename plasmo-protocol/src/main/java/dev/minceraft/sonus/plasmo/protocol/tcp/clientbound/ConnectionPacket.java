package dev.minceraft.sonus.plasmo.protocol.tcp.clientbound;


import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.net.InetSocketAddress;
import java.util.UUID;

@NullMarked
public class ConnectionPacket extends TcpPlasmoPacket<ConnectionPacket> {

    private @MonotonicNonNull UUID secret;
    private @MonotonicNonNull String address;
    private int port;

    public ConnectionPacket() {

    }

    @Override
    public void encode(ByteBuf buf) {
        DataTypeUtil.writeUniqueId(buf, this.secret);
        Utf8String.writeUnsignedShort(buf, this.address);
        buf.writeInt(this.port);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.secret = DataTypeUtil.readUniqueId(buf);
        this.address = Utf8String.readUnsignedShort(buf);
        this.port = buf.readInt();
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handleConnectionPacket(this);
    }

    public UUID getSecret() {
        return this.secret;
    }

    public void setSecret(UUID secret) {
        this.secret = secret;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setInetAddress(InetSocketAddress address) {
        this.address = address.getHostString();
        this.port = address.getPort();
    }
}
