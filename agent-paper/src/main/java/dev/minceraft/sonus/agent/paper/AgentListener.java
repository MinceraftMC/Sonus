package dev.minceraft.sonus.agent.paper;
// Created by booky10 in Sonus (01:39 17.07.2025)

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.google.common.collect.HashBasedTable;
import dev.minceraft.sonus.agent.paper.util.delta.DeltaTrackerMap;
import dev.minceraft.sonus.agent.paper.util.delta.DeltaTrackerTable;
import dev.minceraft.sonus.common.data.SonusPlayerState;
import dev.minceraft.sonus.common.data.WorldRotatedVec3d;
import dev.minceraft.sonus.protocol.meta.servicebound.BackendTickMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerHideEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.player.PlayerShowEntityEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static dev.minceraft.sonus.common.SonusConstants.PLUGIN_MESSAGE_CHANNEL;

@NullMarked
public class AgentListener implements Listener {

    protected final SonusAgentPlugin plugin;

    protected final DeltaTrackerMap<UUID, WorldRotatedVec3d> positions = new DeltaTrackerMap<>(HashMap::new);

    protected final DeltaTrackerTable<UUID, UUID, SonusPlayerState> playerStates = new DeltaTrackerTable<>(HashBasedTable::create);

    protected final Set<Map.Entry<Player, Player>> visibilityChanges = new HashSet<>();

    protected final DeltaTrackerMap<UUID, @Nullable String> teams = new DeltaTrackerMap<>(HashMap::new);

    protected boolean dirtyRoomDefinition = true;

    public AgentListener(SonusAgentPlugin plugin) {
        this.plugin = plugin;
    }

    protected SonusPlayerState buildState(Player player, Player target) {
        boolean staticHidden = !player.canSee(target);
        boolean spatialHidden = staticHidden
                || target.getGameMode() == GameMode.SPECTATOR
                && player.getGameMode() != GameMode.SPECTATOR;
        return new SonusPlayerState(target.getUniqueId(), staticHidden, spatialHidden);
    }

    protected boolean isPlayerIgnored(Player player) {
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        // specify initial player position
        Player player = event.getPlayer();
        this.onChangePos(player, player.getLocation());

        // set default player states for everything
        UUID playerId = player.getUniqueId();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player == target || this.isPlayerIgnored(target)) {
                continue;
            }
            UUID targetId = target.getUniqueId();

            // initialize state for both directions, as visibility is not necessarily symmetric
            this.playerStates.computeIfAbsent(targetId, playerId, () -> this.buildState(player, target));
            this.playerStates.computeIfAbsent(playerId, targetId, () -> this.buildState(target, player));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // remove cached data for player
        UUID playerId = event.getPlayer().getUniqueId();
        this.positions.removeSilent(playerId);
        this.playerStates.removeRowSilent(playerId);
        this.playerStates.removeColumnSilent(playerId);
        this.teams.removeSilent(playerId);

        // re-send room definition to service if everyone quits
        int playerCount = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!this.isPlayerIgnored(player)) {
                if (++playerCount > 1) {
                    break;
                }
            }
        }
        if (playerCount <= 1) {
            this.dirtyRoomDefinition = true;
        }

        this.plugin.getApi().getConnectedPlayers().remove(playerId);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGamemode(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (event.getNewGameMode() == GameMode.SPECTATOR
                || player.getGameMode() == GameMode.SPECTATOR) {
            // TODO better solution for this
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (player != target && !this.isPlayerIgnored(target)) {
                    this.visibilityChanges.add(Map.entry(player, target));
                    this.visibilityChanges.add(Map.entry(target, player));
                }
            }
        }
    }

    @EventHandler
    public void onHide(PlayerHideEntityEvent event) {
        if (event.getEntity() instanceof Player target) {
            this.visibilityChanges.add(Map.entry(event.getPlayer(), target));
        }
    }

    @EventHandler
    public void onShow(PlayerShowEntityEvent event) {
        if (event.getEntity() instanceof Player target) {
            this.visibilityChanges.add(Map.entry(event.getPlayer(), target));
        }
    }

    public void onChangePos(Player player, Location location) {
        NamespacedKey dimensionKey = location.getWorld().getKey();
        double posY = location.getY() + player.getEyeHeight();
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        WorldRotatedVec3d pos = new WorldRotatedVec3d(location.getX(), posY, location.getZ(), yaw, pitch, dimensionKey);
        this.positions.change(player.getUniqueId(), pos);
    }

    public void tickPosition(Player player) {
        WorldRotatedVec3d lastPos = this.positions.get(player.getUniqueId());
        if (lastPos == null) {
            this.onChangePos(player, player.getLocation());
            return;
        }
        Location currentPos = player.getLocation();
        if (currentPos.getX() != lastPos.getX() ||
                currentPos.getY() + player.getEyeHeight() != lastPos.getY() ||
                currentPos.getZ() != lastPos.getZ() ||
                currentPos.getYaw() != lastPos.getYaw() ||
                currentPos.getPitch() != lastPos.getPitch() ||
                !currentPos.getWorld().getKey().equals(lastPos.getDimension())) {
            this.onChangePos(player, currentPos);
        }
    }

    public void tickTeam(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team playerTeam = scoreboard.getPlayerTeam(player);
        String name = playerTeam == null ? null : playerTeam.getName();

        this.teams.change(player.getUniqueId(), name);
    }

    public void tickVisibilityChanges() {
        Iterator<Map.Entry<Player, Player>> it = this.visibilityChanges.iterator();
        while (it.hasNext()) {
            Map.Entry<Player, Player> entry = it.next();
            it.remove();
            SonusPlayerState state = this.buildState(entry.getKey(), entry.getValue());
            this.playerStates.change(entry.getKey().getUniqueId(), state.playerId(), state);
        }
    }

    @EventHandler
    public void onTickEnd(ServerTickEndEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.isPlayerIgnored(player)) {
                continue;
            }
            this.tickPosition(player);
            this.tickTeam(player);
        }
        this.tickVisibilityChanges();

        this.tickDirtyPlayerMeta();
    }

    public void tickDirtyPlayerMeta() {
        boolean hasChanges = false;
        BackendTickMessage packet = new BackendTickMessage();
        if (this.positions.isDirty()) {
            packet.setPositions(this.positions.getChanges());
            hasChanges = true;
        }
        if (this.playerStates.isDirty()) {
            packet.setPerPlayerStates(this.playerStates.getChanges());
            hasChanges = true;
        }
        if (this.teams.isDirty()) {
            packet.setTeams(this.teams.getChanges());
            hasChanges = true;
        }
        if (hasChanges) {
            this.plugin.sendMetaPacket(packet);
        }

        this.positions.clearChanges();
        this.playerStates.clearChanges();
        this.teams.clearChanges();
    }

    private void sendRoomDefinition() {
        if (this.dirtyRoomDefinition) {
            this.dirtyRoomDefinition = false;
            this.plugin.getLogger().info("Sending agent definition(s) to service");
            this.plugin.broadcastDefinitions();
        }
    }

    // try to send room definition only after a channel has been registered
    @EventHandler
    public void onAgentChannelRegistration(PlayerRegisterChannelEvent event) {
        if (PLUGIN_MESSAGE_CHANNEL.equals(event.getChannel())) {
            // try fire room definition for this server room to service, as
            // we don't have a connection to the service if there is no player connected
            this.sendRoomDefinition();
        }
    }
}
