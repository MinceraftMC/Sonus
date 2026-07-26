package dev.minceraft.sonus.service.api.manager;

import dev.minceraft.sonus.api.service.ISonusServicePlayer;
import dev.minceraft.sonus.api.service.manager.ISonusPlayerManager;
import dev.minceraft.sonus.common.adapter.IPlayerManager;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.service.api.ApiDelegation;
import dev.minceraft.sonus.service.api.ApiSonusPlayer;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NullMarked
public class ApiPlayerManager extends ApiDelegation<IPlayerManager> implements ISonusPlayerManager {

    public ApiPlayerManager(IPlayerManager delegate) {
        super(delegate);
    }

    @Override
    public @Nullable ISonusServicePlayer getPlayer(UUID uniqueId) {
        return new ApiSonusPlayer(this.delegate.getPlayer(uniqueId));
    }

    @Override
    public @Unmodifiable Collection<? extends ISonusServicePlayer> getPlayers() {
        Set<ApiSonusPlayer> players = new HashSet<>();
        for (ISonusPlayer player : this.delegate.getPlayers()) {
            players.add(new ApiSonusPlayer(player));
        }

        return players;
    }
}
