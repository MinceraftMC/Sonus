package dev.minceraft.sonus.svc.adapter.connection;


import dev.minceraft.sonus.common.SonusConstants;
import dev.minceraft.sonus.common.adapter.ISonusService;
import dev.minceraft.sonus.common.adapter.service.ISonusRoomManager;
import dev.minceraft.sonus.common.participant.builtin.IRoom;
import dev.minceraft.sonus.svc.adapter.SvcProtocolAdapter;
import dev.minceraft.sonus.svc.protocol.meta.IMetaSvcHandler;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.JoinedGroupSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.clientbound.SecretSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.servicebound.CreateGroupSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.servicebound.JoinGroupSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.servicebound.LeaveGroupSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.servicebound.RequestSecretSvcPacket;
import dev.minceraft.sonus.svc.protocol.meta.servicebound.UpdateStateSvcPacket;

import java.net.InetSocketAddress;
import java.util.UUID;

import static net.kyori.adventure.text.Component.translatable;

public class MetaHandler implements IMetaSvcHandler {

    private final SvcProtocolAdapter protocolAdapter;
    private final SvcConnection connection;

    public MetaHandler(SvcProtocolAdapter protocolAdapter, SvcConnection connection) {
        this.protocolAdapter = protocolAdapter;
        this.connection = connection;
    }

    private void tryJoinRoom(UUID roomId, String password) {
        IRoom room = this.protocolAdapter.getAdapter().getService().getRoomManager().getRoom(roomId);
        boolean success = room != null && this.connection.getPlayer().canAccessRoom(room, password);
        if (success) {
            // if player can access room, update primary room
            this.connection.getPlayer().setPrimaryRoom(room);
        }

        // send response
        JoinedGroupSvcPacket response = new JoinedGroupSvcPacket();
        response.setGroupId(success ? roomId : null);
        response.setWrongPassword(!success);
        this.connection.sendPacket(response);
    }

    @Override
    public void handleCreateGroupPacket(CreateGroupSvcPacket packet) {
        if (!this.connection.getPlayer().hasPermission(SonusConstants.PERMISSION_GROUPS_USE, true)) {
            this.connection.getPlayer().sendMessage(translatable("sonus.command.moderate.groups.ban.notified"));
            return;
        }

        ISonusRoomManager rooms = this.protocolAdapter.getAdapter().getService().getRoomManager();
        IRoom room = rooms.createStaticRoom(packet.getName(), packet.getPassword(),
                packet.getType().toSonus(), false);
        this.tryJoinRoom(room.getUniqueId(), packet.getPassword());
    }

    @Override
    public void handleJoinGroupPacket(JoinGroupSvcPacket packet) {
        this.tryJoinRoom(packet.getGroupId(), packet.getPassword());
    }

    @Override
    public void handleLeaveGroupPacket(LeaveGroupSvcPacket packet) {
        this.connection.getPlayer().setPrimaryRoom(null);
        // send response
        JoinedGroupSvcPacket response = new JoinedGroupSvcPacket();
        response.setGroupId(null);
        this.connection.sendPacket(response);
    }

    @Override
    public void handleRequestSecretPacket(RequestSecretSvcPacket packet) {
        // Version check is done in the handler
        this.connection.setVersion(packet.getCompatibilityVersion());

        ISonusService service = this.protocolAdapter.getAdapter().getService();
        InetSocketAddress remoteAddress = service.getUdpServer().getRemoteAddress();

        SecretSvcPacket secretSvcPacket = new SecretSvcPacket();
        secretSvcPacket.setSecret(this.connection.getSecret());
        secretSvcPacket.setServerPort(remoteAddress.getPort());
        secretSvcPacket.setPlayerId(this.connection.getPlayer().getUniqueId(this.connection.getPlayer()));
        secretSvcPacket.setCodec(this.protocolAdapter.getAdapter().getService().getConfig().getOpusCodec());
        secretSvcPacket.setMtuSize(service.getConfig().getMtuSize());
        secretSvcPacket.setKeepAlive(service.getConfig().getKeepAliveMs());
        secretSvcPacket.setGroupsEnabled(true); // Sonus requires groups to be enabled
        secretSvcPacket.setVoiceHost(remoteAddress.getHostString());
        secretSvcPacket.setAllowRecording(service.getConfig().allowRecordings());

        this.connection.sendPacket(secretSvcPacket);
    }

    @Override
    public void handleUpdateStatePacket(UpdateStateSvcPacket packet) {
        this.connection.setDisabled(packet.isDisabled());
    }
}
