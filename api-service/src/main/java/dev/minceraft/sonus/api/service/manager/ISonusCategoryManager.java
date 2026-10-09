package dev.minceraft.sonus.api.service.manager;

import dev.minceraft.sonus.api.service.participant.ISonusAudioCategory;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * Manager for audio categories
 */
public interface ISonusCategoryManager {

    /**
     * Creates a new audio category with a random UUID.
     * <p/>
     * <strong>WARNING: The audio category must be registered afterward: {@link #registerAudioCategory(ISonusAudioCategory)}</strong>
     *
     * @param name        the name of the audio category
     * @param description the description of the audio category, can be null
     * @return the created audio category
     */
    default ISonusAudioCategory createAudioCategory(Component name, @Nullable Component description) {
        return this.createAudioCategory(UUID.randomUUID(), name, description);
    }

    /**
     * Creates a new audio category with the given UUID.
     * <p/>
     * <strong>WARNING: The audio category must be registered afterward: {@link #registerAudioCategory(ISonusAudioCategory)}</strong>
     *
     * @param id the UUID of the audio category
     * @param name        the name of the audio category
     * @param description the description of the audio category, can be null
     * @return the created audio category
     */
    ISonusAudioCategory createAudioCategory(UUID id, Component name, @Nullable Component description);

    /**
     * Registers an audio category to the manager.
     * @param category must be created by {@link #createAudioCategory(Component, Component)} or {@link #createAudioCategory(UUID, Component, Component)}
     */
    void registerAudioCategory(ISonusAudioCategory category);

    /**
     * Unregisters an audio category from the manager.
     * @param category must be created by {@link #createAudioCategory(Component, Component)} or {@link #createAudioCategory(UUID, Component, Component)}
     */
    default void unregisterAudioCategory(ISonusAudioCategory category) {
        this.unregisterAudioCategory(category.getUniqueId());
    }

    /**
     * Unregisters an audio category from the manager.
     * @param categoryId the unique id of the audio category
     */
    void unregisterAudioCategory(UUID categoryId);
}
