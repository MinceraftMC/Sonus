package dev.minceraft.sonus.service.api;

import dev.minceraft.sonus.api.service.ISonusServicePlayer;
import dev.minceraft.sonus.api.service.manager.ISonusPlayerManager;
import dev.minceraft.sonus.common.adapter.IPlayerManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
}
