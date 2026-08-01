package dev.minceraft.sonus.api.service;

import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.api.service.manager.ISonusEventManager;
import dev.minceraft.sonus.api.service.manager.ISonusPlayerManager;
import dev.minceraft.sonus.api.service.manager.ISonusRoomManager;
import net.kyori.adventure.util.Services;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ISonusServiceApi {

    ISonusPlayerManager getPlayerManager();

    ISonusRoomManager getRoomManager();

    ISonusEventManager getEventManager();

    ISonusAudio audioFromPcm(long sequence, short[] pcm);

    ISonusAudio audioFromOpus(long sequence, byte[] opus);

    static ISonusServiceApi getInstance() {
        if (InstanceHolder.API == null) {
            try {
                InstanceHolder.API = Services.service(ISonusServiceApi.class).orElseThrow();
            } catch (Exception exception) {
                throw new IllegalStateException("The Sonus Service Api has not been initialized yet. " +
                        "Please make sure to use this api after Sonus has been loaded.", exception);
            }
        }
        return InstanceHolder.API;
    }
}
