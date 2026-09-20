package dev.minceraft.sonus.service.api.participant.builtin;

import dev.minceraft.sonus.api.service.participant.builtin.ISonusBasicSource;
import dev.minceraft.sonus.api.service.participant.builtin.ISonusServicePlayer;
import dev.minceraft.sonus.api.service.util.WorldRotatedVec3d;
import dev.minceraft.sonus.service.participant.BasicAudioSource;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class ApiBasicSource implements ISonusBasicSource {

    private final BasicAudioSource source;

    public ApiBasicSource(BasicAudioSource source) {
        this.source = source;
    }

    @Override
    public void setCategoryId(@Nullable UUID categoryId) {
        this.source.setCategoryId(categoryId);
    }

    @Override
    public void setServerId(@Nullable UUID serverId) {
        this.source.setServerId(serverId);
    }

    @Override
    public void setPosition(@Nullable WorldRotatedVec3d position) {
        this.source.setPosition(position == null ? null : new dev.minceraft.sonus.common.data.WorldRotatedVec3d(
                position.getX(),
                position.getY(),
                position.getZ(),
                position.getYaw(),
                position.getPitch(),
                position.getDimension()
        ));
    }

    @Override
    public UUID getUniqueId(@Nullable ISonusServicePlayer viewer) {
        return this.source.getUniqueId(); // BasicAudioSource does not use the viewer parameter, so we can ignore it here.
    }

    @Override
    public @Nullable UUID getCategoryId() {
        return this.source.getCategoryId();
    }

    @Override
    public @Nullable UUID getServerId() {
        return this.source.getServerId();
    }

    @Override
    public @Nullable WorldRotatedVec3d getPosition() {
        dev.minceraft.sonus.common.data.WorldRotatedVec3d position = this.source.getPosition();
        return position == null ? null : new WorldRotatedVec3d(
                position.getX(),
                position.getY(),
                position.getZ(),
                position.getYaw(),
                position.getPitch(),
                position.getDimension()
        );
    }
}
