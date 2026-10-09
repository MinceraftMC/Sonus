package dev.minceraft.sonus.service.api.participant.builtin;

import dev.minceraft.sonus.api.service.participant.builtin.ISonusBasicSource;
import dev.minceraft.sonus.api.service.participant.builtin.ISonusServicePlayer;
import dev.minceraft.sonus.api.service.util.WorldRotatedVec3d;
import dev.minceraft.sonus.service.api.participant.ApiSonusSource;
import dev.minceraft.sonus.service.participant.BasicAudioSource;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public class ApiBasicSource extends ApiSonusSource<BasicAudioSource> implements ISonusBasicSource {

    public ApiBasicSource(BasicAudioSource source) {
        super(source);
    }

    @Override
    public void setCategoryId(@Nullable UUID categoryId) {
        this.delegate.setCategoryId(categoryId);
    }

    @Override
    public void setServerId(@Nullable UUID serverId) {
        this.delegate.setServerId(serverId);
    }

    @Override
    public void setPosition(@Nullable WorldRotatedVec3d position) {
        this.delegate.setPosition(position == null ? null : new dev.minceraft.sonus.common.data.WorldRotatedVec3d(
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
        return this.delegate.getUniqueId(); // BasicAudioSource does not use the viewer parameter, so we can ignore it here.
    }

    @Override
    public @Nullable UUID getCategoryId() {
        return this.delegate.getCategoryId();
    }

    @Override
    public @Nullable UUID getServerId() {
        return this.delegate.getServerId();
    }

    @Override
    public @Nullable WorldRotatedVec3d getPosition() {
        dev.minceraft.sonus.common.data.WorldRotatedVec3d position = this.delegate.getPosition();
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
