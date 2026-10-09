package dev.minceraft.sonus.service.api.participant;

import dev.minceraft.sonus.api.service.participant.ISonusAudioCategory;
import dev.minceraft.sonus.common.protocol.audio.AudioCategory;
import dev.minceraft.sonus.service.api.ApiDelegation;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public class ApiSonusAudioCategory extends ApiDelegation<AudioCategory> implements ISonusAudioCategory {

    public ApiSonusAudioCategory(AudioCategory category) {
        super(category);
    }

    @Override
    public UUID getUniqueId() {
        return this.delegate.getUniqueId();
    }

    @Override
    public Component getName() {
        return this.delegate.getName();
    }

    @Override
    public @Nullable Component getDescription() {
        return this.delegate.getDescription();
    }
}
