package dev.minceraft.sonus.protocol.meta;
// Created by booky10 in Sonus (01:56 17.07.2025)

import dev.minceraft.sonus.common.protocol.registry.SimpleRegistry;
import dev.minceraft.sonus.common.util.codec.VarInt;
import dev.minceraft.sonus.protocol.meta.agentbound.PlayerConnectionStateMessage;
import dev.minceraft.sonus.protocol.meta.agentbound.TriggerCommandUpdateMessage;
import dev.minceraft.sonus.protocol.meta.servicebound.AudioStreamMessage;
import dev.minceraft.sonus.protocol.meta.servicebound.BackendTickMessage;
import dev.minceraft.sonus.protocol.meta.servicebound.RegisterAudioCategoryMessage;
import dev.minceraft.sonus.protocol.meta.servicebound.UpdateRoomDefinitionMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class MetaRegistry {

    public static final SimpleRegistry<ByteBuf, IMetaMessage> REGISTRY =
            SimpleRegistry.Builder.<ByteBuf, IMetaMessage>createSimple()
                    .codec((buf, packet) -> packet.decode(buf), (buf, packet) -> packet.encode(buf))
                    .idCodec(VarInt::read, VarInt::write)
                    .register(BackendTickMessage.class, BackendTickMessage::new)
                    .register(UpdateRoomDefinitionMessage.class, UpdateRoomDefinitionMessage::new)
                    .register(AudioStreamMessage.class, AudioStreamMessage::new)
                    .register(RegisterAudioCategoryMessage.class, RegisterAudioCategoryMessage::new)
                    .register(PlayerConnectionStateMessage.class, PlayerConnectionStateMessage::new)
                    .register(TriggerCommandUpdateMessage.class, TriggerCommandUpdateMessage::new)
                    .build();

    private MetaRegistry() {
    }

    public static byte[] write(IMetaMessage message) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        try {
            REGISTRY.encode(buf, message);
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            return data;
        } finally {
            buf.release();
        }
    }
}
