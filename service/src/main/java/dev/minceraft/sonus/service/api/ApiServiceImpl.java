package dev.minceraft.sonus.service.api;

import dev.minceraft.sonus.api.service.ISonusServiceApi;
import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.api.service.manager.ISonusCategoryManager;
import dev.minceraft.sonus.api.service.manager.ISonusEventManager;
import dev.minceraft.sonus.api.service.manager.ISonusPlayerManager;
import dev.minceraft.sonus.api.service.manager.ISonusRoomManager;
import dev.minceraft.sonus.api.service.participant.builtin.ISonusBasicSource;
import dev.minceraft.sonus.common.audio.SonusAudio;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.api.audio.ApiAudio;
import dev.minceraft.sonus.service.api.manager.ApiCategoryManager;
import dev.minceraft.sonus.service.api.manager.ApiEventManager;
import dev.minceraft.sonus.service.api.manager.ApiPlayerManager;
import dev.minceraft.sonus.service.api.manager.ApiRoomManager;
import dev.minceraft.sonus.service.api.participant.builtin.ApiBasicSource;
import dev.minceraft.sonus.service.participant.BasicAudioSource;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ApiServiceImpl implements ISonusServiceApi {

    private @MonotonicNonNull SonusService service;
    private @MonotonicNonNull ApiPlayerManager playerManager;
    private @MonotonicNonNull ApiRoomManager roomManager;
    private @MonotonicNonNull ApiEventManager eventManager;
    private @MonotonicNonNull ApiCategoryManager categoryManager;

    public static void init(SonusService service) {
        ApiServiceImpl instance = (ApiServiceImpl) ISonusServiceApi.getInstance();
        instance.init0(service);
    }

    private void init0(SonusService service) {
        this.service = service;
        this.playerManager = new ApiPlayerManager(service.getPlayerManager());
        this.roomManager = new ApiRoomManager(service.getRoomManager());
        this.eventManager = new ApiEventManager(service);
        this.categoryManager = new ApiCategoryManager(service.getCategoryManager());
    }

    @Override
    public ISonusPlayerManager getPlayerManager() {
        return this.playerManager;
    }

    @Override
    public ISonusRoomManager getRoomManager() {
        return this.roomManager;
    }

    @Override
    public ISonusEventManager getEventManager() {
        return this.eventManager;
    }

    @Override
    public ISonusCategoryManager getCategoryManager() {
        return this.categoryManager;
    }

    @Override
    public ISonusAudio audioFromPcm(long sequence, short[] pcm) {
        return new ApiAudio(SonusAudio.fromPcm(sequence, pcm));
    }

    @Override
    public ISonusAudio audioFromOpus(long sequence, byte[] opus) {
        return new ApiAudio(SonusAudio.fromOpus(sequence, opus));
    }

    @Override
    public ISonusBasicSource createBasicSource() {
        return new ApiBasicSource(new BasicAudioSource());
    }
}
