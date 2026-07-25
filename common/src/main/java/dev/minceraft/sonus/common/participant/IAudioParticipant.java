package dev.minceraft.sonus.common.participant;

import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface IAudioParticipant {

    UUID getUniqueId(@Nullable ISonusPlayer viewer);

    default UUID getUniqueId() {
        return this.getUniqueId(null);
    }

}
