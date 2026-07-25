package dev.minceraft.sonus.service.participant;

import dev.minceraft.sonus.service.data.ISonusPlayer;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface IAudioParticipant {

    UUID getUniqueId(@Nullable ISonusPlayer viewer);

    default @Nullable UUID getUniqueId() {
        return this.getUniqueId(null);
    }

}
