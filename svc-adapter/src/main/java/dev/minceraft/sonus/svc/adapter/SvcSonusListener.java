package dev.minceraft.sonus.svc.adapter;

import dev.minceraft.sonus.common.data.SonusPlayerState;
import dev.minceraft.sonus.common.adapter.service.ISonusServiceEvents;
import dev.minceraft.sonus.common.participant.builtin.IRoom;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.svc.adapter.connection.SvcConnection;
import dev.minceraft.sonus.svc.protocol.data.SonusClientGroup;
import dev.minceraft.sonus.svc.protocol.data.SvcPlayerState;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.AddGroupSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.JoinedGroupSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.PlayerStateSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.RemoveGroupSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.RemovePlayerStatePacket;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

import static dev.minceraft.sonus.common.SonusConstants.PERMISSION_GROUPS_BYPASS_PASSWORD;

@NullMarked
public class SvcSonusListener implements ISonusServiceEvents {

    private final SvcAdapter adapter;

    public SvcSonusListener(SvcAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onPlayerSwitchBackend(UUID playerId) {
        // remove backend-specific session
        if (this.adapter.getSessions().removeSession(playerId)) {
            // disable sonus during backend switch
            this.adapter.getService().getPlayerManager().disableOnBackendSwitch(playerId);
        }
    }

    @Override
    public void onPlayerDisconnect(ISonusPlayer player) {
        // remove backend-specific session
        this.adapter.getSessions().removeSession(player.getUniqueId());
        // completely remove player
        RemovePlayerStatePacket packet = new RemovePlayerStatePacket();
        packet.setPlayerId(player.getUniqueId());
        this.adapter.getSessions().broadcastFrom(player, packet);
    }

    @Override
    public void onPlayerStateUpdate(ISonusPlayer player, boolean globalUpdate) {
        if (!player.isVoiceActive() || !player.isConnected()) {
            // if the player isn't connected and not in a primary room,
            // remove the state; if the player is in a primary room, still send state updates,
            // as the player would appear to be removed from the current primary room otherwise
            if (player.getPrimaryRoom() == null || !player.isConnected()) {
                RemovePlayerStatePacket packet = new RemovePlayerStatePacket();
                packet.setPlayerId(player.getUniqueId());
                this.adapter.getSessions().broadcastFrom(player, packet);
                return;
            }
        }
        // broadcast state update from the player
        this.adapter.getSessions().broadcastFrom(player, !globalUpdate, connection -> {
            PlayerStateSvcPacket packet = new PlayerStateSvcPacket();
            packet.setState(this.adapter.getSessions().buildPlayerState(connection.getPlayer(), player));

            // If the player himself is receiving an update, ensure his current room is synced
            if (connection.getPlayer() == player) {
                IRoom currentRoom = player.getPrimaryRoom();
                if (currentRoom == null) {
                    // Send empty JoinedGroup packet to indicate no current room
                    connection.sendPacket(new JoinedGroupSvcPacket());
                } else if (currentRoom.getUniqueId() != connection.getCurrentRoomId()) {
                    // if the player's primary room differs from the connection's current room,
                    // send a GroupJoined packet to update it

                    JoinedGroupSvcPacket joinPacket = new JoinedGroupSvcPacket();
                    joinPacket.setGroupId(currentRoom.getUniqueId());
                    connection.sendPacket(joinPacket);

                    connection.setCurrentRoomId(currentRoom.getUniqueId());
                }
            }

            return packet;
        });
    }

    @Override
    public void onPlayerNickUpdate(ISonusPlayer player, UUID previousNick) {
        RemovePlayerStatePacket packet = new RemovePlayerStatePacket();
        packet.setPlayerId(previousNick);
        this.adapter.getSessions().broadcastFrom(player, false, __ -> packet);

        this.onPlayerStateUpdate(player, true);
    }

    @Override
    public void onGroupCreate(IRoom room) {
        this.adapter.getSessions().broadcast(connection -> {
            boolean bypassPassword = connection.getPlayer().hasPermission(PERMISSION_GROUPS_BYPASS_PASSWORD, false);
            AddGroupSvcPacket packet = new AddGroupSvcPacket();
            SonusClientGroup group = new SonusClientGroup(room, bypassPassword);
            packet.setGroup(group);
            return packet;
        });
    }

    @Override
    public void onGroupRemove(IRoom room) {
        RemoveGroupSvcPacket packet = new RemoveGroupSvcPacket();
        packet.setGroupId(room.getUniqueId());
        this.adapter.getSessions().broadcast(packet);
    }

    @Override
    public void onPlayerVisibilityStateUpdate(ISonusPlayer player, ISonusPlayer target, SonusPlayerState state) {
        SvcConnection connection = this.adapter.getSessions().getConnection(player.getUniqueId());
        if (connection == null) {
            return;
        }
        if (state.isFullyHidden()) {
            RemovePlayerStatePacket packet = new RemovePlayerStatePacket();
            packet.setPlayerId(target.getUniqueId());
            connection.sendPacket(packet);
        } else if (player.canReceive(target)) {
            SvcPlayerState svcState = this.adapter.getSessions().buildPlayerState(connection.getPlayer(), target);

            PlayerStateSvcPacket packet = new PlayerStateSvcPacket();
            packet.setState(svcState);
            connection.sendPacket(packet);
        }
    }
}
