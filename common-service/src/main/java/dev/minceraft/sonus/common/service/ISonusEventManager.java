package dev.minceraft.sonus.common.service;

public interface ISonusEventManager extends ISonusServiceEvents {

    void registerListener(ISonusServiceEvents events);
}
