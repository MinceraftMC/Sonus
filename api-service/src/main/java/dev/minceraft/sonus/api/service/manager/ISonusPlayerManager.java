package dev.minceraft.sonus.api.service.manager;

import dev.minceraft.sonus.api.service.participant.builtin.ISonusServicePlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

/**
 * The sonus service player manager
 */
@NullMarked
public interface ISonusPlayerManager {

    /**
     * Gets the {@link ISonusServicePlayer} from an id
     *
     * @param uniqueId the players id
     * @return the player
     */
    @Nullable
    ISonusServicePlayer getPlayer(UUID uniqueId);

    /**
     * Get all sonus players.
     * <p>
     * <strong>WARNING: This contains all players, also not sonus connected players. See {@link #getActivePlayers()}</strong>
     *
     * @return the players
     */
    Collection<? extends ISonusServicePlayer> getPlayers();

    /**
     * Get all active sonus players.
     *
     * @return the players.
     */
    default Collection<? extends ISonusServicePlayer> getActivePlayers() {
        Collection<? extends ISonusServicePlayer> players = this.getPlayers();
        players.removeIf(player -> !player.isVoiceActive());
        return players;
    }
}
