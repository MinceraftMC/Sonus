package dev.minceraft.sonus.plasmo.protocol.udp.bothbound;


import dev.minceraft.sonus.common.protocol.util.PacketDirection;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.plasmo.protocol.udp.UdpHandler;
import dev.minceraft.sonus.plasmo.protocol.udp.UdpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.net.InetSocketAddress;

@NullMarked
public class PingPlasmoPacket extends UdpPlasmoPacket<PingPlasmoPacket> {

    private PacketDirection direction;
    private long time;
    private @MonotonicNonNull String serverAddress;
    private int serverPort;

    public PingPlasmoPacket() {
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeLong(this.time);

        if (this.serverAddress != null && this.serverPort > 0) {
            Utf8String.writeUnsignedShort(buf, this.serverAddress);
            buf.writeShort(this.serverPort);
        }
    }

    @Override
    public void decode(ByteBuf buf) {
        this.time = buf.readLong();

        if (buf.isReadable()) {
            this.serverAddress = Utf8String.readUnsignedShort(buf, 255);
            this.serverPort = buf.readShort();
        } else {
            this.serverAddress = null;
            this.serverPort = 0;
        }
    }

    @Override
    public void handle(UdpHandler handler) {
        handler.handlePingPacket(this);
    }

    public PacketDirection getDirection() {
        return this.direction;
    }

    public void setDirection(PacketDirection direction) {
        this.direction = direction;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getServerAddress() {
        return this.serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setInetSocketAddress(InetSocketAddress socketAddress) {
        this.serverAddress = socketAddress.getHostString();
        this.serverPort = socketAddress.getPort();
    }

    @Override
    public String toString() {
        return "PingPacket{" +
                "time=" + time +
                ", serverAddress='" + serverAddress + '\'' +
                ", serverPort=" + serverPort +
                '}';
    }
}
