package dev.minceraft.sonus.service;
// Created by booky10 in Sonus (02:23 17.07.2025)

import dev.minceraft.sonus.service.data.ISonusPlayer;
import dev.minceraft.sonus.service.data.WorldRotatedVec3d;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public interface IAudioSource {

    UUID getSenderId(@Nullable ISonusPlayer viewer);

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
        public UUID getSenderId(ISonusPlayer viewer) {
            return this.senderId;
        }

        @Override
        public @Nullable UUID getCategoryId() {
            return this.categoryId;
        }
    }
}
