package dev.minceraft.sonus.common;

import dev.minceraft.sonus.common.audio.AudioProcessor;
import dev.minceraft.sonus.common.config.ISonusConfig;
import dev.minceraft.sonus.common.config.YamlConfigHolder;
import dev.minceraft.sonus.common.natives.OpusNativesLoader;
import dev.minceraft.sonus.common.protocol.tcp.IPluginMessenger;
import dev.minceraft.sonus.common.protocol.udp.IUdpServer;
import dev.minceraft.sonus.common.service.ISonusEventManager;
import dev.minceraft.sonus.common.service.ISonusRoomManager;
import dev.minceraft.sonus.common.service.ISonusScheduler;
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
