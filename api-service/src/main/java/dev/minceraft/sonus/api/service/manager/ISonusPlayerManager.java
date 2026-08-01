package dev.minceraft.sonus.api.service.manager;

import dev.minceraft.sonus.api.service.participant.builtin.ISonusServicePlayer;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

@NullMarked
public interface ISonusPlayerManager {

    @Nullable
    ISonusServicePlayer getPlayer(UUID uniqueId);

    @Unmodifiable
    Collection<? extends ISonusServicePlayer> getPlayers();

}
