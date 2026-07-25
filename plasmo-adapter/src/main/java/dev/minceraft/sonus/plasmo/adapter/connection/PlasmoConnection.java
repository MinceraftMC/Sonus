package dev.minceraft.sonus.plasmo.adapter.connection;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import dev.minceraft.sonus.common.audio.AudioProcessor;
import dev.minceraft.sonus.common.audio.OpusMode;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.common.protocol.udp.WrappedUdpPipelineData;
import dev.minceraft.sonus.common.version.SemanticVersion;
import dev.minceraft.sonus.plasmo.adapter.PlasmoAdapter;
import dev.minceraft.sonus.plasmo.adapter.pipeline.PlasmoUdpContext;
import dev.minceraft.sonus.plasmo.protocol.AbstractPlasmoPacket;
import dev.minceraft.sonus.plasmo.protocol.PlasmoPmChannels;
import dev.minceraft.sonus.plasmo.protocol.cipher.CipherAes;
import dev.minceraft.sonus.plasmo.protocol.cipher.ICipher;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPacketRegistry;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.clientbound.SourceInfoPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.VoiceActivation;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.VoiceSourceLine;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.source.SourceInfo;
import dev.minceraft.sonus.plasmo.protocol.udp.UdpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@NullMarked
public class PlasmoConnection implements AutoCloseable {

    private final PlasmoAdapter adapter;
    private final ISonusPlayer player;
    private final UUID secret = UUID.randomUUID();

    private final MetaHandler metaHandler;
    private final VoiceHandler voiceHandler;
    private final Map<UUID, AudioProcessor> processors = new ConcurrentHashMap<>();
    private final Table<UUID, UUID, SourceInfo> sourceCategorySourceInfoMap = Tables.synchronizedTable(HashBasedTable.create()); // sourceId, categoryId, SourceInfo
    private final Map<UUID, SourceInfo> sources = new ConcurrentHashMap<>();
    private final VoiceSourceLine defaultSourceLine;

    private final Map<UUID, VoiceActivation> voiceActivations = new ConcurrentHashMap<>();
    private final Map<UUID, VoiceSourceLine> sourceLines = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> sonusPlasmoSourceLineMap = new ConcurrentHashMap<>();

    private @MonotonicNonNull ICipher cipher;
    private @MonotonicNonNull InetSocketAddress remoteAddress;
    private @MonotonicNonNull SemanticVersion plasmoVersion;
    private @MonotonicNonNull SemanticVersion minecraftVersion;

    public PlasmoConnection(PlasmoAdapter adapter, ISonusPlayer player) {
        this.adapter = adapter;
        this.player = player;

        this.metaHandler = new MetaHandler(adapter, this);
        this.voiceHandler = new VoiceHandler(adapter, this);

        int voiceChatRange = (int) this.adapter.getService().getConfig().getVoiceChatRange();
        VoiceActivation defaultActivation = new VoiceActivation(
                "proximity",
                this.adapter.getTranslationHolder().registerTranslationKey("sonus.pv.activation.proximity"),
                "plasmovoice:textures/icons/microphone.png",
                List.of(voiceChatRange),
                voiceChatRange,
                true, // Allow proximity
                false, // Force mono audio
                true, // Allow other activations
                this.adapter.getUdpAdapter().getCodecInfo(),
                100 // weight
        );
        this.voiceActivations.put(defaultActivation.getId(), defaultActivation);

        this.defaultSourceLine = new VoiceSourceLine(
                "proximity",
                this.adapter.getTranslationHolder().registerTranslationKey("sonus.pv.sourceline.proximity"),
                "plasmovoice:textures/icons/speaker.png",
                1.0,
                0,
                Set.of()
        );
        this.sourceLines.put(this.defaultSourceLine.getId(), this.defaultSourceLine);
    }

    public ICipher getCipher() {
        return this.cipher;
    }

    public void sendPacket(AbstractPlasmoPacket<?> packet) {
        if (packet instanceof UdpPlasmoPacket<?> voicePacket) {
            sendUdpPacket(voicePacket);
        } else if (packet instanceof TcpPlasmoPacket<?> metaPacket) {
            sendTcpPacket(metaPacket);
        } else {
            throw new IllegalArgumentException("Unsupported packet type: " + packet.getClass().getName());
        }
    }

    private void sendUdpPacket(UdpPlasmoPacket<?> packet) {
        if (this.remoteAddress == null) {
            throw new IllegalStateException("Cannot send UDP packet before remote address is set.");
        }
        packet.setSecret(this.getSecret());
        packet.setTimestamp(System.currentTimeMillis());
        WrappedUdpPipelineData payload = new WrappedUdpPipelineData(
                PlasmoUdpContext.newInstance(this.remoteAddress, this),
                this.adapter.getUdpAdapter().getPlasmoCodec(),
                packet
        );
        this.adapter.getService().getUdpServer().sendPacket(payload);
    }

