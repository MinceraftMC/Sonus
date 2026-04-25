package dev.minceraft.sonus.common;

import dev.minceraft.sonus.common.data.ISonusPlayer;
import dev.minceraft.sonus.common.data.ISonusServer;

import java.util.Collection;
import java.util.UUID;

public interface IPlayerManager {

    ISonusPlayer getPlayer(UUID playerId);

    Collection<? extends ISonusPlayer> getPlayers();

    ISonusServer getServer(UUID serverId);

    void disableOnBackendSwitch(UUID playerId);
}
