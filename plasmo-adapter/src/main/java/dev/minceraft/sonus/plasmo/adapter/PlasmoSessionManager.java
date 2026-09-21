package dev.minceraft.sonus.plasmo.adapter;

import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.plasmo.adapter.connection.PlasmoConnection;
import dev.minceraft.sonus.plasmo.protocol.AbstractPlasmoPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.VoicePlayerInfo;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static dev.minceraft.sonus.common.SonusConstants.PERMISSION_CONNECT_PLASMO;

@NullMarked
public class PlasmoSessionManager {

    private final PlasmoAdapter adapter;
    private final Map<UUID, PlasmoConnection> usersByUniqueId = new ConcurrentHashMap<>();
    private final Map<UUID, PlasmoConnection> usersBySecret = new ConcurrentHashMap<>();

    public PlasmoSessionManager(PlasmoAdapter adapter) {
        this.adapter = adapter;
    }

    public void broadcastFrom(ISonusPlayer source, AbstractPlasmoPacket<?> packet) {
        this.broadcastFrom(source, true, __ -> packet);
    }

    public void broadcastFrom(ISonusPlayer source, boolean requireVisibility, Function<PlasmoConnection, AbstractPlasmoPacket<?>> packet) {
        for (PlasmoConnection conn : this.usersByUniqueId.values()) {
            if (!conn.isVoiceActive()) {
                continue;
            }
            ISonusPlayer targetPlayer = conn.getPlayer();
            if (!targetPlayer.canSee(source)) {
                continue;
            }
            if (requireVisibility && !source.canReceive(source)) {
                continue;
            }

            targetPlayer.ensureTabListed(source);
            conn.sendPacket(packet.apply(conn));
        }
    }

    public boolean removeSession(UUID playerId) {
        try (PlasmoConnection conn = this.usersByUniqueId.remove(playerId)) {
            if (conn != null) {
                this.usersBySecret.remove(conn.getSecret());
            }
            return conn != null;
        }
    }

    public Map<UUID, VoicePlayerInfo> getPlayerInfos(PlasmoConnection listener) {
        Collection<? extends ISonusPlayer> players = this.adapter.getService().getPlayerManager().getPlayers();
        Map<UUID, VoicePlayerInfo> states = new HashMap<>(players.size());
        for (ISonusPlayer player : players) {
            if (!player.isVoiceActive() || !player.canReceive(listener.getPlayer())) {
                continue;
            }
            states.put(player.getUniqueId(), this.buildPlayerInfo(listener.getPlayer(), player));
        }
        return states;
    }

    @Nullable
    public PlasmoConnection getConnectionBySecret(UUID secret) {
        return this.usersBySecret.get(secret);
    }

    @Nullable
    public PlasmoConnection getConnectionByUniqueId(UUID uniqueId) {
        return this.usersByUniqueId.get(uniqueId);
    }

    @Nullable
    public PlasmoConnection createConnection(ISonusPlayer sonusPlayer) {
        if (!sonusPlayer.hasPermission(PERMISSION_CONNECT_PLASMO, true)) {
            return null;
        }
        PlasmoConnection plasmoConnection = new PlasmoConnection(this.adapter, sonusPlayer);
        this.usersBySecret.put(plasmoConnection.getSecret(), plasmoConnection);
        this.usersByUniqueId.put(sonusPlayer.getUniqueId(), plasmoConnection);
        return plasmoConnection;
    }

    public VoicePlayerInfo buildPlayerInfo(ISonusPlayer viewer, ISonusPlayer player) {
        return new VoicePlayerInfo(
                player.getUniqueId(viewer),
                player.getName(viewer),
                !player.isVoiceActive(),
                player.isDeafened(),
                player.isMuted()
        );
    }
}
