package dev.minceraft.sonus.agent.paper.api;
// Created by booky10 in Sonus (18:11 17.11.2025)

import dev.minceraft.sonus.agent.paper.audio.AudioSupplier;
import dev.minceraft.sonus.service.audio.AudioCategory;
import dev.minceraft.sonus.service.audio.AudioProcessor;
import dev.minceraft.sonus.service.natives.LameNativesLoader;
import dev.minceraft.sonus.service.natives.OpusNativesLoader;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public interface SonusAgentApi {

    boolean isConnected(Player player);

    void registerAudioCategory(AudioCategory category);

    AudioPlayer createAudioPlayer(
            Player player, UUID channelId, @Nullable UUID categoryId,
            AudioSupplier audio, AudioProcessor.Mode mode
    );

    AudioProcessor createAudioProcessor(AudioProcessor.Mode mode);

    OpusNativesLoader getOpusNatives();

    LameNativesLoader getLameNatives();
}
