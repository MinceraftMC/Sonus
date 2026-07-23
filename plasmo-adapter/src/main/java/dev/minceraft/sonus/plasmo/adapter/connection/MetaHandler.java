package dev.minceraft.sonus.plasmo.adapter.connection;

import dev.minceraft.sonus.service.data.ISonusPlayer;
import dev.minceraft.sonus.service.version.SemanticVersion;
import dev.minceraft.sonus.plasmo.adapter.PlasmoAdapter;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpHandler;
import dev.minceraft.sonus.plasmo.protocol.tcp.clientbound.ConnectionPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.clientbound.LanguagePacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.source.SourceInfo;
import dev.minceraft.sonus.plasmo.protocol.tcp.serverbound.LanguageRequestPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.serverbound.PlayerActivationDistancesPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.serverbound.PlayerAudioEndPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.serverbound.PlayerInfoPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.serverbound.PlayerStatePacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.serverbound.SourceInfoRequestPacket;
import dev.minceraft.sonus.plasmo.protocol.version.VersionManager;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@NullMarked
public class MetaHandler implements TcpHandler {

    private final PlasmoAdapter adapter;
    private final PlasmoConnection connection;

    public MetaHandler(PlasmoAdapter adapter, PlasmoConnection connection) {
        this.adapter = adapter;
        this.connection = connection;
    }

    @Override
    public void handleLanguageRequestPacket(LanguageRequestPacket packet) {
        Locale locale = Locale.forLanguageTag(packet.getLanguage().replace('_', '-'));
        Map<String, String> translations = this.adapter.getTranslationHolder().getTranslations(this.connection.getPlayer(), locale);

        LanguagePacket response = new LanguagePacket();
        response.setLanguage(packet.getLanguage());
        response.setLanguageMap(translations);

        this.connection.sendPacket(response);
    }

    @Override
    public void handlePlayerActivationDistancesPacket(PlayerActivationDistancesPacket packet) {

    }

    @Override
    public void handlePlayerAudioEndPacket(PlayerAudioEndPacket packet) {
        this.connection.getPlayer().handleAudioInputEnd(packet.getSequenceNumber());
    }

    @Override
    public void handlePlayerInfoPacket(PlayerInfoPacket packet) {
        SemanticVersion plasmoVersion = SemanticVersion.of(packet.getPlasmoVersion());
        if (!VersionManager.isSupported(plasmoVersion)) {
            this.connection.getPlayer().sendMessage(translatable("sonus.plasmo.version_unsupported")
                    .arguments(text(plasmoVersion.asShortPrettyString()),
                            text(VersionManager.MIN_VERSION.asShortPrettyString())));
            return;
        }

        this.connection.setPlasmoVersion(plasmoVersion);
        this.connection.setMinecraftVersion(SemanticVersion.of(packet.getMinecraftVersion()));
        this.connection.initCipher(packet.getPublicKey());
        ISonusPlayer player = this.connection.getPlayer();
        player.setDeafened(packet.isVoiceDisabled());
        player.setMuted(packet.isMicrophoneMuted());

        ConnectionPacket connectionPacket = new ConnectionPacket();
        connectionPacket.setSecret(this.connection.getSecret());
        connectionPacket.setInetAddress(this.adapter.getService().getUdpServer().getRemoteAddress());

        this.connection.sendPacket(connectionPacket);
    }

    @Override
    public void handlePlayerStatePacket(PlayerStatePacket<?> packet) {
        this.connection.getPlayer().setDeafened(packet.isVoiceDisabled());
        this.connection.getPlayer().setMuted(packet.isMicrophoneMuted());
        this.connection.getPlayer().updateState();
    }

    @Override
    public void handleSourceInfoRequestPacket(SourceInfoRequestPacket packet) {
        UUID sourceId = packet.getSourceId();

        SourceInfo sourceInfo = this.connection.getSourceInfo(sourceId);
        if (sourceInfo == null) {
            return;
        }

        this.connection.sendSourceUpdate(sourceInfo);
    }
}
