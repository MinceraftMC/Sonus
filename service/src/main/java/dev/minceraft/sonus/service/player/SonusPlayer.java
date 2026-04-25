package dev.minceraft.sonus.service.player;
// Created by booky10 in Sonus (02:18 17.07.2025)

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.api.service.data.Vec3d;
import dev.minceraft.sonus.api.service.data.WorldRotatedVec3d;
import dev.minceraft.sonus.api.service.participant.ISonusSource;
import dev.minceraft.sonus.common.IAudioSource;
import dev.minceraft.sonus.common.adapter.SonusAdapter;
import dev.minceraft.sonus.common.audio.SonusAudio;
import dev.minceraft.sonus.common.data.ISonusPlayer;
import dev.minceraft.sonus.common.data.SonusPlayerState;
import dev.minceraft.sonus.common.rooms.IRoom;
import dev.minceraft.sonus.protocol.meta.IMetaMessage;
import dev.minceraft.sonus.protocol.meta.MetaRegistry;
import dev.minceraft.sonus.protocol.meta.agentbound.PlayerConnectionStateMessage;
import dev.minceraft.sonus.protocol.meta.agentbound.TriggerCommandUpdateMessage;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.commands.CommandSender;
import dev.minceraft.sonus.service.platform.IPlatformPlayer;
import dev.minceraft.sonus.service.processing.nodes.AgcNode;
import dev.minceraft.sonus.service.processing.util.SpatialNormProcessor;
import dev.minceraft.sonus.service.rooms.ServerRoom;
import dev.minceraft.sonus.service.server.SonusServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static dev.minceraft.sonus.common.SonusConstants.PERMISSION_CONNECT;
import static dev.minceraft.sonus.common.SonusConstants.PERMISSION_GROUPS_BYPASS_PASSWORD;
import static dev.minceraft.sonus.common.SonusConstants.PERMISSION_GROUPS_USE;
import static dev.minceraft.sonus.common.SonusConstants.PERMISSION_VOICE_LISTEN;
import static dev.minceraft.sonus.common.SonusConstants.PERMISSION_VOICE_SPEAK;
import static dev.minceraft.sonus.common.SonusConstants.PLUGIN_MESSAGE_CHANNEL_KEY;

