package dev.minceraft.sonus.web.adapter.connection;

import dev.minceraft.sonus.common.adapter.data.ISonusServer;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.web.adapter.WebAdapter;
import dev.minceraft.sonus.web.adapter.rtc.RtcHandler;
import dev.minceraft.sonus.web.protocol.AbstractWebPacket;
import dev.minceraft.sonus.web.protocol.packets.WebSocketPacket;
import dev.minceraft.sonus.web.protocol.packets.clientbound.ConnectedPacket;
import dev.minceraft.sonus.web.protocol.packets.clientbound.VoiceActivityPacket;
import dev.minceraft.sonus.web.protocol.packets.servicebound.VolumePacket;
import io.netty.channel.Channel;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.kyori.adventure.text.Component.text;

@NullMarked
public class WebSocketConnection implements AutoCloseable {

    private final WebAdapter adapter;
    private final ISonusPlayer player;
    private final Channel channel;

    private final WebSocketPacketHandler packetHandler = new WebSocketPacketHandler(this);
    private int version = -1;

    private final Set<UUID> voiceActivity = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Float> categoryVolumeMap = new ConcurrentHashMap<>();
    private final Map<UUID, Float> playerVolumeMap = new ConcurrentHashMap<>();

    public WebSocketConnection(WebAdapter adapter, ISonusPlayer player, Channel channel) {
        this.adapter = adapter;
        this.player = player;
        this.channel = channel;
    }

    public void setVoiceActive(UUID playerId, boolean active) {
        // only send packets if the state has changed
        if (active && this.voiceActivity.add(playerId)
                || !active && this.voiceActivity.remove(playerId)) {
            this.sendPacket(new VoiceActivityPacket(playerId, active));
        }
    }

    public float getVolume(VolumePacket.VolumeType volumeType, UUID entryId) {
        return (switch (volumeType) {
            case CATEGORY -> this.categoryVolumeMap;
            case PLAYER -> this.playerVolumeMap;
        }).getOrDefault(entryId, 1f);
    }

    public void setVolume(VolumePacket.VolumeType volumeType, UUID entryId, float volume) {
        (switch (volumeType) {
            case CATEGORY -> this.categoryVolumeMap;
            case PLAYER -> this.playerVolumeMap;
        }).put(entryId, Math.clamp(volume, 0f, 2f));
    }

    public void sendConnected() {
        UUID playerId = this.player.getUniqueId();
        Component username = this.player.renderComponent(text(this.player.getName()));

        // send some info about the current server
        UUID serverId = this.player.getServerId();
        ISonusServer server = serverId != null ? this.adapter.getService().getPlayerManager().getServer(serverId) : null;
        Component serverName = server != null ? this.player.renderComponent(server.getName()) : null;
        String serverType = server != null ? server.getType() : null;

        this.sendPacket(new ConnectedPacket(playerId, username, serverId, serverName, serverType));
    }

    public void sendPacket(AbstractWebPacket<?> packet) {
        if (packet instanceof WebSocketPacket) {
            this.channel.writeAndFlush(packet);
        }
    }

    public ISonusPlayer getPlayer() {
        return this.player;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public WebAdapter getAdapter() {
        return this.adapter;
    }

    public WebSocketPacketHandler getWebSocketHandler() {
        return this.packetHandler;
    }

    public RtcHandler getRtc() {
        return this.adapter.getWebRtc().getPeer(this);
    }

    @Override
    public void close() {
        this.channel.close();
    }

    @Override
    public String toString() {
        return "WebSocket[" + this.player.getUniqueId() + ' ' + this.channel + ']';
    }
}
