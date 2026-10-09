package dev.minceraft.sonus.plasmo.protocol.tcp.serverbound;


import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;

@NullMarked
public class PlayerInfoPacket extends PlayerStatePacket<PlayerInfoPacket> {

    private @MonotonicNonNull String minecraftVersion;
    private @MonotonicNonNull String plasmoVersion;
    private byte @MonotonicNonNull [] publicKey;

    public PlayerInfoPacket() {

    }

    @Override
    public void encode(ByteBuf buf) {
        super.encode(buf);
        Utf8String.writeUnsignedShort(buf, this.minecraftVersion);
        Utf8String.writeUnsignedShort(buf, this.plasmoVersion);
        DataTypeUtil.INT.writeByteArray(buf, this.publicKey);
    }

    @Override
    public void decode(ByteBuf buf) {
        super.decode(buf);
        this.minecraftVersion = Utf8String.readUnsignedShort(buf, 32);
        this.plasmoVersion = Utf8String.readUnsignedShort(buf, 32);
        this.publicKey = DataTypeUtil.INT.readByteArray(buf, 4096);
    }

    @Override
    public void handle(TcpHandler handler) {
        handler.handlePlayerInfoPacket(this);
    }

    public String getMinecraftVersion() {
        return this.minecraftVersion;
    }

    public void setMinecraftVersion(String minecraftVersion) {
        this.minecraftVersion = minecraftVersion;
    }

    public String getPlasmoVersion() {
        return this.plasmoVersion;
    }

    public void setPlasmoVersion(String plasmoVersion) {
        this.plasmoVersion = plasmoVersion;
    }

    public byte[] getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public String toString() {
        return "PlayerInfoPacket{" +
                "minecraftVersion='" + minecraftVersion + '\'' +
                ", plasmoVersion='" + plasmoVersion + '\'' +
                ", publicKey=" + Arrays.toString(publicKey) +
                '}';
    }
}
