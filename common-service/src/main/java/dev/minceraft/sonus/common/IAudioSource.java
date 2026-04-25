package dev.minceraft.sonus.common;
// Created by booky10 in Sonus (02:23 17.07.2025)

import dev.minceraft.sonus.api.service.ISonusServicePlayer;
import dev.minceraft.sonus.api.service.participant.ISonusSource;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public interface IAudioSource extends ISonusSource {

    record Static(UUID senderId, @Nullable UUID categoryId) implements IAudioSource {

        @Override
        public @Nullable UUID getCategoryId() {
            return this.categoryId;
        }

        @Override
        public UUID getUniqueId(@Nullable ISonusServicePlayer viewer) {
            return this.senderId;
        }
    }
}
