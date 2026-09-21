package dev.minceraft.sonus.api.service.participant;

import dev.minceraft.sonus.api.service.participant.builtin.ISonusServicePlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * The base sonus participant
 */
@NullMarked
public interface ISonusParticipant {

    /**
     * Get the id of the participant. It respects nick systems.
     * If you want the internal id, use {@link #getUniqueId()} instead.
     *
     * @param viewer the viewer watching this participant
     * @return the id, can be the nicked id
     */
    UUID getUniqueId(@Nullable ISonusServicePlayer viewer);

    /**
     * Get the id of the participant. It is always the internal id.
     * Use {@link #getUniqueId(ISonusServicePlayer)} instead if you want to consider the nick system.
     *
     * @return the internal id
     */
    default @Nullable UUID getUniqueId() {
        return this.getUniqueId(null);
    }

}
