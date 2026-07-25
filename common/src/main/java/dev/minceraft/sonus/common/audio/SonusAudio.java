package dev.minceraft.sonus.common.audio;
// Created by booky10 in Sonus (02:44 17.07.2025)

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Should be used with caution, thread-safety is only limited to lazy initialization of
 * pcm/opus audio data and returned arrays are mutable but should not be modified
 * without calling {@link #setDirtyPcm()}/{@link #setDirtyOpus()} respectively.
 */
@NullMarked
public final class SonusAudio {

    private long sequenceNumber;
    private @Nullable Supplier<AudioProcessor> processor;
    private volatile short @MonotonicNonNull [] pcm;
    private volatile byte @MonotonicNonNull [] opus;

    private SonusAudio(
            long sequenceNumber, @Nullable Supplier<AudioProcessor> processor,
            short @MonotonicNonNull [] pcm, byte @MonotonicNonNull [] opus
    ) {
        this.sequenceNumber = sequenceNumber;
        this.processor = processor;
        this.pcm = pcm;
        this.opus = opus;
    }

    public static SonusAudio fromPcm(long sequenceNumber, short[] pcm) {
        return new SonusAudio(sequenceNumber, null, pcm, null);
    }

    public static SonusAudio fromOpus(long sequenceNumber, byte[] opus) {
        return new SonusAudio(sequenceNumber, null, null, opus);
    }

    public short[] pcm() {
        short[] pcm = this.pcm;
        byte[] opus = this.opus;
        if (pcm == null) {
            if (opus == null || this.processor == null) {
                throw new IllegalStateException("Raw audio data is not present");
            }
            synchronized (this) {
                if (this.pcm == null) {
                    this.pcm = pcm = this.processor.get().decode(opus);
                } else {
                    pcm = this.pcm;
                }
            }
        }
        return pcm;
    }

    public void setDirtyPcm() {
        synchronized (this) {
            // pcm has been changed, invalidate opus
            this.opus = null;
        }
    }

    public byte[] opus() {
        byte[] opus = this.opus;
        short[] pcm = this.pcm;
        if (opus == null) {
            if (pcm == null || this.processor == null) {
                throw new IllegalStateException("Opus audio data is not present");
            }
            synchronized (this) {
                if (this.opus == null) {
                    this.opus = opus = this.processor.get().encode(pcm);
                } else {
                    opus = this.opus;
                }
            }
        }
        return opus;
    }

    public void setDirtyOpus() {
        synchronized (this) {
            // opus has been changed, invalidate pcm
            this.pcm = null;
        }
    }

    public boolean isEmpty() {
        return this.pcm != null && this.pcm.length == 0
                || this.opus != null && this.opus.length == 0;
    }

    public long getSequenceNumber() {
        return this.sequenceNumber;
    }

    public SonusAudio setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        return this;
    }

    public SonusAudio setProcessor(Supplier<AudioProcessor> processor) {
        this.processor = processor;
        return this;
    }

    public SonusAudio copy() {
        // intentionally discard audio processor while copying
        return new SonusAudio(this.sequenceNumber, null, this.pcm, this.opus);
    }
}
