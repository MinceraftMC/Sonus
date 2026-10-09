package dev.minceraft.sonus.plasmo.adapter;

import dev.minceraft.sonus.common.adapter.ISonusService;
import dev.minceraft.sonus.common.adapter.adapter.AdapterInfo;
import dev.minceraft.sonus.common.adapter.adapter.SonusAdapter;
import dev.minceraft.sonus.common.audio.SonusAudio;
import dev.minceraft.sonus.common.participant.IAudioSource;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.common.data.Vec3d;
import dev.minceraft.sonus.common.data.WorldRotatedVec3d;
import dev.minceraft.sonus.common.data.GameProfile;
import dev.minceraft.sonus.common.protocol.audio.AudioCategory;
import dev.minceraft.sonus.plasmo.adapter.config.PlasmoConfig;
import dev.minceraft.sonus.plasmo.adapter.connection.PlasmoConnection;
import dev.minceraft.sonus.plasmo.protocol.tcp.clientbound.SourceAudioEndPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.clientbound.SourceLineRegisterPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.clientbound.SourceLineUnregisterPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.VoiceSourceLine;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.source.DirectSourceInfo;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.source.PlayerSourceInfo;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.source.SourceInfo;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.source.StaticSourceInfo;
import dev.minceraft.sonus.plasmo.protocol.udp.bothbound.PingPlasmoPacket;
import dev.minceraft.sonus.plasmo.protocol.udp.clientbound.SourceAudioPlasmoPacket;
import dev.minceraft.sonus.plasmo.protocol.version.VersionManager;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static dev.minceraft.sonus.plasmo.adapter.PlasmoConstants.ADDON_ID;

@NullMarked
public class PlasmoAdapter implements SonusAdapter {

    private final PlasmoTranslationHolder translationHolder = new PlasmoTranslationHolder();
    private @MonotonicNonNull ISonusService service;
    private @MonotonicNonNull PlasmoProtocolAdapter adapter;
    private @MonotonicNonNull PlasmoSessionManager sessionManager;
    private @MonotonicNonNull AdapterInfo adapterInfo;

    @Override
    public void load(ISonusService service) {
        this.service = service;
        service.getConfigHolder().registerConfigTemplate("plasmo", PlasmoConfig.class, PlasmoConfig::new);
    }

    private AdapterInfo buildAdapterInfo() {
        return new AdapterInfo("plasmo",
                this.service.getConfig().getSubConfig(PlasmoConfig.class).enabled);
    }

    @Override
    public void init(ISonusService service) {
        this.adapter = new PlasmoProtocolAdapter(this);
        this.sessionManager = new PlasmoSessionManager(this);

        this.service.getEventManager().registerListener(new PlasmoSonusListener(this));

        VersionManager.logSupportedVersions();
    }

    @Override
    public void sendStaticAudio(ISonusPlayer player, IAudioSource source, SonusAudio audio) {
        PlasmoConnection connection = this.sessionManager.getConnectionByUniqueId(player.getUniqueId());
        if (connection == null) {
            return; // no plasmo session found
        }
        UUID categoryId = this.extractCategoryId(source, connection);

        SourceInfo sourceInfo = connection.registerSourceInfo(source.getUniqueId(player), categoryId, () -> {
            String name = null;
            GameProfile profile = null;

            if (source instanceof ISonusPlayer speaker) {
                name = speaker.getName(player);
                profile = speaker.getSimpleProfile(player);
            }
            return new DirectSourceInfo(
                    ADDON_ID,
                    UUID.randomUUID(),
                    connection.getSourceLine(categoryId).getId(),
                    name,
                    (byte) 1,
                    this.adapter.getCodecInfo(),
                    true,
                    true,
                    0,
                    profile, // attach the sound to the player himself for static audio
                    Vec3d.ZERO,
                    Vec3d.ZERO,
                    true
            );
        });

        this.sendAudio(connection, source, sourceInfo, audio);
    }

