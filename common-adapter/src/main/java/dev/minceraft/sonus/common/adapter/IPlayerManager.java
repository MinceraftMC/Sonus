package dev.minceraft.sonus.common.adapter;

import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.common.adapter.data.ISonusServer;

import java.util.Collection;
import java.util.UUID;

public interface IPlayerManager {

    ISonusPlayer getPlayer(UUID playerId);

    Collection<? extends ISonusPlayer> getPlayers();

    ISonusServer getServer(UUID serverId);

    void disableOnBackendSwitch(UUID playerId);
}
