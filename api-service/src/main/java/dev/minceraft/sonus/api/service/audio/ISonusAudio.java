package dev.minceraft.sonus.api.service.audio;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ISonusAudio {

    long getSequenceNumber();

    short @Nullable [] getPcm();

    byte @Nullable [] getOpus();

    default boolean isEmpty() {
        return this.getPcm() != null && this.getPcm().length == 0
                || this.getOpus() != null && this.getOpus().length == 0;
    }

    ISonusAudio copy();
}