    @Override
    public void sendSpatialAudio(ISonusPlayer player, IAudioSource source, SonusAudio audio, Vec3d pos) {
        PlasmoConnection connection = this.sessionManager.getConnectionByUniqueId(player.getUniqueId());
        if (connection == null) {
            return; // no plasmo session found
        }
        UUID categoryId = this.extractCategoryId(source, connection);

        SourceInfo sourceInfo = connection.registerSourceInfo(source.getUniqueId(player), categoryId, () -> {
            WorldRotatedVec3d position = player.getPosition();
            if (source instanceof ISonusPlayer speaker && position != null) {
                Vec3d relativePos = pos.sub(position);
                return new DirectSourceInfo(
                        ADDON_ID,
                        source.getUniqueId(player),
                        connection.getSourceLine(source.getCategoryId()).getId(),
                        speaker.getName(player),
                        (byte) 1,
                        this.adapter.getCodecInfo(),
                        false,
                        false,
                        0,
                        speaker.getSimpleProfile(player),
                        relativePos,
                        null,
                        false
                );
            }

            return new StaticSourceInfo(
                    ADDON_ID,
                    UUID.randomUUID(),
                    connection.getSourceLine(categoryId).getId(),
                    null,
                    (byte) 1,
                    this.adapter.getCodecInfo(),
                    false,
                    false,
                    0,
                    pos,
                    null
            );
        });

        if (sourceInfo instanceof StaticSourceInfo staticInfo) {
            if (!staticInfo.getPosition().equals(pos)) {
                staticInfo.setPosition(pos);
                staticInfo.markDirty();
            }
        } else if (sourceInfo instanceof DirectSourceInfo directInfo) {
            WorldRotatedVec3d position = player.getPosition();
            if (position != null) {
                Vec3d relativePos = pos.sub(position);
                if (!Objects.equals(relativePos, directInfo.getRelativePosition())) {
                    directInfo.setRelativePosition(relativePos);
                    directInfo.markDirty();
                }
            }
        }

        this.sendAudio(connection, source, sourceInfo, audio);
    }

    @Override
    public void sendSpatialAudio(ISonusPlayer player, IAudioSource source, SonusAudio audio) {
        PlasmoConnection connection = this.sessionManager.getConnectionByUniqueId(player.getUniqueId());
        if (connection == null) {
            return; // no plasmo session found
        }

        UUID categoryId = this.extractCategoryId(source, connection);

        WorldRotatedVec3d position = source.getPosition();

        SourceInfo sourceInfo = connection.registerSourceInfo(source.getUniqueId(player), categoryId, () -> {
            if (source instanceof ISonusPlayer speaker) {
                return new PlayerSourceInfo(
                        ADDON_ID,
                        UUID.randomUUID(),
                        connection.getSourceLine(categoryId).getId(),
                        speaker.getName(player),
                        (byte) 1,
                        this.adapter.getCodecInfo(),
                        false,
                        true,
                        0,
                        this.sessionManager.buildPlayerInfo(player, speaker));
            }
            if (position == null) {
                return null;
            }
            return new StaticSourceInfo(
                    ADDON_ID,
                    UUID.randomUUID(),
                    connection.getSourceLine(categoryId).getId(),
                    null,
                    (byte) 1,
                    this.adapter.getCodecInfo(),
                    false,
                    false,
                    0,
                    position,
                    null
            );
        });

        if (sourceInfo instanceof StaticSourceInfo staticInfo) {
            if (!staticInfo.getPosition().equals(position)) {
                staticInfo.setPosition(position);
                staticInfo.markDirty();
            }
        }

        this.sendAudio(connection, source, sourceInfo, audio);
    }

    private UUID extractCategoryId(IAudioSource source, PlasmoConnection connection) {
        UUID categoryId = source.getCategoryId();
        if (source instanceof ISonusPlayer speaker) {
            // In same room override category with group category
            if (speaker.getPrimaryRoom() != null && speaker.getPrimaryRoom().equals(connection.getPlayer().getPrimaryRoom())) {
                categoryId = speaker.getPrimaryRoom().getUniqueId();
            }
        }
        if (categoryId == null) {
            categoryId = connection.getDefaultSourceLine().getId();
        }
        return categoryId;
    }

