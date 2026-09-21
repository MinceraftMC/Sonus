package dev.minceraft.sonus.agent.paper;
// Created by booky10 in Sonus (01:39 17.07.2025)

import dev.minceraft.sonus.agent.paper.api.SonusAgentApi;
import dev.minceraft.sonus.agent.paper.api.SonusAgentApiImpl;
import dev.minceraft.sonus.agent.paper.config.SonusAgentConfig;
import dev.minceraft.sonus.common.config.YamlConfigHolder;
import dev.minceraft.sonus.common.natives.LameNativesLoader;
import dev.minceraft.sonus.common.natives.OpusNativesLoader;
import dev.minceraft.sonus.common.participant.builtin.RoomDefinition;
import dev.minceraft.sonus.protocol.meta.IMetaMessage;
import dev.minceraft.sonus.protocol.meta.MetaRegistry;
import dev.minceraft.sonus.protocol.meta.servicebound.UpdateRoomDefinitionMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static dev.minceraft.sonus.common.SonusConstants.PLUGIN_MESSAGE_CHANNEL;

@NullMarked
public class SonusAgentPlugin extends JavaPlugin {

    private @MonotonicNonNull SonusAgentApiImpl api;
    private @MonotonicNonNull YamlConfigHolder<? extends SonusAgentConfig> config;
    private @Nullable YamlConfigHolder<RoomDefinition> roomDefinition;
    private final List<IMetaMessage> definitions = new ArrayList<>();

    protected @MonotonicNonNull OpusNativesLoader opusNatives;
    protected @MonotonicNonNull LameNativesLoader lameNatives;

    protected void loadNatives() {
        this.opusNatives = new OpusNativesLoader();
        this.lameNatives = new LameNativesLoader();
    }

    protected SonusAgentApiImpl createApi() {
        return new SonusAgentApiImpl(this);
    }

    protected YamlConfigHolder<? extends SonusAgentConfig> createConfig() {
        Path configPath = this.getDataPath().resolve("config.yml");
        return new YamlConfigHolder<>(SonusAgentConfig.class, SonusAgentConfig::new, configPath);
    }

    protected AgentListener createAgentListener() {
        return new AgentListener(this);
    }

    protected AgentMessageListener createAgentMessageListener() {
        return new AgentMessageListener(this);
    }

    @Override
    public void onLoad() {
        this.loadNatives();

        this.config = this.createConfig();
        this.config.reloadConfig();

        this.api = this.createApi();
        Bukkit.getServicesManager().register(SonusAgentApi.class, this.api, this, ServicePriority.Normal);
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this.createAgentListener(), this);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, PLUGIN_MESSAGE_CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, PLUGIN_MESSAGE_CHANNEL, this.createAgentMessageListener());

        this.loadRoomDefinition();
    }

    @Override
    public void onDisable() {
        try (OpusNativesLoader ignoredOpusNatives = this.opusNatives;
             LameNativesLoader ignoredLameNatives = this.lameNatives) {
            this.opusNatives = null;
            this.lameNatives = null;
        }
    }

    public boolean sendMetaPacket(IMetaMessage packet) {
        byte[] data = MetaRegistry.write(packet);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isConnected()) {
                continue; // not connected
            }
            // send to first player who has our agent messaging channel registered
            if (player.getListeningPluginChannels().contains(PLUGIN_MESSAGE_CHANNEL)) {
                player.sendPluginMessage(this, PLUGIN_MESSAGE_CHANNEL, data);
                return true;
            }
        }
        return false; // no valid connection found
    }

    public void loadRoomDefinition() {
        Path definitionPath = this.getDataPath().resolve("room-definition.yml");
        if (Files.exists(definitionPath)) {
            this.getLogger().info("Loading room definition from file...");
            this.roomDefinition = new YamlConfigHolder<>(RoomDefinition.class, RoomDefinition::new, definitionPath);
            this.roomDefinition.reloadConfig();
        }
    }

    public @MonotonicNonNull SonusAgentApiImpl getApi() {
        return this.api;
    }

    public SonusAgentConfig getSonusConfig() {
        return this.config.getDelegate();
    }

    public @Nullable RoomDefinition getRoomDefinition() {
        return this.roomDefinition == null ? null : this.roomDefinition.getDelegate();
    }

    public void addDefinition(IMetaMessage message) {
        this.definitions.add(message);
        this.sendMetaPacket(message);
    }

    public void broadcastDefinitions() {
        if (this.roomDefinition != null) {
            UpdateRoomDefinitionMessage packet = new UpdateRoomDefinitionMessage();
            packet.setDefinition(this.roomDefinition.getDelegate());
            this.sendMetaPacket(packet);
        }
        for (IMetaMessage definition : this.definitions) {
            this.sendMetaPacket(definition);
        }
    }

    public OpusNativesLoader getOpusNatives() {
        return this.opusNatives;
    }

    public LameNativesLoader getLameNatives() {
        return this.lameNatives;
    }
}
