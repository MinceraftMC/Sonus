package dev.minceraft.sonus.service;

import dev.minceraft.sonus.service.audio.AudioProcessor;
import dev.minceraft.sonus.service.config.ISonusConfig;
import dev.minceraft.sonus.service.config.YamlConfigHolder;
import dev.minceraft.sonus.service.natives.OpusNativesLoader;
import dev.minceraft.sonus.common.protocol.tcp.IPluginMessenger;
import dev.minceraft.sonus.common.protocol.udp.IUdpServer;
import dev.minceraft.sonus.service.service.ISonusEventManager;
import dev.minceraft.sonus.service.service.ISonusRoomManager;
import dev.minceraft.sonus.service.service.ISonusScheduler;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;

@NullMarked
public interface ISonusService {

    IUdpServer getUdpServer();

    IPluginMessenger getPluginMessenger();

    ISonusConfig getConfig();

    YamlConfigHolder<? extends ISonusConfig> getConfigHolder();

    Path getDataDirectory();

    ISonusEventManager getEventManager();

    ISonusScheduler getScheduler();

    ISonusRoomManager getRoomManager();

    IPlayerManager getPlayerManager();

    OpusNativesLoader getOpusNatives();

    default AudioProcessor createAudioProcessor(AudioProcessor.Mode mode) {
        return new AudioProcessor(this.getOpusNatives(), () -> this.getConfig().getMtuSize(), mode);
    }
}
