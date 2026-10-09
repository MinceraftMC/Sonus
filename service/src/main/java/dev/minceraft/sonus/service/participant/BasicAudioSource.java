package dev.minceraft.sonus.service.participant;

import dev.minceraft.sonus.common.data.WorldRotatedVec3d;
import dev.minceraft.sonus.common.participant.IAudioSource;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class BasicAudioSource implements IAudioSource {

    private final UUID uniqueId = UUID.randomUUID();
    private UUID categoryId;
    private UUID serverId;
    private WorldRotatedVec3d position;

    @Override
    public UUID getUniqueId(@Nullable ISonusPlayer viewer) {
        return this.uniqueId;
    }

    @Override
    public @Nullable UUID getCategoryId() {
        return this.categoryId;
    }

    @Override
    public @Nullable UUID getServerId() {
        return this.serverId;
    }

    @Override
    public @Nullable WorldRotatedVec3d getPosition() {
        return this.position;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public void setServerId(UUID serverId) {
        this.serverId = serverId;
    }

    public void setPosition(WorldRotatedVec3d position) {
        this.position = position;
    }
}
