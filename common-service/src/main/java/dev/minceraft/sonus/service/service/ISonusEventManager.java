package dev.minceraft.sonus.service.service;

public interface ISonusEventManager extends ISonusServiceEvents {

    void registerListener(ISonusServiceEvents events);
}
