package dev.minceraft.sonus.svc.protocol.registries;

import dev.minceraft.sonus.common.protocol.registry.ContextedRegistry;
import dev.minceraft.sonus.common.protocol.tcp.holder.PmDataHolderBuf;
import dev.minceraft.sonus.common.version.Versioned;
import dev.minceraft.sonus.svc.protocol.SvcPacketContext;
import dev.minceraft.sonus.svc.protocol.meta.SvcMetaPacket;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.AddCategorySvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.AddGroupSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.JoinedGroupSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.PlayerStateSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.PlayerStatesSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.RemoveCategorySvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.RemoveGroupSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.RemovePlayerStatePacket;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.SecretSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.servicebound.CreateGroupSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.servicebound.JoinGroupSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.servicebound.LeaveGroupSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.servicebound.RequestSecretSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.servicebound.UpdateStateSvcPacket;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

import static dev.minceraft.sonus.common.protocol.registry.ContextedRegistry.MessageDirection.DECODE;
import static dev.minceraft.sonus.common.protocol.registry.ContextedRegistry.MessageDirection.ENCODE;

@NullMarked
public final class SvcMetaPacketRegistry {

    private static final Map<Key, Integer> PACKET_IDS = new HashMap<>();

    public static final ContextedRegistry<PmDataHolderBuf, SvcMetaPacket, SvcPacketContext> BUF_REGISTRY =
            ContextedRegistry.Builder.<PmDataHolderBuf, SvcMetaPacket, SvcPacketContext>createContext()
                    .codec((data, packet, ctx) -> packet.decode(data.getFirst(), ctx),
                            (data, packet, ctx) -> packet.encode(data.getFirst(), ctx))
                    .idCodec((holder, ctx) -> PACKET_IDS.get(holder.getSecond()),
                            (id, packet, ctx) -> { /**/ })
                    .idConsumer((id, sample) -> {
                        for (Versioned.VersionedKeyEntry<Key> entry : sample.getPluginMessageChannel().versionedKeys()) {
                            PACKET_IDS.put(entry.key(), id);
                        }
                    })
                    .register(AddCategorySvcPacket.class, AddCategorySvcPacket::new, ENCODE)
                    .register(AddGroupSvcPacket.class, AddGroupSvcPacket::new, ENCODE)
                    .register(CreateGroupSvcPacket.class, CreateGroupSvcPacket::new, DECODE)
                    .register(JoinGroupSvcPacket.class, JoinGroupSvcPacket::new, DECODE)
                    .register(JoinedGroupSvcPacket.class, JoinedGroupSvcPacket::new, ENCODE)
                    .register(LeaveGroupSvcPacket.class, LeaveGroupSvcPacket::new, DECODE)
                    .register(PlayerStateSvcPacket.class, PlayerStateSvcPacket::new, ENCODE)
                    .register(PlayerStatesSvcPacket.class, PlayerStatesSvcPacket::new, ENCODE)
                    .register(RemovePlayerStatePacket.class, RemovePlayerStatePacket::new, ENCODE)
                    .register(RemoveCategorySvcPacket.class, RemoveCategorySvcPacket::new, ENCODE)
                    .register(RemoveGroupSvcPacket.class, RemoveGroupSvcPacket::new, ENCODE)
                    .register(RequestSecretSvcPacket.class, RequestSecretSvcPacket::new, DECODE)
                    .register(SecretSvcPacket.class, SecretSvcPacket::new, ENCODE)
                    .register(UpdateStateSvcPacket.class, UpdateStateSvcPacket::new, DECODE)
                    .build();
}
