package dev.minceraft.sonus.service.player;
// Created by booky10 in Sonus (02:18 17.07.2025)

import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import dev.minceraft.sonus.service.IPlayerManager;
import dev.minceraft.sonus.service.service.IScheduledTask;
import dev.minceraft.sonus.service.SonusConfig;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.platform.IPlatformPlayer;
import dev.minceraft.sonus.service.platform.IServer;
import dev.minceraft.sonus.service.server.SonusServer;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@NullMarked
public final class PlayerManager implements IPlayerManager {

    private final SonusService service;
    private final Map<UUID, SonusPlayer> players = new ConcurrentHashMap<>();
    private final LoadingCache<UUID, SonusServer> serverCache;

    public PlayerManager(SonusService service) {
        this.service = service;

        this.serverCache = CacheBuilder.newBuilder()
                .weakValues().ticker(Ticker.systemTicker())
                .build(new CacheLoader<>() {
                    @Override
                    public SonusServer load(UUID serverId) {
                        IServer server = service.getPlatform().getServer(serverId);
                        return new SonusServer(service, server);
                    }
                });

        this.service.getConfigHolder().addReloadHookAndRun(new Consumer<>() {

            private @MonotonicNonNull IScheduledTask task;

            @Override
            public void accept(SonusConfig config) {
                if (this.task != null) {
                    this.task.cancel();
                }
                int keepAliveInterval = config.getKeepAliveMs();
                this.task = PlayerManager.this.service.getScheduler().schedule(PlayerManager.this::tickKeepAlive,
                        0, keepAliveInterval, TimeUnit.MILLISECONDS);
            }
        });
    }

    private void tickKeepAlive() {
        long currentTime = System.currentTimeMillis();
        for (SonusPlayer player : this.players.values()) {
            player.tickKeepAlive(currentTime);
        }
    }

    public boolean unregisterPlayer(UUID playerId) {
        try (SonusPlayer removed = this.players.remove(playerId)) {
            if (removed == null) {
                return false;
            }
            removed.disconnect();
            return true;
        }
    }

    @Override
    public @Nullable SonusPlayer getPlayer(UUID playerId) {
        SonusPlayer player = this.players.get(playerId);
        if (player == null) {
            IPlatformPlayer platform = this.service.getPlatform().getPlayer(playerId);
            if (platform != null && platform.isOnline()) {
                player = new SonusPlayer(this.service, platform);
                this.players.put(playerId, player);
            }
        }
        return player;
    }

    @Override
    public Collection<SonusPlayer> getPlayers() {
        return this.players.values();
    }

    @Override
    public SonusServer getServer(UUID serverId) {
        return this.serverCache.getUnchecked(serverId);
    }

    @Override
    public void disableOnBackendSwitch(UUID playerId) {
        SonusPlayer player = this.getPlayer(playerId);
        if (player != null) {
            this.disablePlayer(player);
        }
    }

    public void disablePlayer(SonusPlayer player) {
        player.setVoiceActive(false, false); // prevent packet sending
        player.setMuted(true);
        player.setDeafened(true);
        player.setAdapter(null);
    }

    public void onPlayerSwitchBackend(UUID playerId) {
        SonusPlayer player = this.getPlayer(playerId);
        if (player == null) {
            return;
        }
        player.clearStates(); // reset player states on server switch
        player.setTeam(null);

        // skip broadcasting packets if player isn't conected
        if (player.isConnected()) {
            player.updateState(); // broadcast update

            for (SonusPlayer target : this.players.values()) {
                if (target.getPrimaryRoom() == null || target == player) {
                    continue; // no primary room set, ignore
                }
                // show skin of target for this player
                if (player.canReceive(target)) {
                    player.ensureTabListed(target);
                }
                // show skin of this player to target (if this player is in primary room)
                if (player.getPrimaryRoom() != null && target.canReceive(player)) {
                    target.ensureTabListed(player);
                }
            }

            player.sendConnectionToAgent();
        }
    }
}
