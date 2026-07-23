package dev.minceraft.sonus.service;
// Created by booky10 in Sonus (01:33 17.07.2025)

import com.google.gson.Gson;
import dev.minceraft.sonus.service.config.ISonusConfig;
import dev.minceraft.sonus.service.config.YamlConfigHolder;
import dev.minceraft.sonus.service.natives.OpusNativesLoader;
import dev.minceraft.sonus.service.natives.SpeexNativesLoader;
import dev.minceraft.sonus.common.protocol.udp.IUdpServer;
import dev.minceraft.sonus.service.service.ISonusEventManager;
import dev.minceraft.sonus.service.service.ISonusRoomManager;
import dev.minceraft.sonus.service.service.ISonusScheduler;
import dev.minceraft.sonus.service.adapter.AdapterManager;
import dev.minceraft.sonus.service.agent.AgentManager;
import dev.minceraft.sonus.service.commands.CommandHolder;
import dev.minceraft.sonus.service.commands.builtin.SonusCommand;
import dev.minceraft.sonus.service.network.UdpServer;
import dev.minceraft.sonus.service.platform.IServicePlatform;
import dev.minceraft.sonus.service.player.PlayerManager;
import dev.minceraft.sonus.service.rooms.SonusRoomManager;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@NullMarked
public final class SonusService implements ISonusService {

    private static final Logger LOGGER = LoggerFactory.getLogger("Sonus");
    public static final Gson GSON = new Gson();

    private final IServicePlatform platform;
    private final PlayerManager players;
    private final CommandHolder commands = new CommandHolder();
    private final SonusPluginMessenger pluginMessageListener = new SonusPluginMessenger(this);
    private final SonusEventManager eventManager = new SonusEventManager(this);
    private final SonusScheduler scheduler = new SonusScheduler();
    private final SonusRoomManager roomManager = new SonusRoomManager(this);
    private final AdapterManager adapters = new AdapterManager(this);
    private final AgentManager agentManager = new AgentManager(this);
    private final YamlConfigHolder<SonusConfig> config;
    private @MonotonicNonNull UdpServer udpServer;

    private @Nullable OpusNativesLoader opusNatives = new OpusNativesLoader();
    private @Nullable SpeexNativesLoader speexNatives = new SpeexNativesLoader();

    public SonusService(IServicePlatform platform) {
        this.platform = platform;
        Path configPath = this.platform.getDataPath().resolve("config.yml");
        this.config = new YamlConfigHolder<>(SonusConfig.class, SonusConfig::createWithTemplates, configPath);
        this.players = new PlayerManager(this);
    }

    public void init() {
        // constructed here to prevent class leaks in netty thread local map
        this.udpServer = new UdpServer(this);

        LOGGER.info("Initializing sonus service...");
        this.adapters.load();

        LOGGER.info("Loading configuration...");
        this.config.reloadConfig();

        this.adapters.init();
        this.roomManager.init();
        this.agentManager.init();
        this.udpServer.bind();

        this.initCommands();
    }

    private void initCommands() {
        new SonusCommand(this).register(this.commands);

        // register all commands in platform command registrar
        this.platform.registerCommands(this.commands.getNodes());
    }

    public void shutdown() {
        LOGGER.info("Shutting down sonus service...");
        this.adapters.shutdown();

        if (this.udpServer != null) {
            this.udpServer.shutdown();
        }
        this.scheduler.shutdown();

        try (OpusNativesLoader ignoredOpusNatives = this.opusNatives;
             SpeexNativesLoader ignoredSpeexNatives = this.speexNatives) {
            this.opusNatives = null;
            this.speexNatives = null;
        }
    }

    public IServicePlatform getPlatform() {
        return this.platform;
    }

    public CommandHolder getCommandHolder() {
        return this.commands;
    }

    @Override
    public SonusPluginMessenger getPluginMessenger() {
        return this.pluginMessageListener;
    }

    public AdapterManager getAdapters() {
        return this.adapters;
    }

    @Override
    public ISonusConfig getConfig() {
        return this.config.getDelegate();
    }

    @Override
    public YamlConfigHolder<SonusConfig> getConfigHolder() {
        return this.config;
    }

    @Override
    public Path getDataDirectory() {
        return this.platform.getDataPath();
    }

    @Override
    public IUdpServer getUdpServer() {
        return this.udpServer;
    }

    @Override
    public ISonusEventManager getEventManager() {
        return this.eventManager;
    }

    @Override
    public ISonusScheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    public ISonusRoomManager getRoomManager() {
        return this.roomManager;
    }

    @Override
    public PlayerManager getPlayerManager() {
        return this.players;
    }

    @Override
    public OpusNativesLoader getOpusNatives() {
        if (this.opusNatives == null) {
            throw new IllegalStateException("Opus natives have already been closed");
        }
        return this.opusNatives;
    }

    public SpeexNativesLoader getSpeexNatives() {
        if (this.speexNatives == null) {
            throw new IllegalStateException("Speex natives have already been closed");
        }
        return this.speexNatives;
    }
}
