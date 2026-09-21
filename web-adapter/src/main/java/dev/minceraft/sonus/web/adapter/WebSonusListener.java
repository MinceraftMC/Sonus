package dev.minceraft.sonus.web.adapter;

import dev.minceraft.sonus.common.adapter.service.ISonusServiceEvents;
import dev.minceraft.sonus.common.data.SonusPlayerState;
import dev.minceraft.sonus.common.participant.builtin.IRoom;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.web.adapter.connection.WebSocketConnection;
import dev.minceraft.sonus.web.protocol.model.SonusWebPlayerState;
import dev.minceraft.sonus.web.protocol.model.SonusWebRoom;
import dev.minceraft.sonus.web.protocol.packets.clientbound.RoomAddPacket;
import dev.minceraft.sonus.web.protocol.packets.clientbound.RoomRemovePacket;
import dev.minceraft.sonus.web.protocol.packets.clientbound.StateRemovePacket;
import dev.minceraft.sonus.web.protocol.packets.clientbound.StateUpdatePacket;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class WebSonusListener implements ISonusServiceEvents {

    private final WebAdapter adapter;

    public WebSonusListener(WebAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onPlayerSwitchBackend(UUID playerId) {
        WebSocketConnection connection = this.adapter.getSessions().getConnection(playerId);
        if (connection != null) {
            connection.sendConnected();
        }
    }

    @Override
    public void onPlayerDisconnect(ISonusPlayer player) {
        this.adapter.getSessions().removeSession(player.getUniqueId());
        this.adapter.getSessions().broadcastFrom(player, new StateRemovePacket(player.getUniqueId()));
    }

    @Override
    public void onPlayerStateUpdate(ISonusPlayer player, boolean globalUpdate) {
        this.adapter.getSessions().broadcastFrom(player, !globalUpdate, connection -> {
            if (player.isConnected()) {
                return new StateUpdatePacket(SonusWebPlayerState.fromState(player, connection.getPlayer()));
            } else {
                return new StateRemovePacket(player.getUniqueId(connection.getPlayer()));
            }
        });
    }

    @Override
    public void onPlayerNickUpdate(ISonusPlayer player, UUID previousNick) {
        this.adapter.getSessions().broadcastFrom(player, false, __ ->
                new StateRemovePacket(previousNick));

        this.onPlayerStateUpdate(player, true);
    }

    @Override
    public void onGroupCreate(IRoom room) {
        this.adapter.getSessions().broadcast(connection ->
                new RoomAddPacket(SonusWebRoom.fromRoom(room, connection.getPlayer())));
    }

    @Override
    public void onGroupRemove(IRoom room) {
        this.adapter.getSessions().broadcast(new RoomRemovePacket(room.getUniqueId()));
    }

    @Override
    public void onConnectionState(ISonusPlayer player) {
        if (this.adapter.getSessions().getConnection(player.getUniqueId()) != null) {
            this.sendConnectionStateMessage(player, player.isVoiceActive());
        }
    }

    @Override
    public void onPlayerVisibilityStateUpdate(ISonusPlayer player, ISonusPlayer target, SonusPlayerState state) {
        WebSocketConnection connection = this.adapter.getSessions().getConnection(player.getUniqueId());
        if (connection == null) {
            return;
        }
        if (state.isFullyHidden()) {
            StateRemovePacket packet = new StateRemovePacket();
            packet.setPlayerId(target.getUniqueId());
            connection.sendPacket(packet);
        } else if (player.canReceive(target)) {
            StateUpdatePacket packet = new StateUpdatePacket();
            packet.setState(SonusWebPlayerState.fromState(target, player));
            connection.sendPacket(packet);
        }
    }

    public void sendConnectionStateMessage(ISonusPlayer player, boolean connected) {
        player.sendMessage(Component.translatable(connected
                ? "sonus.web.connection.active"
                : "sonus.web.connection.inactive"));
    }
}
