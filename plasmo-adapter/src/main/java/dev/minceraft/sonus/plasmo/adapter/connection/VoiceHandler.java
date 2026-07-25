package dev.minceraft.sonus.plasmo.adapter.connection;

import dev.minceraft.sonus.common.SonusConstants;
import dev.minceraft.sonus.common.audio.SonusAudio;
import dev.minceraft.sonus.plasmo.adapter.PlasmoAdapter;
import dev.minceraft.sonus.plasmo.protocol.tcp.clientbound.ConfigPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.clientbound.PlayerListPacket;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.CaptureInfo;
import dev.minceraft.sonus.plasmo.protocol.udp.UdpHandler;
import dev.minceraft.sonus.plasmo.protocol.udp.bothbound.PingPlasmoPacket;
import dev.minceraft.sonus.plasmo.protocol.udp.serverbound.PlayerAudioPlasmoPacket;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;

@NullMarked
public class VoiceHandler implements UdpHandler {

    private final PlasmoAdapter adapter;
    private final PlasmoConnection connection;
    private State state = State.WAITING_ACK;

    public VoiceHandler(PlasmoAdapter adapter, PlasmoConnection connection) {
        this.adapter = adapter;
        this.connection = connection;
    }

    @Override
    public void handlePlayerAudioPacket(PlayerAudioPlasmoPacket packet) {
        if (this.state == State.CONNECTED) {
            short[] pcm = this.connection.getProcessor(packet.getActivationId()).decode(packet.getAudioData());
            this.connection.getPlayer().handleAudioInput(SonusAudio.fromPcm(packet.getSequenceNumber(), pcm));
        }
    }

    @Override
    public void handlePingPacket(PingPlasmoPacket packet) {
        if (this.state == State.WAITING_ACK) {
            this.sendConfig();
            this.connection.setVoiceActive(true);

            this.connection.getPlayer().handleConnect();

            this.sendPlayerList();
            this.state = State.CONNECTED;
        }
        this.connection.getPlayer().setKeepAlive(System.currentTimeMillis());
    }

    private void sendConfig() {
        ConfigPacket configPacket = new ConfigPacket();
        configPacket.setPermissions(Map.of());
        configPacket.setServerId(this.adapter.getConfig().serverId);

        CaptureInfo captureInfo = new CaptureInfo(
                SonusConstants.SAMPLE_RATE,
                this.adapter.getService().getConfig().getMtuSize(),
                this.adapter.getUdpAdapter().getCodecInfo()
        );
        configPacket.setCaptureInfo(captureInfo);

        configPacket.setEncryptionInfo(this.connection.getCipher().getEncryptionInfo());
        configPacket.setSourceLines(this.connection.getSourceLines().values());
        configPacket.setActivations(this.connection.getVoiceActivations().values());

        this.connection.sendPacket(configPacket);
    }

    private void sendPlayerList() {
        PlayerListPacket playerListPacket = new PlayerListPacket();
        playerListPacket.setPlayers(List.copyOf(this.adapter.getSessionManager().getPlayerInfos(this.connection).values()));

        this.connection.sendPacket(playerListPacket);
    }

    public enum State {
        WAITING_ACK,
        CONNECTED,
    }
}
