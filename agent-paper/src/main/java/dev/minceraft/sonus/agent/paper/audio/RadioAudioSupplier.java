package dev.minceraft.sonus.agent.paper.audio;
// Created by booky10 in TjcSonus (22:53 17.11.2024)

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static dev.minceraft.sonus.common.SonusConstants.FRAME_SIZE;

@NullMarked
public class RadioAudioSupplier implements AudioSupplier {

    private final short[] audioData;
    private int position;

    public RadioAudioSupplier(short[] audioData) {
        this.audioData = audioData;
    }

    @Override
    public void tick() {
        this.position = (this.position + FRAME_SIZE) % this.audioData.length;
    }

    @Override
    public short @Nullable [] get(int offset) {
        int pos = offset == 0 ? this.position : (this.position + FRAME_SIZE * offset) % this.audioData.length;
        short[] frame = THREAD_FRAME.get();

        int maxAudio = this.audioData.length - pos;
        if (FRAME_SIZE > maxAudio) {
            System.arraycopy(this.audioData, pos, frame, 0, maxAudio);
            System.arraycopy(this.audioData, 0, frame, maxAudio, FRAME_SIZE - maxAudio);
        } else {
            System.arraycopy(this.audioData, pos, frame, 0, FRAME_SIZE);
        }
        return frame;
    }
}