    private void sendAudio(PlasmoConnection connection, IAudioSource source, @Nullable SourceInfo sourceInfo, SonusAudio audio) {
        if (sourceInfo == null) {
            return;
        }
        SourceAudioPlasmoPacket packet = new SourceAudioPlasmoPacket();
        packet.setDistance((short) this.getService().getConfig().getVoiceChatRange());
        packet.setAudioData(audio.setProcessor(() -> connection.getProcessor(source.getUniqueId(connection.getPlayer()))).opus());
        packet.setSequenceNumber(audio.getSequenceNumber());
        packet.setSourceId(sourceInfo.getId());
        packet.setSourceState(sourceInfo.getState());

        connection.sendPacket(packet);

        sourceInfo.resetState();
    }

    @Override
    public void sendAudioEnd(ISonusPlayer player, IAudioSource source, long sequence) {
        PlasmoConnection connection = this.sessionManager.getConnectionByUniqueId(player.getUniqueId());
        if (connection == null) {
            return; // no plasmo session found
        }

        SourceAudioEndPacket packet = new SourceAudioEndPacket();
        packet.setSequenceNumber(sequence);
        packet.setSourceId(source.getUniqueId(player));

        connection.sendPacket(packet);
    }

    @Override
    public void registerCategory(ISonusPlayer player, AudioCategory category) {
        PlasmoConnection connection = this.sessionManager.getConnectionByUniqueId(player.getUniqueId());
        if (connection == null) {
            return; // no plasmo session found
        }
        VoiceSourceLine sourceLine = new VoiceSourceLine(
                category.getUniqueId().toString(),
                player.renderPlainComponent(category.getName()), // prerender the name component
                PlasmoConstants.DEFAULT_SOURCE_LINE_ICON,
                1.0,
                0,
                Set.of()
        );
        connection.registerVoiceSourceLine(category.getUniqueId(), sourceLine);

        SourceLineRegisterPacket packet = new SourceLineRegisterPacket();
        packet.setSourceLine(sourceLine);

        connection.sendPacket(packet);
    }

    @Override
    public void unregisterCategory(ISonusPlayer player, UUID categoryId) {
        PlasmoConnection connection = this.sessionManager.getConnectionByUniqueId(player.getUniqueId());
        if (connection == null) {
            return; // no plasmo session found
        }

        UUID sourceLineId = connection.getPlasmoSourceLineId(categoryId);
        if (sourceLineId == null) {
            throw new IllegalStateException("No Plasmo source line registered for category " + categoryId +
                    " cannot unregister for player " + player.getUniqueId());
        }

        connection.unregisterVoiceSourceLine(sourceLineId);

        SourceLineUnregisterPacket packet = new SourceLineUnregisterPacket();
        packet.setSourceLineId(sourceLineId);
        connection.sendPacket(packet);
    }

    @Override
    public void sendKeepAlive(ISonusPlayer player, long currentTime) {
        PlasmoConnection connection = this.sessionManager.getConnectionByUniqueId(player.getUniqueId());
        if (connection == null) {
            return; // no plasmo session found
        }
        if (!connection.isVoiceActive()) {
            return;
        }

        PingPlasmoPacket packet = new PingPlasmoPacket();
        packet.setTimestamp(currentTime);

        connection.sendPacket(packet);
    }

    @Override
    public PlasmoProtocolAdapter getUdpAdapter() {
        return this.adapter;
    }

    @Override
    public AdapterInfo getAdapterInfo() {
        if (this.adapterInfo == null) {
            this.adapterInfo = this.buildAdapterInfo();
        }
        return this.adapterInfo;
    }

    public PlasmoTranslationHolder getTranslationHolder() {
        return this.translationHolder;
    }

    public PlasmoConfig getConfig() {
        return this.service.getConfig().getSubConfig(PlasmoConfig.class);
    }

    public ISonusService getService() {
        return this.service;
    }

    public PlasmoSessionManager getSessionManager() {
        return this.sessionManager;
    }
}
