package dev.minceraft.sonus.service.api.participant;

import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.api.service.participant.ISonusListener;
import dev.minceraft.sonus.api.service.participant.ISonusSource;
import dev.minceraft.sonus.api.service.util.Vec3d;
import dev.minceraft.sonus.common.participant.IAudioListener;
import dev.minceraft.sonus.service.api.audio.ApiAudio;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ApiSonusListener<T extends IAudioListener> extends ApiSonusParticipant<T> implements ISonusListener {

    public ApiSonusListener(T delegate) {
        super(delegate);
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
