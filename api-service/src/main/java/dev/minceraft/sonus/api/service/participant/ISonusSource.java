package dev.minceraft.sonus.api.service.participant;

import dev.minceraft.sonus.api.service.data.WorldRotatedVec3d;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface ISonusSource extends ISonusParticipant {

    default @Nullable UUID getCategoryId() {
        return null;
    }

    default @Nullable UUID getServerId() {
        return null;
    }

    default @Nullable WorldRotatedVec3d getPosition() {
        return null;
    }
}
