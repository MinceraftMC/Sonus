package dev.minceraft.sonus.api.service.participant;

import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.api.service.util.Vec3d;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ISonusListener extends ISonusParticipant {

    void sendStaticAudio(ISonusSource source, ISonusAudio audio);

    void sendStaticAudioEnd(ISonusSource source, long sequence);

    void sendSpatialAudio(ISonusSource source, ISonusAudio audio, Vec3d position);

    void sendSpatialAudio(ISonusSource source, ISonusAudio audio);

    void sendSpatialNormedAudio(ISonusSource source, ISonusAudio audio);

    void sendSpatialAudioEnd(ISonusSource source, long sequence);
}
