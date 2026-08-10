package dev.minceraft.sonus.api.service.participant;

import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.api.service.util.Vec3d;
import org.jspecify.annotations.NullMarked;

/**
 * The sonus participant type, that can receive various audio
 */
@NullMarked
public interface ISonusListener extends ISonusParticipant {

    /**
     * Sends static audio
     *
     * @param source the source the audio should come from
     * @param audio  the audio data
     */
    void sendStaticAudio(ISonusSource source, ISonusAudio audio);

    /**
     * Sends static audio end.
     *
     * @param source   the source the audio end should come from
     * @param sequence the last sequence number
     */
    void sendStaticAudioEnd(ISonusSource source, long sequence);

    /**
     * Sends spatial audio bound to a position
     *
     * @param source   the source the audio should come from
     * @param audio    the audio data
     * @param position the source position
     */
    void sendSpatialAudio(ISonusSource source, ISonusAudio audio, Vec3d position);

    /**
     * Send spatial audio bound to the source itself. Could be a player or entity
     *
     * @param source the source the audio should come from
     * @param audio  the audio data
     */
    void sendSpatialAudio(ISonusSource source, ISonusAudio audio);

    /**
     * Sends normalized spatial audio bound to the source.
     * <p>
     * It helps to hide the exact location a little.
     * <strong>However, it does not guarantee complete protection!</strong>
     *
     * @param source the source the audio should come from
     * @param audio  the audio data
     */
    void sendSpatialNormedAudio(ISonusSource source, ISonusAudio audio);

    /**
     * Sends spatial audio end.
     *
     * @param source   the source the audio end should come from
     * @param sequence the last sequence number
     */
    void sendSpatialAudioEnd(ISonusSource source, long sequence);
}
