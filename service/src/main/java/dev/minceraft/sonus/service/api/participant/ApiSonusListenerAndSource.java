package dev.minceraft.sonus.service.api.participant;

import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.api.service.participant.ISonusListener;
import dev.minceraft.sonus.api.service.participant.ISonusSource;
import dev.minceraft.sonus.api.service.util.Vec3d;
import dev.minceraft.sonus.api.service.util.WorldRotatedVec3d;
import dev.minceraft.sonus.common.participant.IAudioListener;
import dev.minceraft.sonus.common.participant.IAudioSource;
import dev.minceraft.sonus.service.api.audio.ApiAudio;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class ApiSonusListenerAndSource<T extends IAudioListener & IAudioSource> extends ApiSonusParticipant<T> implements ISonusListener, ISonusSource {

    public ApiSonusListenerAndSource(T delegate) {
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

    @Override
    public void sendStaticAudio(ISonusSource source, ISonusAudio audio) {
        this.delegate.sendStaticAudio(((ApiSonusSource<?>) source).getDelegate(), ((ApiAudio) audio).getDelegate());
    }

    @Override
    public void sendStaticAudioEnd(ISonusSource source, long sequence) {
        this.delegate.sendStaticAudioEnd(((ApiSonusSource<?>) source).getDelegate(), sequence);
    }

    @Override
    public void sendSpatialAudio(ISonusSource source, ISonusAudio audio, Vec3d position) {
        dev.minceraft.sonus.common.data.Vec3d vec3d = new dev.minceraft.sonus.common.data.Vec3d(position.getX(), position.getY(), position.getZ());
        this.delegate.sendSpatialAudio(((ApiSonusSource<?>) source).getDelegate(), ((ApiAudio) audio).getDelegate(), vec3d);
    }

    @Override
    public void sendSpatialAudio(ISonusSource source, ISonusAudio audio) {
        this.delegate.sendSpatialAudio(((ApiSonusSource<?>) source).getDelegate(), ((ApiAudio) audio).getDelegate());
    }

    @Override
    public void sendSpatialNormedAudio(ISonusSource source, ISonusAudio audio) {
        this.delegate.sendSpatialNormedAudio(((ApiSonusSource<?>) source).getDelegate(), ((ApiAudio) audio).getDelegate());
    }

    @Override
    public void sendSpatialAudioEnd(ISonusSource source, long sequence) {
        this.delegate.sendSpatialAudioEnd(((ApiSonusSource<?>) source).getDelegate(), sequence);
    }
}
