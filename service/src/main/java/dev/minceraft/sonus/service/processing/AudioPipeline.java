package dev.minceraft.sonus.service.processing;

import dev.minceraft.sonus.common.audio.SonusAudio;

import java.util.List;

public final class AudioPipeline implements AudioPipelineNode {

    private final List<AudioPipelineNode> nodes;

    public AudioPipeline(List<AudioPipelineNode> nodes) {
        this.nodes = nodes;
    }

    public void addNode(AudioPipelineNode node) {
        this.nodes.add(node);
    }

    @Override
    public void process(SonusAudio audio) {
        for (AudioPipelineNode node : this.nodes) {
            node.process(audio);
        }
    }

    public boolean isEmpty() {
        return this.nodes.isEmpty();
    }
}