@NullMarked
public final class SonusPlayer implements ISonusPlayer, CommandSender, AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger("Sonus");

    private final SonusService service;
    private final IPlatformPlayer platform;
    // keep track of which rooms this player is in
    private final Map<UUID, IRoom> voiceRooms = new ConcurrentHashMap<>();
    private final AtomicLong maxSequenceNumber = new AtomicLong();
    // visibility states of other players
    private final Map<UUID, SonusPlayerState> perPlayerStates = new ConcurrentHashMap<>();
    private @Nullable SonusAdapter sonusAdapter;
    private @Nullable IRoom serverRoom;
    private @Nullable IRoom prevPrimaryRoom; // hack to fix state updates
    private @Nullable IRoom primaryRoom;
    private @Nullable AgcNode agcNode; // automatic gain control
    // metadata sent by the backend server agent
    private @Nullable WorldRotatedVec3d position;
    private @Nullable String team;
    // player state
    private boolean connected; // player has initialized connection, but may not be active yet
    private boolean voiceActive; // active adapter connection
    private boolean muted;
    private boolean deafened;
    private long lastKeepAlive = System.currentTimeMillis();

    // track server reference
    private volatile @Nullable SonusServer server;

    public SonusPlayer(SonusService service, IPlatformPlayer platform) {
        this.service = service;
        this.platform = platform;
    }

    @Override
    public void handleAudioInput(SonusAudio audio) {
        if (this.muted || this.deafened
                || !this.platform.hasPermission(PERMISSION_VOICE_SPEAK, true)) {
            return;
        }

        this.maxSequenceNumber.updateAndGet(l -> Math.max(l, audio.getSequenceNumber()));
        this.processAudioInput(audio);
        this.handleRoomBroadcast(room -> room.sendStaticAudio(this, audio));
    }

    @Override
    public void handleAudioInputEnd(long sequence) {
        if (!this.platform.hasPermission(PERMISSION_VOICE_SPEAK, true)) {
            return;
        }
        this.maxSequenceNumber.set(0L);
        this.handleRoomBroadcast(room -> room.sendSpatialAudioEnd(this, sequence));
    }

    private void processAudioInput(SonusAudio audio) {
        if (audio.isEmpty()) {
            return; // don't process zero-length audio
        }
        if (this.service.getConfig().agcEnabled()) {
            // do automatic gain control on input audio to prevent destruction of ears
            if (this.agcNode == null) {
                this.agcNode = new AgcNode(this.service.getSpeexNatives());
            }
            this.agcNode.process(audio);
        }
    }

    private void handleRoomBroadcast(Consumer<IRoom> consumer) {
        // first, process all rooms which are configured to prevent audio input
        // being passed to other rooms
        boolean preventSpeakToOthers = false;
        for (IRoom room : this.voiceRooms.values()) {
            if (!room.getRoomAudioType().isSpeakToOthers()) {
                consumer.accept(room);
                preventSpeakToOthers = true;
            }
        }
        // if no rooms exist which prevent audio input being passed, send audio to all rooms
        if (!preventSpeakToOthers) {
            for (IRoom room : this.voiceRooms.values()) {
                consumer.accept(room);
            }
        }
    }

    private boolean canHear(ISonusSource source, boolean spatial) {
        if (this.sonusAdapter == null || !this.isVoiceActive()) {
            return false;
        }
        if (this.deafened || !this.platform.hasPermission(PERMISSION_VOICE_LISTEN, true)) {
            return false;
        } else if (this == source) {
            return false; // never make players listen to themselves
        }
        // check if player is hidden
        SonusPlayerState state = this.perPlayerStates.get(source.getUniqueId(this));
        if (state != null) {
            if (!spatial && state.staticHidden()) {
                return false; // player is hidden for static audio
            } else if (spatial && state.spatialHidden()) {
                return false; // player is hidden for spatial audio
            }
        }
        IRoom primaryRoom = this.getPrimaryRoom();
        if (primaryRoom != null) {
            // if this player is in an isolated room, the player won't be
            // able to listen to player audio from outside of this room
            if (!primaryRoom.getRoomAudioType().isListenToOthers()
                    && source instanceof ISonusPlayer other
                    && other.getPrimaryRoom() != primaryRoom) {
                return false;
            } else if (!spatial
                    && source instanceof ISonusPlayer other
                    && other.getPrimaryRoom() == primaryRoom) {
                // players in the same room can always hear each other
                return true;
            }
        }
        if (spatial) {
            // if this is spatial audio, check the server matches
            if (!Objects.equals(source.getServerId(), this.getServerId())) {
                return false;
            }
            WorldRotatedVec3d thisPosition = this.getPosition();
            WorldRotatedVec3d thatPosition = source.getPosition();
            if (thisPosition != null && thatPosition != null) {
                if (!thisPosition.getDimension().equals(thatPosition.getDimension())) {
                    return false; // dimensions don't match
                }
                double voiceRange = this.service.getConfig().getVoiceChatRange();
                if (thisPosition.distanceSquared(thatPosition) >= voiceRange * voiceRange) {
                    return false; // too far away
                }
            }
        }
        if (source instanceof SonusPlayer sonusSource) {
            // call platform-specific fallback when there is no state set
            return this.platform.canSee(sonusSource.platform);
        }
        return true;
    }

    @Override
    public void setKeepAlive(long timestamp) {
        this.lastKeepAlive = timestamp;
    }

    @Override
    public long getLastKeepAlive() {
        return this.lastKeepAlive;
    }

    @Override
    public void sendStaticAudio(ISonusSource source, ISonusAudio audio) {
        if (this.canHear(source, false)) {
            assert this.sonusAdapter != null;
            this.sonusAdapter.sendStaticAudio(this, source, audio);
        }
    }

    @Override
    public void sendStaticAudioEnd(ISonusSource source, long sequence) {
        if (this.canHear(source, false)) {
            assert this.sonusAdapter != null;
            this.sonusAdapter.sendAudioEnd(this, source, sequence);
        }
    }

    @Override
    public void sendSpatialAudio(ISonusSource source, SonusAudio audio, Vec3d position) {
        if (this.canHear(source, true)) {
            assert this.sonusAdapter != null;
            this.sonusAdapter.sendSpatialAudio(this, source, audio, position);
        }
    }

    @Override
    public void sendSpatialAudio(ISonusSource source, SonusAudio audio) {
        if (this.canHear(source, true)) {
            assert this.sonusAdapter != null;
            this.sonusAdapter.sendSpatialAudio(this, source, audio);
        }
    }

    @Override
    public void sendSpatialNormedAudio(ISonusSource source, SonusAudio audio) {
        if (!this.canHear(source, true)) {
            return;
        }
        Vec3d pos = SpatialNormProcessor.normalizeAudio(this.service, this, source, audio);
        if (pos != null) { // if the position is null, the processor decided to cancel the audio packet
            assert this.sonusAdapter != null;
            this.sonusAdapter.sendSpatialAudio(this, source, audio, pos);
        }
    }

    @Override
    public void sendSpatialAudioEnd(ISonusSource source, long sequence) {
        if (this.canHear(source, true)) {
            assert this.sonusAdapter != null;
            this.sonusAdapter.sendAudioEnd(this, source, sequence);
        }
    }

    @Override
    public boolean canAccessRoom(dev.minceraft.sonus.api.service.rooms.IRoom room, @Nullable String password) {
        if (!this.hasPermission(PERMISSION_GROUPS_USE, true)) {
            return false; // no permission to use groups
        }
        // check password
        if (!this.hasPermission(PERMISSION_GROUPS_BYPASS_PASSWORD, false)) {
            if (!Objects.equals(room.getPassword(), password)) {
                return false; // wrong password (and no bypass permission)
            }
        }
        if (room instanceof ServerRoom) {
            return false; // don't allow accessing server rooms manually
        }
        return true; // allow join
    }

    @Override
    public void joinRoom(IRoom room) {
        Preconditions.checkNotNull(room, "Room cannot be null");
        if (this.voiceRooms.putIfAbsent(room.getId(), room) == null) {
            room.addMember(this);
        }
    }

    @Override
    public void leaveRoom(IRoom room) {
        Preconditions.checkNotNull(room, "Room cannot be null");
        if (this.voiceRooms.remove(room.getId()) == null) {
            return; // wasn't in room
        }
        room.removeMember(this);
        if (this.primaryRoom == room) {
            this.primaryRoom = null;
            this.prevPrimaryRoom = room;
            this.updateState();
            this.prevPrimaryRoom = null;
        }
        if (this.serverRoom == room) {
            this.serverRoom = null;
            this.updateState();
        }
    }

    @Override
    public @Nullable IRoom getServerRoom() {
        return this.serverRoom;
    }

    @Override
    public void setServerRoom(@Nullable IRoom room) {
        if (this.primaryRoom == room) {
            return; // nothing to do
        }
        // properly leave previous room
        IRoom prevRoom = this.serverRoom;
        if (prevRoom != null) {
            this.serverRoom = null;
            this.leaveRoom(prevRoom);
        }
        // enter new room
        this.serverRoom = room;
        if (room != null) {
            this.joinRoom(room);
        }
        // trigger state update
        this.updateState();
    }

    @Override
    public @Nullable IRoom getPrimaryRoom() {
        return this.primaryRoom;
    }

    @Override
    public void setPrimaryRoom(@Nullable IRoom room) {
        if (this.primaryRoom == room) {
            return; // nothing to do
        }
        // properly leave previous room
        IRoom prevRoom = this.primaryRoom;
        if (prevRoom != null) {
            this.primaryRoom = null;
            this.leaveRoom(prevRoom);
            this.service.getEventManager().onPrimaryRoomLeaved(this, prevRoom);
        }
        // enter new room
        this.prevPrimaryRoom = prevRoom;
        this.primaryRoom = room;
        if (room != null) {
            this.joinRoom(room);
            this.service.getEventManager().onPrimaryRoomJoined(this, room);
        }
        // trigger state update
        this.updateState(true);
        this.prevPrimaryRoom = null;

        this.updateCommands();
    }

    @Override
    public UUID getUniqueId(@Nullable ISonusPlayer viewer) {
        IPlatformPlayer platformViewer = viewer != null ? ((SonusPlayer) viewer).platform : null;
        return this.platform.getUniqueId(platformViewer);
    }

    @Override
    public @Nullable UUID getServerId() {
        return this.platform.getServerId();
    }

    @Override
    public String getName(@Nullable ISonusPlayer viewer) {
        IPlatformPlayer platformViewer = viewer != null ? ((SonusPlayer) viewer).platform : null;
        return this.platform.getName(platformViewer);
    }

    @Override
    @Nullable
    public String getTeam() {
        return this.team;
    }

    public void setTeam(@Nullable String team) {
        this.team = team;
    }

    @Override
    @Nullable
    public SonusAdapter getAdapter() {
        return this.sonusAdapter;
    }

    @Override
    public boolean setAdapter(@Nullable SonusAdapter adapter) {
        synchronized (this) {
            if (adapter != null && this.sonusAdapter != null) {
                LOGGER.warn("Player {}({}) tried to connect via {}, but is already connected via another adapter: {}",
                        this.getName(), this.getUniqueId(), adapter.getAdapterInfo().id(), this.sonusAdapter.getAdapterInfo().id());
                return false; // Already connected via an adapter
            }
            if (this.sonusAdapter == null && adapter != null && !this.hasPermission(PERMISSION_CONNECT, true)) {
                return false; // No permission to connect
            }
            this.sonusAdapter = adapter;
            return true;
        }
    }

    @Override
    public void sendPluginMessage(Key key, ByteBuf data) {
        this.platform.sendPluginMessage(key, data);
    }

    @Override
    public void sendBackendPluginMessage(Key key, ByteBuf data) {
        this.platform.sendBackendPluginMessage(key, data);
    }

    @Override
    public @Nullable WorldRotatedVec3d getPosition() {
        return this.position;
    }

    public void setPosition(@Nullable WorldRotatedVec3d position) {
        this.position = position;
    }

    public Map<UUID, IRoom> getVoiceRooms() {
        return this.voiceRooms;
    }

    @Override
    public boolean isMuted() {
        return this.muted;
    }

    @Override
    public void setMuted(boolean muted, boolean ignorePermission) {
        this.muted = muted && (ignorePermission || this.platform.hasPermission(PERMISSION_VOICE_SPEAK, true));
    }

    @Override
    public boolean isDeafened() {
        return this.deafened;
    }

    @Override
    public void setDeafened(boolean deafened) {
        if (this.deafened != deafened) {
            this.deafened = deafened;
            this.updateState();
        }
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public boolean isVoiceActive() {
        return this.voiceActive;
    }

    @Override
    public void setVoiceActive(boolean voiceActive) {
        this.setVoiceActive(voiceActive, true);
    }

    public void updateCommands() {
        // trigger command updates, some commands are not available if connected or only if connected
        //
        // velocity doesn't support directly updating commands, so we need
        // to tell the agent to fire a command update packet; still, some platforms may
        // support this feature, so leave it up to the platform
        if (this.service.getPlatform().isCommandUpdateSupported()) {
            this.platform.updateCommands();
        } else {
            this.sendMetaPacket(new TriggerCommandUpdateMessage(this.getUniqueId()));
        }
    }

    public void setVoiceActive(boolean voiceActive, boolean sendToAgent) {
        if (this.voiceActive == voiceActive) {
            return; // no change
        }
        this.voiceActive = voiceActive;
        this.lastKeepAlive = System.currentTimeMillis();

        this.service.getEventManager().onConnectionState(this);

        if (sendToAgent) {
            this.sendConnectionToAgent();
        }
    }

    public void sendConnectionToAgent() {
        PlayerConnectionStateMessage packet = new PlayerConnectionStateMessage();
        packet.setPlayerId(this.getUniqueId());
        packet.setVoiceActive(voiceActive);
        this.sendMetaPacket(packet);
    }

    public void sendMetaPacket(IMetaMessage packet) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        try {
            MetaRegistry.REGISTRY.encode(buf, packet);
            this.sendBackendPluginMessage(PLUGIN_MESSAGE_CHANNEL_KEY, buf.retain());
        } finally {
            buf.release();
        }
    }

    @Override
    public UUID getSenderId(ISonusPlayer viewer) {
        return this.getUniqueId(viewer);
    }

    @Override
    public void handleConnect() {
        // switch server room
        UUID serverId = this.getServerId();
        IRoom room = serverId != null ? this.service.getRoomManager().getRoom(serverId) : null;
        this.setServerRoom(room);

        // broadcast audio end packet to reset sequence numbers
        this.handleAudioInputEnd(this.maxSequenceNumber.get() + 1L);
    }

    @Override
    public void ensureTabListed(ISonusPlayer target) {
        this.platform.ensureTabListed(((SonusPlayer) target).platform);
    }

    @Override
    @Nullable
    public String getTextureHash(@Nullable ISonusPlayer viewer) {
        String encodedTextures = this.platform.getTextures(viewer != null ? ((SonusPlayer) viewer).platform : null);
        if (encodedTextures == null) {
            return null;
        }
        try {
            // Extract texture hash from base64 encoded textures JSON
            byte[] decoded = Base64.getDecoder().decode(encodedTextures);
            JsonObject obj = SonusService.GSON.fromJson(new String(decoded, StandardCharsets.UTF_8), JsonObject.class);
            JsonObject textures = obj.getAsJsonObject("textures");
            if (textures == null || !textures.has("SKIN")) {
                return null;
            }

            String skinUrl = textures.getAsJsonObject("SKIN").get("url").getAsString();
            // extract texture hash from URL
            return skinUrl.substring(skinUrl.lastIndexOf('/') + 1);
        } catch (Throwable throwable) {
            LOGGER.error("Failed to decode texture for player {}", this.getUniqueId(), throwable);
            return null;
        }
    }

    @Override
    public void updateState(boolean globalUpdate) {
        this.service.getEventManager().onPlayerStateUpdate(this, globalUpdate);
        this.updateServer();
    }

    @Override
    public boolean hasPermission(String permission, boolean defaultValue) {
        return this.platform.hasPermission(permission, defaultValue);
    }

    @Override
    public void setPermission(String permission, TriState state) {
        this.platform.setPermission(permission, state);
    }

    @Override
    public void sendMessage(Component component) {
        this.platform.sendMessage(component);
    }

    @Override
    public String getNameFor(ISonusPlayer target) {
        return target.getName(this);
    }

    @Override
    public UUID getUniqueIdFor(ISonusPlayer target) {
        return target.getUniqueId(this);
    }

    @Override
    public boolean isOnline() {
        return this.platform.isOnline();
    }

    @Override
    public boolean canSee(ISonusPlayer target) {
        if (this == target) {
            return true; // the player can always view themselves, regardless of what happens
        }
        // check whether the player is hidden or not; if no state is set, ask platform for fallback
        SonusPlayerState state = this.perPlayerStates.get(target.getUniqueId());
        if (state != null && state.staticHidden() && state.spatialHidden()) {
            return false;
        }
        return this.platform.canSee(((SonusPlayer) target).platform);
    }

    @Override
    public boolean canReceive(ISonusPlayer target) {
        if (this == target) {
            return true; // the player can always view themselves, regardless of what happens
        }
        // check for vanish
        if (!this.canSee(target)) {
            return false;
        }
        if (target.getPrimaryRoom() != null && target.getPrimaryRoom() == this.primaryRoom) {
            return true;
        }
        return this.getServerId() != null && this.getServerId().equals(target.getServerId());
    }

    @Override
    public Locale getLocale() {
        return this.platform.getLocale();
    }

    @Override
    public Component renderComponent(Component component, Locale locale) {
        return this.platform.renderComponent(component, locale);
    }

    @Override
    public String renderPlainComponent(Component component, Locale locale) {
        return this.platform.renderPlainComponent(component, locale);
    }

    public void clearStates() {
        this.perPlayerStates.clear();
    }

    public void updateStates(Map<UUID, SonusPlayerState> states) {
        for (Map.Entry<UUID, SonusPlayerState> newState : states.entrySet()) {
            this.updateState(newState.getValue());
        }
    }

    public void updateState(SonusPlayerState newState) {
        SonusPlayerState oldState = this.perPlayerStates.get(newState.playerId());
        if (oldState == null || !oldState.equals(newState)) {
            SonusPlayer target = this.service.getPlayerManager().getPlayer(newState.playerId());
            if (target == null) {
                LOGGER.warn("Received state update for unknown player: {}", newState.playerId());
                return;
            }
            this.perPlayerStates.put(newState.playerId(), newState);
            this.service.getEventManager().onPlayerVisibilityStateUpdate(this, target, newState);
        }
    }

    @Override
    public void disconnect() {
        if (!this.isConnected()) {
            return; // Prevent state updates for non-connected players
        }

        // leave all rooms (includes primary + server rooms)
        for (IRoom room : Set.copyOf(this.voiceRooms.values())) {
            this.leaveRoom(room);
        }

        this.setVoiceActive(false);
        this.service.getPlayerManager().disablePlayer(this); // disable voice stuff
        this.updateState(true);
        this.service.getEventManager().onPlayerDisconnect(this);
        this.setConnected(false);
    }

    public void updateServer() {
        UUID serverId;
        if (this.isVoiceActive() && (serverId = this.platform.getServerId()) != null) {
            this.updateServer(this.service.getPlayerManager().getServer(serverId));
        } else {
            this.updateServer(null);
        }
    }

    public synchronized void updateServer(@Nullable SonusServer server) {
        SonusServer prevServer = this.server;
        if (server == prevServer) {
            return; // nothing changed
        }
        if (prevServer != null) {
            prevServer.onDisconnect(this);
        }
        this.server = server;
    }

    @Override
    public void close() {
        try (AgcNode ignoredAgcNode = this.agcNode) {
            // NO-OP
        } finally {
            this.agcNode = null;
        }
    }

    public void tickKeepAlive(long currentTime) {
        if (!this.isConnected()) {
            return;
        }
        if (this.sonusAdapter != null && this.isVoiceActive()) {
            this.sonusAdapter.sendKeepAlive(this, currentTime);
        }
        long keepAliveTimeoutMs = this.service.getConfig().getKeepAliveTimeoutMs();
        if (this.lastKeepAlive + keepAliveTimeoutMs < currentTime) {
            LOGGER.warn("Disconnecting {}, timed out", this.getUniqueId());
            this.disconnect();
        }
    }

    public IPlatformPlayer getPlatform() {
        return this.platform;
    }
}
