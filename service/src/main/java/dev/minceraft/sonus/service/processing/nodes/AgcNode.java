package dev.minceraft.sonus.service.processing.nodes;

import dev.minceraft.sonus.common.audio.SonusAudio;
import dev.minceraft.sonus.common.natives.SpeexNativesLoader;
import dev.minceraft.sonus.common.natives.SpeexNativesLoader.AutomaticGainControl;
import dev.minceraft.sonus.service.processing.AudioPipelineNode;

import static dev.minceraft.sonus.common.SonusConstants.FRAME_SIZE;
import static dev.minceraft.sonus.common.SonusConstants.SAMPLE_RATE;

public final class AgcNode implements AudioPipelineNode, AutoCloseable {

    private static final int TARGET = dbSample(-5d); // -5 dBFS

    private final AutomaticGainControl agc;

    public AgcNode(SpeexNativesLoader speex) {
        this.agc = speex.new AutomaticGainControl(FRAME_SIZE, SAMPLE_RATE);
        this.agc.setTarget(TARGET);
    }

    private static int dbSample(double dbfs) {
        double value = 32768D * Math.pow(10D, dbfs / 20D);
        long rounded = Math.round(value);
        if (rounded < 0) {
            return 0;
        }
        if (rounded > 32768) {
            return 32768;
        }
        return (int) rounded;
    }

    @Override
    public void process(SonusAudio audio) {
        // thread safety is not relevant here
        this.agc.agc(audio.pcm());
        audio.setDirtyPcm();
    }

    @Override
    public void close() {
        try (this.agc) {
            // NO-OP
        }
    }
}
