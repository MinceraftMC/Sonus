package dev.minceraft.sonus.service.server;
// Created by booky10 in Sonus (01:05 17.11.2025)

import dev.minceraft.sonus.common.adapter.SonusAdapter;
import dev.minceraft.sonus.api.service.audio.AudioCategory;
import dev.minceraft.sonus.common.data.ISonusServer;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.platform.IServer;
import dev.minceraft.sonus.service.player.PlayerManager;
import dev.minceraft.sonus.service.player.SonusPlayer;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public final class SonusServer implements ISonusServer {

    private final SonusService service;
    private final IServer platform;

    private final Map<UUID, CategoryData> categories = new ConcurrentHashMap<>();

    public SonusServer(SonusService service, IServer platform) {
        this.service = service;
        this.platform = platform;
    }

    public void ensureCategory(SonusPlayer player, UUID categoryId) {
        CategoryData categoryData = this.categories.get(categoryId);
        if (categoryData == null) {
            return; // unknown category
        }
        SonusAdapter adapter = player.getAdapter();
        if (adapter == null) {
            return; // no updater set yet
        } else if (!categoryData.informedPlayers().add(player.getUniqueId())) {
            return; // player already knew about category
        }
        // send registration packet
        adapter.registerCategory(player, categoryData.category());
    }

    public void registerCategory(AudioCategory category) {
        CategoryData newCategory = new CategoryData(category);
        CategoryData prevCategory = this.categories.put(category.uniqueId(), newCategory);
        if (prevCategory == null) {
            return; // didn't replace anything
        }

        PlayerManager players = this.service.getPlayerManager();
        for (UUID playerId : prevCategory.informedPlayers()) {
            SonusPlayer player = players.getPlayer(playerId);
            if (player == null || !this.platform.getUniqueId().equals(player.getServerId())) {
                continue; // player offline or not on server
            }
            SonusAdapter adapter = player.getAdapter();
            if (adapter == null) {
                continue; // no voice adapter set
            }
            // remove category and re-add to ensure it is updated correctly
            adapter.unregisterCategory(player, prevCategory.category().uniqueId());
            adapter.registerCategory(player, category);
            newCategory.informedPlayers().add(player.getUniqueId());
        }
    }

    public void onDisconnect(SonusPlayer player) {
        for (CategoryData category : this.categories.values()) {
            category.informedPlayers.remove(player.getUniqueId());
        }
    }

    @Override
    public UUID getUniqueId() {
        return this.platform.getUniqueId();
    }

    @Override
    public Component getName() {
        return this.platform.getName();
    }

    @Override
    public @Nullable String getType() {
        return this.platform.getType();
    }

    public record CategoryData(
            Set<UUID> informedPlayers,
            AudioCategory category
    ) {

        public CategoryData(AudioCategory category) {
            this(ConcurrentHashMap.newKeySet(), category);
        }
    }
}
