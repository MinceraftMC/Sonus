package dev.minceraft.sonus.api.service.manager;

import dev.minceraft.sonus.api.service.event.ISonusEvents;

public interface ISonusEventManager {

    void registerListener(ISonusEvents events);
}
