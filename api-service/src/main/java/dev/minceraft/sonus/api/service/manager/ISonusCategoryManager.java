package dev.minceraft.sonus.api.service.manager;

import dev.minceraft.sonus.api.service.participant.ISonusAudioCategory;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface ISonusCategoryManager {

    default ISonusAudioCategory createAudioCategory(Component name, @Nullable Component description) {
        return this.createAudioCategory(UUID.randomUUID(), name, description);
    }

    ISonusAudioCategory createAudioCategory(UUID id, Component name, @Nullable Component description);

    void registerAudioCategory(ISonusAudioCategory category);

    default void unregisterAudioCategory(ISonusAudioCategory category){
        this.unregisterAudioCategory(category.getUniqueId());
    }

    void unregisterAudioCategory(UUID categoryId);
}
