package dev.minceraft.sonus.agent.paper.api;
// Created by booky10 in Sonus (18:11 17.11.2025)

import dev.minceraft.sonus.agent.paper.audio.AudioSupplier;
import dev.minceraft.sonus.common.audio.AudioProcessor;
import dev.minceraft.sonus.common.audio.OpusMode;
import dev.minceraft.sonus.common.natives.LameNativesLoader;
import dev.minceraft.sonus.common.natives.OpusNativesLoader;
import dev.minceraft.sonus.common.protocol.audio.AudioCategory;
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
            AudioSupplier audio, OpusMode mode
    );

    AudioProcessor createAudioProcessor(OpusMode mode);

    OpusNativesLoader getOpusNatives();

    LameNativesLoader getLameNatives();
}
