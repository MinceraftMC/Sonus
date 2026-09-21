package dev.minceraft.sonus.common.adapter.service;

public interface ISonusEventManager extends ISonusServiceEvents {

    void registerListener(ISonusServiceEvents events);
}
