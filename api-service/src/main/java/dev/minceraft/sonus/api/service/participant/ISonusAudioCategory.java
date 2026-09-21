package dev.minceraft.sonus.api.service.participant;

import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * An audio category can be used to group audio sources together.
 * A client will show this category with name, description, and a volume slider as example.
 */
@NullMarked
public interface ISonusAudioCategory {

    /**
     * Get the unique id of this category.
     *
     * @return the unique id of this category
     */
    UUID getUniqueId();

    /**
     * Get the name of this category.
     *
     * @return the name of this category
     */
    Component getName();

    /**
     * Get the description of this category.
     *
     * @return the description of this category, or null if there is no description
     */
    @Nullable
    Component getDescription();
}
