package dev.minceraft.sonus.agent.paper.audio;
// Created by booky10 in TjcSonus (23:39 17.11.2024)

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import static dev.minceraft.sonus.common.SonusConstants.FRAME_SIZE;

@NullMarked
public interface AudioSupplier {

    ThreadLocal<short[]> THREAD_FRAME = ThreadLocal.withInitial(() -> new short[FRAME_SIZE]);

    static AudioSupplier fromSimple(Supplier<short @Nullable []> supplier) {
        return new AudioSupplier() {
            private final List<short[]> frames = new LinkedList<>();

            public short @Nullable [] appendFrame() {
                short[] frame = supplier.get();
                short[] clonedFrame = frame != null ? frame.clone() : null;
                this.frames.add(clonedFrame);
                return clonedFrame;
            }

            @Override
            public void tick() {
                // discard tick frame
                if (!this.frames.isEmpty()) {
                    this.frames.removeFirst();
                } else {
                    supplier.get();
                }
            }

            @Override
            public short @Nullable [] get(int offset) {
                Preconditions.checkArgument(offset >= 0, "offset can't be negative");
                if (this.frames.size() >= offset + 1) {
                    // already in list
                    return this.frames.get(offset);
                }
                // add frames to list
                short[] frame;
                do {
                    frame = this.appendFrame();
                } while (this.frames.size() < offset + 1);
                return frame;
            }
        };
    }

    /**
     * Should mutate state to skip to the next frame and return the next frame.
     *
     * @return null to stop playing
     */
    default short @Nullable [] getAndTick() {
        short[] ret = this.get(0);
        this.tick();
        return ret;
    }

    /**
     * Should mutate state to skip to the next frame.
     */
    default void tick() {
    }

    /**
     * Should not have any side effects.
     *
     * @return null to stop playing
     */
    @Contract(pure = true)
    default short @Nullable [] get() {
        return this.get(0);
    }

    /**
     * Should not have any side effects.
     *
     * @return null to stop playing
     */
    @Contract(pure = true)
    short @Nullable [] get(int offset);

    default UntickableAudioSupplier untickable() {
        return new UntickableAudioSupplier(this);
    }
}
