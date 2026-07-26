package dev.minceraft.sonus.api.service;

import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.api.service.manager.ISonusPlayerManager;
import dev.minceraft.sonus.api.service.manager.ISonusRoomManager;

public interface ISonusServiceApi {

    ISonusPlayerManager getPlayerManager();

    ISonusRoomManager getRoomManager();

    ISonusAudio fromPcm(short[] pcm);

    ISonusAudio fromOpus(byte[] opus);
}
