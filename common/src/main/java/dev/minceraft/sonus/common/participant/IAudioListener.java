package dev.minceraft.sonus.common.participant;

import dev.minceraft.sonus.common.audio.SonusAudio;
import dev.minceraft.sonus.common.data.Vec3d;

public interface IAudioListener extends IAudioParticipant {

    void sendStaticAudio(IAudioSource source, SonusAudio audio);

    void sendStaticAudioEnd(IAudioSource source, long sequence);

    void sendSpatialAudio(IAudioSource source, SonusAudio audio, Vec3d position);

    void sendSpatialAudio(IAudioSource source, SonusAudio audio);

    void sendSpatialNormedAudio(IAudioSource source, SonusAudio audio);

    void sendSpatialAudioEnd(IAudioSource source, long sequence);
}
