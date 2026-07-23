package dev.minceraft.sonus.plasmo.adapter;

import dev.minceraft.sonus.service.data.ISonusPlayer;
import dev.minceraft.sonus.service.data.SonusPlayerState;
import dev.minceraft.sonus.service.rooms.IRoom;
import dev.minceraft.sonus.service.service.ISonusServiceEvents;
import dev.minceraft.sonus.plasmo.adapter.connection.PlasmoConnection;
import dev.minceraft.sonus.plasmo.protocol.PlasmoPmChannels;
import dev.minceraft.sonus.plasmo.protocol.tcp.clientbound.PlayerDisconnectPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.clientbound.PlayerInfoRequestPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.clientbound.PlayerInfoUpdatePacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.clientbound.SourceLineRegisterPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.VoicePlayerInfo;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.VoiceSourceLine;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;

@NullMarked
public class PlasmoSonusListener implements ISonusServiceEvents {

    private static final Logger LOGGER = LoggerFactory.getLogger("Sonus");

    private final PlasmoAdapter adapter;

    public PlasmoSonusListener(PlasmoAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onPlayerSwitchBackend(UUID playerId) {
        // remove backend-specific session
        if (this.adapter.getSessionManager().removeSession(playerId)) {
            // disable sonus during backend switch
            this.adapter.getService().getPlayerManager().disableOnBackendSwitch(playerId);
        }
    }

    @Override
    public void onPlayerDisconnect(ISonusPlayer player) {
        // remove backend-specific session
        this.adapter.getSessionManager().removeSession(player.getUniqueId());

        // completely remove player
        PlayerDisconnectPacket packet = new PlayerDisconnectPacket();
        packet.setUniqueId(player.getUniqueId());
        this.adapter.getSessionManager().broadcastFrom(player, packet);
    }

    @Override
    public void onPlayerStateUpdate(ISonusPlayer player, boolean globalUpdate) {
        this.adapter.getSessionManager().broadcastFrom(player, !globalUpdate, connection -> {
            if (player.isConnected()) {
                PlayerInfoUpdatePacket packet = new PlayerInfoUpdatePacket();
                packet.setPlayerInfo(this.adapter.getSessionManager().buildPlayerInfo(connection.getPlayer(), player));

                return packet;
            } else {
                PlayerDisconnectPacket packet = new PlayerDisconnectPacket();
                packet.setUniqueId(player.getUniqueId());

                return packet;
            }
        });
    }

    @Override
    public void onPlayerNickUpdate(ISonusPlayer player, UUID previousNick) {
        PlayerDisconnectPacket packet = new PlayerDisconnectPacket();
        packet.setUniqueId(previousNick);
        this.adapter.getSessionManager().broadcastFrom(player, false, __ -> packet);

        this.onPlayerStateUpdate(player, true);
    }

    @Override
    public void onPrimaryRoomJoined(ISonusPlayer player, IRoom room) {
        PlasmoSessionManager sessionManager = this.adapter.getSessionManager();
        PlasmoConnection connection = sessionManager.getConnectionByUniqueId(player.getUniqueId());
        if (connection == null) {
            return;
        }
        for (ISonusPlayer member : room.getMembers()) {
            PlasmoConnection plasmoConnection = sessionManager.getConnectionByUniqueId(member.getUniqueId());
            connection.unregisterSourceInfo(member.getUniqueId()); // unregister source infos
            if (plasmoConnection == null) {
                continue; // member not connected via plasmo
            }
            plasmoConnection.unregisterSourceInfo(player.getUniqueId());
        }

        VoiceSourceLine sourceLine = new VoiceSourceLine(
                room.getId().toString(),
                room.getName(),
                PlasmoConstants.DEFAULT_GROUP_ICON,
                1.0,
                200,
                Set.of()
        );
        connection.registerVoiceSourceLine(room.getId(), sourceLine);

        SourceLineRegisterPacket packet = new SourceLineRegisterPacket();
        packet.setSourceLine(sourceLine);

        connection.sendPacket(packet);
    }

    @Override
    public void onPrimaryRoomLeaved(ISonusPlayer player, IRoom room) {
        PlasmoSessionManager sessionManager = this.adapter.getSessionManager();
        PlasmoConnection connection = sessionManager.getConnectionByUniqueId(player.getUniqueId());
        if (connection == null) {
            return;
        }
        for (ISonusPlayer member : room.getMembers()) {
            PlasmoConnection plasmoConnection = sessionManager.getConnectionByUniqueId(member.getUniqueId());
            connection.unregisterSourceInfo(member.getUniqueId()); // unregister source infos
            if (plasmoConnection == null) {
                continue; // member not connected via plasmo
            }
            plasmoConnection.unregisterSourceInfo(player.getUniqueId());
        }
        this.adapter.unregisterCategory(player, room.getId());
    }

    @Override
    public void onChannelRegistered(UUID playerId, Set<Key> channel) {
        if (!channel.contains(PlasmoPmChannels.CHANNEL)) {
            return;
        }
        ISonusPlayer player = this.adapter.getService().getPlayerManager().getPlayer(playerId);
        if (player == null) {
            return;
        }
        if (!player.setAdapter(this.adapter)) {
            return; // player is not allowed to use this adapter
        }

        PlasmoConnection connection = this.adapter.getSessionManager().createConnection(player);
        if (connection == null) {
            LOGGER.warn("Player '{}' registered plasmo channels, but this player is not known!", playerId);
            return;
        }
        player.setConnected(true);

        PlayerInfoRequestPacket packet = new PlayerInfoRequestPacket();
        connection.sendPacket(packet);
    }

    @Override
    public void onPlayerVisibilityStateUpdate(ISonusPlayer player, ISonusPlayer target, SonusPlayerState state) {
        PlasmoConnection connection = this.adapter.getSessionManager().getConnectionByUniqueId(player.getUniqueId());
        if (connection == null) {
            return;
        }
        if (state.isFullyHidden()) {
            PlayerDisconnectPacket packet = new PlayerDisconnectPacket();
            packet.setUniqueId(target.getUniqueId());
            connection.sendPacket(packet);
        } else if (player.canReceive(target)) {
            PlayerInfoUpdatePacket packet = new PlayerInfoUpdatePacket();
            VoicePlayerInfo plasmoState = this.adapter.getSessionManager().buildPlayerInfo(player, connection.getPlayer());
            packet.setPlayerInfo(plasmoState);

            connection.sendPacket(packet);
        }
    }
}
