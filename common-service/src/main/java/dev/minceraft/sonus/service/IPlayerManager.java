package dev.minceraft.sonus.service;

import dev.minceraft.sonus.service.data.ISonusPlayer;
import dev.minceraft.sonus.service.data.ISonusServer;

import java.util.Collection;
import java.util.UUID;

public interface IPlayerManager {

    ISonusPlayer getPlayer(UUID playerId);

    Collection<? extends ISonusPlayer> getPlayers();

    ISonusServer getServer(UUID serverId);

    void disableOnBackendSwitch(UUID playerId);
}