    private void sendTcpPacket(TcpPlasmoPacket<?> packet) {
        ByteBuf buffer = Unpooled.buffer();
        try {
            TcpPacketRegistry.REGISTRY.encode(buffer, packet);
            this.player.sendPluginMessage(PlasmoPmChannels.CHANNEL, buffer.retain());
        } finally {
            buffer.release();
        }
    }

    public ISonusPlayer getPlayer() {
        return this.player;
    }

    public UUID getSecret() {
        return this.secret;
    }

    public MetaHandler getMetaHandler() {
        return this.metaHandler;
    }

    public VoiceHandler getVoiceHandler() {
        return this.voiceHandler;
    }

    public Map<UUID, VoiceActivation> getVoiceActivations() {
        return this.voiceActivations;
    }

    public Map<UUID, VoiceSourceLine> getSourceLines() {
        return this.sourceLines;
    }

    public void initCipher(byte[] publicKey) {
        this.cipher = CipherAes.createFromRsaHandshake(publicKey);
    }

    public boolean isVoiceActive() {
        return this.player.isVoiceActive();
    }

    public void setVoiceActive(boolean active) {
        this.player.setVoiceActive(active);
    }

    public InetSocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    public void setRemoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public SemanticVersion getPlasmoVersion() {
        return this.plasmoVersion;
    }

    public void setPlasmoVersion(SemanticVersion plasmoVersion) {
        this.plasmoVersion = plasmoVersion;
    }

    public SemanticVersion getMinecraftVersion() {
        return this.minecraftVersion;
    }

    public void setMinecraftVersion(SemanticVersion minecraftVersion) {
        this.minecraftVersion = minecraftVersion;
    }

    public AudioProcessor getProcessor(UUID channelId) {
        return this.processors.computeIfAbsent(channelId, __ ->
                this.adapter.getService().createAudioProcessor(OpusMode.VOICE));
    }

    public VoiceSourceLine getDefaultSourceLine() {
        return this.defaultSourceLine;
    }

    public void registerVoiceSourceLine(UUID sonusId, VoiceSourceLine sourceLine) {
        this.sourceLines.put(sourceLine.getId(), sourceLine);
        this.sonusPlasmoSourceLineMap.put(sonusId, sourceLine.getId());
    }

    public void unregisterVoiceSourceLine(UUID sourceLineId) {
        if (sourceLineId.equals(this.defaultSourceLine.getId())) {
            return; // cannot unregister default source line
        }
        this.sourceLines.remove(sourceLineId);
        this.sonusPlasmoSourceLineMap.values().removeIf(id -> id.equals(sourceLineId));
    }

    public VoiceSourceLine getSourceLine(@Nullable UUID sourceLineId) {
        if (sourceLineId == null) {
            return this.defaultSourceLine;
        }
        UUID plasmoSourceLineId = this.getPlasmoSourceLineId(sourceLineId);
        if (plasmoSourceLineId == null) {
            return this.defaultSourceLine;
        }
        return this.sourceLines.getOrDefault(plasmoSourceLineId, this.defaultSourceLine);
    }

    @Nullable
    public SourceInfo getSourceInfo(UUID sourceId, @Nullable UUID categoryId) {
        return this.sourceCategorySourceInfoMap.get(sourceId, categoryId);
    }

    public List<SourceInfo> getSourceInfos(UUID sourceId) {
        return List.copyOf(this.sourceCategorySourceInfoMap.row(sourceId).values());
    }

    @Nullable
    public SourceInfo registerSourceInfo(UUID sourceId, @Nullable UUID categoryId, Supplier<@Nullable SourceInfo> builder) {
        SourceInfo sourceInfo = this.getSourceInfo(sourceId, categoryId);
        if (sourceInfo != null) {
            return sourceInfo;
        }
        sourceInfo = builder.get();
        if (sourceInfo == null) {
            return null;
        }

        this.sourceCategorySourceInfoMap.put(sourceId, categoryId, sourceInfo);
        this.sources.put(sourceInfo.getId(), sourceInfo);

        return sourceInfo;
    }

    public void unregisterSourceInfo(UUID id) {
        SourceInfo removed = this.sources.remove(id);
        if (removed == null) {
            return;
        }
        this.sourceCategorySourceInfoMap.values().removeIf(info -> info.getId().equals(removed.getId()));
        removed.markRemoved();
        sendSourceUpdate(removed);
    }

    @Nullable
    public UUID getPlasmoSourceLineId(UUID sonusSourceLineId) {
        return this.sonusPlasmoSourceLineMap.get(sonusSourceLineId);
    }

    @Nullable
    public SourceInfo getSourceInfo(UUID sourceInfoId) {
        return this.sources.get(sourceInfoId);
    }

    @Override
    public void close() {
        this.processors.values().removeIf(processor -> {
            processor.close();
            return true;
        });
    }

    public void sendSourceUpdate(SourceInfo sourceInfo) {
        SourceInfoPacket infoPacket = new SourceInfoPacket();
        infoPacket.setSourceInfo(sourceInfo);
        this.sendPacket(infoPacket);
    }
}
