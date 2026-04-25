package dev.minceraft.sonus.api.service.manager;

import dev.minceraft.sonus.api.service.ISonusServicePlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public interface IPlayerManager {

    @Nullable
    ISonusServicePlayer getPlayer(UUID uniqueId);
}
