package dev.minceraft.sonus.common.adapter.service;

import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.common.protocol.audio.AudioCategory;

import java.util.UUID;

public interface ICategoryManager {

    void registerCategory(AudioCategory category);

    void ensureCategory(ISonusPlayer player, UUID categoryId);

    void unregisterCategory(UUID categoryId);
}
