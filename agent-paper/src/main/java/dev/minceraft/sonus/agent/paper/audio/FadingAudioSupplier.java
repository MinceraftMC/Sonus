package dev.minceraft.sonus.agent.paper.audio;
// Created by booky10 in TjcSonus (23:29 17.11.2024)

import dev.minceraft.sonus.common.util.AudioConversionUtil;
import org.bukkit.util.NumberConversions;
import org.joml.Math;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

import static dev.minceraft.sonus.common.SonusConstants.FRAME_SIZE;
import static dev.minceraft.sonus.common.SonusConstants.SAMPLE_RATE;

@NullMarked
public class FadingAudioSupplier implements AudioSupplier {

    private static final short[] EMPTY_FRAME = new short[FRAME_SIZE];

    private final AudioSupplier delegate;
    private final Supplier<Boolean> active;

    private float sourceVolume = 1f;
    private float targetVolume = 1f;
    private int totalFadeTicks = 0;
    private int fadeTicks = 0;

    public FadingAudioSupplier(AudioSupplier delegate, Supplier<Boolean> active) {
        this.delegate = delegate;
        this.active = active;
    }

    public void setVolume(float volume) {
        this.sourceVolume = volume;
    }

    public void setFade(float targetVolume, float seconds) {
        this.targetVolume = targetVolume;
        this.totalFadeTicks = NumberConversions.ceil((SAMPLE_RATE * seconds) / FRAME_SIZE);
        this.fadeTicks = 0;
    }

    @Override
    public void tick() {
        if (!this.active.get()) {
            return;
        }
        this.delegate.tick();
        // tick fade step
        if (this.totalFadeTicks != 0) {
            // intentionally reset after a frame has passed with the total fade being done
            if (this.fadeTicks >= this.totalFadeTicks) {
                this.totalFadeTicks = 0;
                this.sourceVolume = this.targetVolume;
            } else {
                this.fadeTicks++;
            }
        }
    }

    @Override
    public short @Nullable [] get(int offset) {
        if (!this.active.get()) {
            return null;
        }
        int fadeTicks = this.fadeTicks + offset;
        int totalFadeTicks = this.totalFadeTicks;
        float sourceVolume = this.sourceVolume;
        if (totalFadeTicks != 0 && fadeTicks >= totalFadeTicks) {
            totalFadeTicks = 0;
            sourceVolume = this.targetVolume;
        }
        if (totalFadeTicks == 0 && sourceVolume == 1f) {
            return this.delegate.get(offset);
        }
        if (totalFadeTicks == 0) {
            if (sourceVolume == 0f) {
                return EMPTY_FRAME;
            }
            short[] samples = this.delegate.get(offset);
            if (samples == null) {
                return null; // pass on cancel signal
            }
            return AudioConversionUtil.adjustVolume(samples.clone(), sourceVolume);
        }
        short[] samples = this.delegate.get(offset);
        if (samples == null) {
            return null;
        }
        float preFadeProgress = fadeTicks / (float) totalFadeTicks;
        float preVolume = Math.lerp(sourceVolume, this.targetVolume, preFadeProgress);
        float postFadeProgress = (fadeTicks + 1) / (float) totalFadeTicks;
        float postVolume = Math.lerp(sourceVolume, this.targetVolume, postFadeProgress);
        return AudioConversionUtil.adjustVolumeLerp(
                samples.clone(), preVolume, postVolume);
    }

    public AudioSupplier getDelegate() {
        return this.delegate;
    }
}
