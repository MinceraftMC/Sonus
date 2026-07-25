package dev.minceraft.sonus.svc.protocol.registries;

import dev.minceraft.sonus.common.protocol.registry.ContextedRegistry;
import dev.minceraft.sonus.common.protocol.registry.SimpleRegistry;
import dev.minceraft.sonus.common.util.codec.VarInt;
import dev.minceraft.sonus.svc.protocol.SvcPacketContext;
import dev.minceraft.sonus.svc.protocol.voice.SvcVoicePacket;
import dev.minceraft.sonus.svc.protocol.voice.clientbound.AuthenticateAckSvcPacket;
import dev.minceraft.sonus.svc.protocol.voice.clientbound.ConnectionCheckAckSvcPacket;
import dev.minceraft.sonus.svc.protocol.voice.clientbound.GroupSoundSvcPacket;
import dev.minceraft.sonus.svc.protocol.voice.clientbound.LocationSoundSvcPacket;
import dev.minceraft.sonus.svc.protocol.voice.clientbound.PlayerSoundSvcPacket;
import dev.minceraft.sonus.svc.protocol.voice.commonbound.KeepAliveSvcPacket;
import dev.minceraft.sonus.svc.protocol.voice.commonbound.PingSvcPacket;
import dev.minceraft.sonus.svc.protocol.voice.servicebound.AuthenticateSvcPacket;
import dev.minceraft.sonus.svc.protocol.voice.servicebound.ConnectionCheckSvcPacket;
import dev.minceraft.sonus.svc.protocol.voice.servicebound.MicSvcPacket;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;

import static dev.minceraft.sonus.common.protocol.registry.ContextedRegistry.MessageDirection.ANY;
import static dev.minceraft.sonus.common.protocol.registry.ContextedRegistry.MessageDirection.DECODE;
import static dev.minceraft.sonus.common.protocol.registry.ContextedRegistry.MessageDirection.ENCODE;

@NullMarked
public final class SvcVoicePacketRegistry {

    public static final ContextedRegistry<ByteBuf, SvcVoicePacket, SvcPacketContext> REGISTRY =
            SimpleRegistry.Builder.<ByteBuf, SvcVoicePacket, SvcPacketContext>createContext()
                    .codec((buf, packet, ctx) -> packet.decode(buf, ctx),
                            (buf, packet, ctx) -> packet.encode(buf, ctx))
                    .idCodec((buf, __) -> VarInt.read(buf),
                            (buf, id, __) -> VarInt.write(buf, id))
                    .idOffset(1)  // svc index starts at 1
                    .register(MicSvcPacket.class, MicSvcPacket::new)
                    .register(PlayerSoundSvcPacket.class, PlayerSoundSvcPacket::new, ENCODE)
                    .register(GroupSoundSvcPacket.class, GroupSoundSvcPacket::new, ENCODE)
                    .register(LocationSoundSvcPacket.class, LocationSoundSvcPacket::new, ENCODE)
                    .register(AuthenticateSvcPacket.class, AuthenticateSvcPacket::new, DECODE)
                    .register(AuthenticateAckSvcPacket.class, AuthenticateAckSvcPacket::new, ENCODE)
                    .register(PingSvcPacket.class, PingSvcPacket::new, ANY)
                    .register(KeepAliveSvcPacket.class, () -> KeepAliveSvcPacket.INSTANCE, ANY)
                    .register(ConnectionCheckSvcPacket.class, ConnectionCheckSvcPacket::new, DECODE)
                    .register(ConnectionCheckAckSvcPacket.class, ConnectionCheckAckSvcPacket::new, ENCODE)
                    .build();
}
