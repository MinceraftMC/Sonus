package dev.minceraft.sonus.service.api.participant;

import dev.minceraft.sonus.api.service.participant.ISonusSource;
import dev.minceraft.sonus.api.service.util.WorldRotatedVec3d;
import dev.minceraft.sonus.common.participant.IAudioSource;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public class ApiSonusSource<T extends IAudioSource> extends ApiSonusParticipant<T> implements ISonusSource {

    public ApiSonusSource(T delegate) {
        super(delegate);
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
        dev.minceraft.sonus.common.data.WorldRotatedVec3d pos = this.delegate.getPosition();
        if (pos == null) {
            return null;
        }
        return new WorldRotatedVec3d(pos.getX(), pos.getY(), pos.getZ(), pos.getYaw(), pos.getPitch(), pos.getDimension());
    }
}
