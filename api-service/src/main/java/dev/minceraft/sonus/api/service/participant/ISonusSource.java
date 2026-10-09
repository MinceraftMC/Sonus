package dev.minceraft.sonus.api.service.participant;

import dev.minceraft.sonus.api.service.util.WorldRotatedVec3d;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * The sonus audio source
 */
@NullMarked
public interface ISonusSource extends ISonusParticipant {

    /**
     * The source can be categorized
     *
     * @return the category id
     */
    default @Nullable UUID getCategoryId() {
        return null;
    }

    /**
     * The source can be bound to a server
     *
     * @return the server id
     */
    default @Nullable UUID getServerId() {
        return null;
    }

    /**
     * The source can be bound to a location
     *
     * @return the location
     */
    default @Nullable WorldRotatedVec3d getPosition() {
        return null;
    }
}
