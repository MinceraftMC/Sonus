package dev.minceraft.sonus.service.api.manager;

import dev.minceraft.sonus.api.service.manager.ISonusCategoryManager;
import dev.minceraft.sonus.api.service.participant.ISonusAudioCategory;
import dev.minceraft.sonus.common.adapter.service.ICategoryManager;
import dev.minceraft.sonus.common.protocol.audio.AudioCategory;
import dev.minceraft.sonus.service.api.ApiDelegation;
import dev.minceraft.sonus.service.api.participant.ApiSonusAudioCategory;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class ApiCategoryManager extends ApiDelegation<ICategoryManager> implements ISonusCategoryManager {

    public ApiCategoryManager(ICategoryManager delegate) {
        super(delegate);
    }

    @Override
    public ISonusAudioCategory createAudioCategory(UUID id, Component name, @Nullable Component description) {
        return new ApiSonusAudioCategory(new AudioCategory(id, name, description));
    }

    @Override
    public void registerAudioCategory(ISonusAudioCategory category) {
        this.delegate.registerCategory(((ApiSonusAudioCategory) category).getDelegate());
    }

    @Override
    public void unregisterAudioCategory(UUID categoryId) {
        this.delegate.unregisterCategory(categoryId);
    }
}
