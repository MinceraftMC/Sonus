package dev.minceraft.sonus.api.service.manager;

import dev.minceraft.sonus.api.service.event.ISonusEvents;
import org.jspecify.annotations.NullUnmarked;

/**
 * The Sonus server event manager
 */
@NullUnmarked
public interface ISonusEventManager {

    /**
     * Registers an event listener
     *
     * @param events
     */
    void registerListener(ISonusEvents events);
}
