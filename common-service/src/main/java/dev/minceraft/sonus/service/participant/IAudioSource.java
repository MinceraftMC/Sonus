package dev.minceraft.sonus.service.participant;

import dev.minceraft.sonus.service.data.WorldRotatedVec3d;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface IAudioSource extends IAudioParticipant {

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
