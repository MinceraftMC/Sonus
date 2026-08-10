package dev.minceraft.sonus.plasmo.protocol.udp;

import dev.minceraft.sonus.common.protocol.registry.SimpleRegistry;
import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.plasmo.protocol.udp.bothbound.CustomPlasmoPacket;
import dev.minceraft.sonus.plasmo.protocol.udp.bothbound.PingPlasmoPacket;
import dev.minceraft.sonus.plasmo.protocol.udp.clientbound.SelfAudioInfoPlasmoPacket;
import dev.minceraft.sonus.plasmo.protocol.udp.clientbound.SourceAudioPlasmoPacket;
import dev.minceraft.sonus.plasmo.protocol.udp.serverbound.PlayerAudioPlasmoPacket;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;

import static dev.minceraft.sonus.common.protocol.registry.ContextedRegistry.MessageDirection.ANY;
import static dev.minceraft.sonus.common.protocol.registry.ContextedRegistry.MessageDirection.DECODE;
import static dev.minceraft.sonus.common.protocol.registry.ContextedRegistry.MessageDirection.ENCODE;
import static dev.minceraft.sonus.common.protocol.registry.ContextedRegistry.MessageDirection.NONE;

@NullMarked
public class UdpPlasmoRegistry {

    public static final SimpleRegistry<ByteBuf, UdpPlasmoPacket<?>> REGISTRY =
            SimpleRegistry.Builder.<ByteBuf, UdpPlasmoPacket<?>>createSimple()
                    .codec((buf, packet) -> {
                        packet.setSecret(DataTypeUtil.readUniqueId(buf));
                        packet.setTimestamp(buf.readLong());

                        packet.decode(buf);
                    }, (buf, packet) -> {
                        DataTypeUtil.writeUniqueId(buf, packet.getSecret());
                        buf.writeLong(packet.getTimestamp());
                        packet.encode(buf);
                    })
                    .idCodec(ByteBuf::readByte, ByteBuf::writeByte)
                    .register(0x01, PingPlasmoPacket.class, PingPlasmoPacket::new, ANY)
                    .register(0x02, PlayerAudioPlasmoPacket.class, PlayerAudioPlasmoPacket::new, DECODE)
                    .register(0x03, SourceAudioPlasmoPacket.class, SourceAudioPlasmoPacket::new, ENCODE)
                    .register(0x04, SelfAudioInfoPlasmoPacket.class, SelfAudioInfoPlasmoPacket::new, ENCODE)
                    .register(0x100, CustomPlasmoPacket.class, CustomPlasmoPacket::new, NONE)
                    .build();

}
