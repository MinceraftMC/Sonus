package dev.minceraft.sonus.service.api;

import dev.minceraft.sonus.api.service.ISonusServiceApi;
import dev.minceraft.sonus.api.service.audio.AudioCategory;
import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.api.service.manager.ISonusEventManager;
import dev.minceraft.sonus.api.service.manager.ISonusPlayerManager;
import dev.minceraft.sonus.api.service.manager.ISonusRoomManager;
import dev.minceraft.sonus.common.adapter.adapter.SonusAdapter;
import dev.minceraft.sonus.common.audio.SonusAudio;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.api.audio.ApiAudio;
import dev.minceraft.sonus.service.api.manager.ApiEventManager;
import dev.minceraft.sonus.service.api.manager.ApiPlayerManager;
import dev.minceraft.sonus.service.api.manager.ApiRoomManager;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ApiServiceImpl implements ISonusServiceApi {

    private @MonotonicNonNull SonusService service;
    private @MonotonicNonNull ApiPlayerManager playerManager;
    private @MonotonicNonNull ApiRoomManager roomManager;
    private @MonotonicNonNull ApiEventManager eventManager;

    public static void init(SonusService service) {
        ApiServiceImpl instance = (ApiServiceImpl) ISonusServiceApi.getInstance();
        instance.init0(service);
    }

    private void init0(SonusService service) {
        this.service = service;
        this.playerManager = new ApiPlayerManager(service.getPlayerManager());
        this.roomManager = new ApiRoomManager(service.getRoomManager());
        this.eventManager = new ApiEventManager(service);
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
    public ISonusAudio audioFromPcm(long sequence, short[] pcm) {
        return new ApiAudio(SonusAudio.fromPcm(sequence, pcm));
    }

    @Override
    public ISonusAudio audioFromOpus(long sequence, byte[] opus) {
        return new ApiAudio(SonusAudio.fromOpus(sequence, opus));
    }
}
