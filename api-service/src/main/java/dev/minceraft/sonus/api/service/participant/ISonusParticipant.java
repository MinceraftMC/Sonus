package dev.minceraft.sonus.api.service.participant;

import dev.minceraft.sonus.api.service.ISonusServicePlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public interface ISonusParticipant {

    UUID getUniqueId(@Nullable ISonusServicePlayer viewer);

    default @Nullable UUID getUniqueId() {
        return this.getUniqueId(null);
    }

}
