package dev.minceraft.sonus.agent.paper.api;
// Created by booky10 in Sonus (23:54 16.11.2025)

import dev.minceraft.sonus.agent.paper.SonusAgentPlugin;
import dev.minceraft.sonus.agent.paper.audio.AudioSupplier;
import dev.minceraft.sonus.agent.paper.audio.AudioTicker;
import dev.minceraft.sonus.service.audio.AudioCategory;
import dev.minceraft.sonus.service.audio.AudioProcessor;
import dev.minceraft.sonus.service.natives.LameNativesLoader;
import dev.minceraft.sonus.service.natives.OpusNativesLoader;
import dev.minceraft.sonus.protocol.meta.servicebound.RegisterAudioCategoryMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
@NullMarked
public class SonusAgentApiImpl implements SonusAgentApi {

    // the SVC client caps the audio queue at 32 frames, so we can't queue more than that;
    // additionally, as clients may have terrible internet connection (especially ping spikes are relevant here),
    // leave 12 frames of wiggle room before the client caps its audio frame queue
    private static final int AUDIO_TICKER_FRAMES = 32 - 12;

    protected final SonusAgentPlugin plugin;

    protected final Set<UUID> connectedPlayers = ConcurrentHashMap.newKeySet();

    public SonusAgentApiImpl(SonusAgentPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isConnected(Player player) {
        return this.connectedPlayers.contains(player.getUniqueId());
    }

    @Override
    public void registerAudioCategory(AudioCategory category) {
        RegisterAudioCategoryMessage packet = new RegisterAudioCategoryMessage();
        packet.setCategory(category);
        this.plugin.addDefinition(packet);
    }

    @Override
    public AudioPlayer createAudioPlayer(
            Player player, UUID channelId, @Nullable UUID categoryId,
            AudioSupplier audio, AudioProcessor.Mode mode
    ) {
        AudioProcessor processor = this.createAudioProcessor(mode);
        AudioTicker ticker = new AudioTicker(audio, processor, AUDIO_TICKER_FRAMES);
        return new AudioPlayer(
                player.getUniqueId(), channelId, categoryId,
                ticker, this.plugin::sendMetaPacket
        );
    }

    @Override
    public AudioProcessor createAudioProcessor(AudioProcessor.Mode mode) {
        return new AudioProcessor(this.plugin.getOpusNatives(), () -> this.plugin.getSonusConfig().mtu, mode);
    }

    @Override
    public OpusNativesLoader getOpusNatives() {
        return this.plugin.getOpusNatives();
    }

    @Override
    public LameNativesLoader getLameNatives() {
        return this.plugin.getLameNatives();
    }

    public Set<UUID> getConnectedPlayers() {
        return this.connectedPlayers;
    }
}
