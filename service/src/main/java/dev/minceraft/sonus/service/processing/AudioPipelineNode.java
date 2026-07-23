package dev.minceraft.sonus.service.processing;

import dev.minceraft.sonus.service.audio.SonusAudio;

@FunctionalInterface
public interface AudioPipelineNode {

    void process(SonusAudio audio);
}
