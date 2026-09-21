package dev.minceraft.sonus.common.participant;

import dev.minceraft.sonus.common.data.WorldRotatedVec3d;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
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


    record Static(UUID senderId, @Nullable UUID categoryId) implements IAudioSource {

        @Override
        public UUID getUniqueId(@Nullable ISonusPlayer viewer) {
            return null;
        }

        @Override
        public @Nullable UUID getCategoryId() {
            return this.categoryId;
        }
    }
}
