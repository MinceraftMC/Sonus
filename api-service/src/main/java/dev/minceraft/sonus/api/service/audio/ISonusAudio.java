package dev.minceraft.sonus.api.service.audio;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * The sonus audio wrapper. It is used to hold audio data and can be used to convert it from pcm to opus and vice versa.
 */
@NullMarked
public interface ISonusAudio {

    /**
     * The sequence number of the audio frame.
     *
     * @return the sequence number
     */
    long getSequenceNumber();

    /**
     * It returns the audio as PCM. It converts it lazily if needed.
     *
     * @return pcm audio
     */
    short @Nullable [] getPcm();

    /**
     * It returns the audio as Opus. It converts it lazily if needed.
     *
     * @return opus audio
     */
    byte @Nullable [] getOpus();

    /**
     * Checks if the audio frame is empty
     *
     * @return whether the audio frame is empty or not
     */
    default boolean isEmpty() {
        return this.getPcm() != null && this.getPcm().length == 0
                || this.getOpus() != null && this.getOpus().length == 0;
    }

    /**
     * Clones the audio frame
     *
     * @return the clone
     */
    ISonusAudio copy();
}
