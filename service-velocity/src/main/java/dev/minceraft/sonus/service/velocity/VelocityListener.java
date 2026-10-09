package dev.minceraft.sonus.service.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.PlayerChannelRegisterEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.agent.PluginMessageSourceImpl;
import dev.minceraft.sonus.service.participant.SonusPlayer;
import net.kyori.adventure.key.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VelocityListener {

    private static final Logger LOGGER = LoggerFactory.getLogger("Sonus");

    private final SonusService service;

    public VelocityListener(SonusService service) {
        this.service = service;
    }

    @Subscribe
    public void onPlayerSwitch(ServerPostConnectEvent event) {
        this.service.getEventManager().onPlayerSwitchBackend(event.getPlayer().getUniqueId());
    }

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        SonusPlayer player = this.service.getPlayerManager().getPlayer(playerId);
        // velocity fires disconnects even for players who got kicked for trying to log in twice,
        // so ensure we don't accidentally disconnect the player who is already connected
        if (player != null && ((VelocitySonusPlayer) player.getPlatform()).getHandle() == event.getPlayer()) {
            this.service.getEventManager().onPlayerQuit(player);
        }
    }

    @Subscribe
    public void onChannelRegistered(PlayerChannelRegisterEvent event) {
        Set<Key> channels = new HashSet<>(event.getChannels().size());
        for (ChannelIdentifier channel : event.getChannels()) {
            channels.add(Key.key(channel.getId()));
        }
        this.service.getEventManager().onChannelRegistered(
                event.getPlayer().getUniqueId(), channels);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        Key channel = ((MinecraftChannelIdentifier) event.getIdentifier()).asKey();
        ServicePlatformVelocity platform = (ServicePlatformVelocity) this.service.getPlatform();

        PluginMessageSourceImpl source;
        if (event.getSource() instanceof Player player) {
            source = new PluginMessageSourceImpl.Player(platform.getPlayer(player));
        } else if (event.getTarget() instanceof Player player) {
            source = new PluginMessageSourceImpl.Server(platform.getPlayer(player));
        } else {
            throw new IllegalStateException("Plugin message event source or target is not a player");
        }

        if (this.service.getPluginMessenger().handleMessage(channel, source, event.getData())) {
            event.setResult(PluginMessageEvent.ForwardResult.handled());
        }
    }
}
