package dev.minceraft.sonus.service.agent;

import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.Table;
import dev.minceraft.sonus.service.IAudioSource;
import dev.minceraft.sonus.service.audio.AudioProcessor;
import dev.minceraft.sonus.service.audio.SonusAudio;
import dev.minceraft.sonus.service.data.SonusPlayerState;
import dev.minceraft.sonus.service.data.WorldRotatedVec3d;
import dev.minceraft.sonus.protocol.meta.IMetaHandler;
import dev.minceraft.sonus.protocol.meta.servicebound.AudioStreamMessage;
import dev.minceraft.sonus.protocol.meta.servicebound.BackendTickMessage;
import dev.minceraft.sonus.protocol.meta.servicebound.RegisterAudioCategoryMessage;
import dev.minceraft.sonus.protocol.meta.servicebound.UpdateRoomDefinitionMessage;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.player.PlayerManager;
import dev.minceraft.sonus.service.player.SonusPlayer;
import dev.minceraft.sonus.service.server.SonusServer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@NullMarked
public class AgentListener implements IMetaHandler, AutoCloseable {

    private final SonusService service;
    private final SonusServer server;

    private final LoadingCache<UUID, AudioProcessor> processorCache = CacheBuilder.newBuilder()
            .expireAfterAccess(15L, TimeUnit.MINUTES)
            .removalListener((RemovalListener<UUID, AudioProcessor>) notification -> {
                AudioProcessor processor = notification.getValue();
                if (processor != null) {
                    processor.close();
                }
            })
            .ticker(Ticker.systemTicker())
            .build(new CacheLoader<>() {
                @Override
                public AudioProcessor load(UUID key) {
                    return AgentListener.this.service.createAudioProcessor(AudioProcessor.Mode.AUDIO);
                }
            });

    public AgentListener(SonusService service, SonusServer server) {
        this.service = service;
        this.server = server;
    }

    @Override
    public void handleBackendTick(BackendTickMessage message) {
        this.handlePositions(message.getPositions());
        this.handlePerPlayerStates(message.getPerPlayerStates());
        this.handleTeams(message.getTeams());
    }

    @Override
    public void handleUpdateRoomDefinition(UpdateRoomDefinitionMessage message) {
        this.service.getRoomManager().updateRoomDefinition(this.server.getUniqueId(), message.getDefinition());
    }

    @Override
    public void handleAudioStream(AudioStreamMessage message) {
        SonusPlayer player = this.service.getPlayerManager().getPlayer(message.getPlayerId());
        if (player == null || player.getAdapter() == null) {
            return; // no player found or no adapter set yet
        }
        List<AudioStreamMessage.Frame> frames = message.getFrames();
        if (frames.isEmpty()) {
            return; // no frames sent
        }
        // ensure category is sent to the player if set
        UUID categoryId = message.getCategoryId();
        if (categoryId != null) {
            this.server.ensureCategory(player, categoryId);
        }

        // send all frames at once, the client will queue them (at most 32)
        UUID channelId = message.getChannelId();
        IAudioSource source = new IAudioSource.Static(channelId, categoryId);
        Supplier<AudioProcessor> processor = () -> this.processorCache.getUnchecked(channelId);
        for (AudioStreamMessage.Frame frame : frames) {
            SonusAudio audio = SonusAudio.fromOpus(frame.sequence(), frame.data()).setProcessor(processor);
            player.sendStaticAudio(source, audio);
        }
    }

    @Override
    public void handleRegisterAudioCategory(RegisterAudioCategoryMessage message) {
        this.server.registerCategory(message.getCategory());
    }

    private void handlePositions(@Nullable Map<UUID, WorldRotatedVec3d> positions) {
        if (positions == null) {
            return;
        }
        PlayerManager playerManager = this.service.getPlayerManager();
        for (Map.Entry<UUID, WorldRotatedVec3d> entry : positions.entrySet()) {
            SonusPlayer player = playerManager.getPlayer(entry.getKey());
            if (player != null) {
                player.setPosition(entry.getValue());
            }
        }
    }

    private void handlePerPlayerStates(@Nullable Table<UUID, UUID, SonusPlayerState> perPlayerStates) {
        if (perPlayerStates == null) {
            return;
        }
        PlayerManager playerManager = this.service.getPlayerManager();
        for (Map.Entry<UUID, Map<UUID, SonusPlayerState>> row : perPlayerStates.rowMap().entrySet()) {
            SonusPlayer player = playerManager.getPlayer(row.getKey());
            if (player == null) {
                continue;
            }
            player.updateStates(row.getValue());
        }
    }

    private void handleTeams(@Nullable Map<UUID, @Nullable String> teams) {
        if (teams == null) {
            return;
        }
        PlayerManager playerManager = this.service.getPlayerManager();
        for (Map.Entry<UUID, @Nullable String> entry : teams.entrySet()) {
            SonusPlayer player = playerManager.getPlayer(entry.getKey());
            if (player == null) {
                continue;
            }
            player.setTeam(entry.getValue());
        }
    }

    @Override
    public void close() {
        this.processorCache.invalidateAll();
    }
}
