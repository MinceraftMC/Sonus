package dev.minceraft.sonus.api.service.participant;

import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public interface ISonusAudioCategory {

    UUID getUniqueId();

    Component getName();

    @Nullable
    Component getDescription();
}
