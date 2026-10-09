package dev.minceraft.sonus.web.protocol.model;
// Created by booky10 in Sonus (20:36 28.11.2025)

import dev.minceraft.sonus.common.participant.builtin.IRoom;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.common.util.codec.DataTypeUtil;
import dev.minceraft.sonus.common.util.codec.Utf8String;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

@NullMarked
public class SonusWebPlayerState {

    private static final int FLAG_MUTED = 1 << 0;
    private static final int FLAG_DEAFENED = 1 << 1;
    private static final int FLAG_HAS_GROUP = 1 << 2;
    private static final int FLAG_HAS_SERVER = 1 << 3;
    private static final int FLAG_HAS_TEXTURE = 1 << 4;

    private final UUID uniqueId;
    private final Component name;
    private final @Nullable String textureHash;
    private final boolean muted;
    private final boolean deafened;
    private final @Nullable UUID primaryRoomId;
    private final @Nullable UUID serverId;

    public SonusWebPlayerState(
            UUID uniqueId, Component name,
            @Nullable String textureHash,
            boolean muted, boolean deafened,
            @Nullable UUID primaryRoomId,
            @Nullable UUID serverId
    ) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.textureHash = textureHash;
        this.muted = muted;
        this.deafened = deafened;
        this.primaryRoomId = primaryRoomId;
        this.serverId = serverId;
    }

    public static SonusWebPlayerState fromState(ISonusPlayer player, ISonusPlayer viewer) {
        UUID uniqueId = player.getUniqueId(viewer);
        Component name = viewer.renderComponent(text(player.getName(viewer)));
        String textureHash = player.getTextureHash(viewer);
        boolean muted = player.isMuted();
        boolean deafened = player.isDeafened();
        IRoom primaryRoom = player.getPrimaryRoom();
        UUID primaryRoomId = primaryRoom != null ? primaryRoom.getUniqueId() : null;
        UUID serverId = player.getServerId();
        return new SonusWebPlayerState(uniqueId, name, textureHash, muted, deafened, primaryRoomId, serverId);
    }

    public static void encode(ByteBuf buf, SonusWebPlayerState state) {
        DataTypeUtil.writeUniqueId(buf, state.uniqueId);
        DataTypeUtil.writeComponentJson(buf, state.name);
        buf.writeByte(state.packFlags());
        if (state.primaryRoomId != null) {
            DataTypeUtil.writeUniqueId(buf, state.primaryRoomId);
        }
        if (state.serverId != null) {
            DataTypeUtil.writeUniqueId(buf, state.serverId);
        }
        if (state.textureHash != null) {
            Utf8String.write(buf, state.textureHash);
        }
    }

    public static SonusWebPlayerState decode(ByteBuf buf) {
        UUID uniqueId = DataTypeUtil.readUniqueId(buf);
        Component name = DataTypeUtil.readComponentJson(buf);
        short flags = buf.readUnsignedByte();
        boolean muted = (flags & FLAG_MUTED) != 0;
        boolean deafened = (flags & FLAG_DEAFENED) != 0;
        boolean hasGroup = (flags & FLAG_HAS_GROUP) != 0;
        UUID groupId = hasGroup ? DataTypeUtil.readUniqueId(buf) : null;
        boolean hasServer = (flags & FLAG_HAS_SERVER) != 0;
        UUID serverId = hasServer ? DataTypeUtil.readUniqueId(buf) : null;
        boolean hasTexture = (flags & FLAG_HAS_TEXTURE) != 0;
        String textureHash = hasTexture ? Utf8String.read(buf) : null;
        return new SonusWebPlayerState(uniqueId, name, textureHash, muted, deafened, groupId, serverId);
    }

    public byte packFlags() {
        return (byte) (0
                | (this.muted ? FLAG_MUTED : 0)
                | (this.deafened ? FLAG_DEAFENED : 0)
                | (this.primaryRoomId != null ? FLAG_HAS_GROUP : 0)
                | (this.serverId != null ? FLAG_HAS_SERVER : 0)
                | (this.textureHash != null ? FLAG_HAS_TEXTURE : 0));
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public Component getName() {
        return this.name;
    }

    public @Nullable String getTextureHash() {
        return this.textureHash;
    }

    public boolean isMuted() {
        return this.muted;
    }

    public boolean isDeafened() {
        return this.deafened;
    }

    public @Nullable UUID getPrimaryRoomId() {
        return this.primaryRoomId;
    }

    public @Nullable UUID getServerId() {
        return this.serverId;
    }
}
