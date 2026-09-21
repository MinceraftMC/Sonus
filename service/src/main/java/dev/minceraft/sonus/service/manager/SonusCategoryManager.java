package dev.minceraft.sonus.service.manager;

import dev.minceraft.sonus.common.adapter.adapter.SonusAdapter;
import dev.minceraft.sonus.common.adapter.service.ICategoryManager;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.common.protocol.audio.AudioCategory;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.participant.SonusPlayer;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public class SonusCategoryManager implements ICategoryManager {

    private final SonusService service;
    private final Map<UUID, CategoryData> categories = new ConcurrentHashMap<>();

    public SonusCategoryManager(SonusService service) {
        this.service = service;
    }

    public void ensureCategory(ISonusPlayer player, UUID categoryId) {
        CategoryData categoryData = this.categories.get(categoryId);
        if (categoryData == null) {
            return; // unknown category
        }
        SonusAdapter adapter = (SonusAdapter) player.getAdapter();
        if (adapter == null) {
            return; // no updater set yet
        } else if (!categoryData.informedPlayers().add(player.getUniqueId())) {
            return; // player already knew about category
        }
        // send registration packet
        adapter.registerCategory(player, categoryData.category());
    }

    @Override
    public void registerCategory(AudioCategory category) {
        this.registerCategory(category, CategorySource.API);
    }

    public void registerCategory(AudioCategory category, CategorySource source) {
        this.categories.put(category.getUniqueId(), new CategoryData(category, source));
    }

    @Override
    public void unregisterCategory(UUID categoryId) {
        CategoryData removed = this.categories.remove(categoryId);
        if (removed == null) {
            return;
        }
        for (UUID informedPlayer : removed.informedPlayers()) {
            SonusPlayer player = this.service.getPlayerManager().getPlayer(informedPlayer);
            if (player == null) {
                continue;
            }
            SonusAdapter adapter = player.getAdapter();
            if (adapter == null) {
                continue;
            }
            adapter.unregisterCategory(player, categoryId);
        }
    }

    public void onDisconnect(SonusPlayer player) {
        for (CategoryData categoryData : this.categories.values()) {
            categoryData.informedPlayers().remove(player.getUniqueId());
        }
    }

    public record CategoryData(
            Set<UUID> informedPlayers,
            AudioCategory category,
            CategorySource source
    ) {

        public CategoryData(AudioCategory category, CategorySource source) {
            this(ConcurrentHashMap.newKeySet(), category, source);
        }
    }

    public enum CategorySource {
        AGENT,
        API
    }
}
