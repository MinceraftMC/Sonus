package dev.minceraft.sonus.plasmo.adapter;

import dev.minceraft.sonus.common.adapter.ISonusService;
import dev.minceraft.sonus.common.protocol.adapter.UdpSonusAdapter;
import dev.minceraft.sonus.common.protocol.udp.IUdpServer;
import dev.minceraft.sonus.common.protocol.udp.UdpBasedContext;
import dev.minceraft.sonus.plasmo.adapter.pipeline.PlasmoCipherCodec;
import dev.minceraft.sonus.plasmo.adapter.pipeline.PlasmoPacketCodec;
import dev.minceraft.sonus.plasmo.adapter.pipeline.PlasmoPlayerMarkerCodec;
import dev.minceraft.sonus.plasmo.adapter.pipeline.PlasmoUdpContext;
import dev.minceraft.sonus.plasmo.adapter.pipeline.PlasmoUdpHandler;
import dev.minceraft.sonus.plasmo.protocol.PlasmoUdpMagicCodec;
import dev.minceraft.sonus.plasmo.protocol.tcp.data.CodecInfo;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
public class PlasmoProtocolAdapter implements UdpSonusAdapter {

    private final PlasmoUdpMagicCodec plasmoCodec = new PlasmoUdpMagicCodec(this);
    private @MonotonicNonNull CodecInfo codecInfo;

    public PlasmoProtocolAdapter(PlasmoAdapter adapter) {
        ISonusService service = adapter.getService();
        IUdpServer udpServer = service.getUdpServer();

        udpServer.registerCodec(this.plasmoCodec);
        udpServer.registerHandler("plasmo-codec", new PlasmoPacketCodec(this.plasmoCodec));
        udpServer.registerHandler("plasmo-player-marker", new PlasmoPlayerMarkerCodec(this.plasmoCodec, adapter));
        udpServer.registerHandler("plasmo-cipher", new PlasmoCipherCodec(this.plasmoCodec));
        udpServer.registerHandler("plasmo-handler", new PlasmoUdpHandler(this.plasmoCodec));

        service.getPluginMessenger().registerCodec(new PlasmoPluginMessageCodec(adapter));

        adapter.getService().getConfigHolder().addReloadHookAndRun(config -> {
            this.codecInfo = new CodecInfo("opus",
                    Map.of("mode", config.getOpusCodec().name(),
                            "bitrate", "-1000"
                    ));
        });
    }

    @Override
    public byte getMagicByte() {
        return this.plasmoCodec.getMagicByte();
    }

    @Override
    public UdpBasedContext<?> newPipelineContext() {
        return PlasmoUdpContext.newInstance();
    }

    public CodecInfo getCodecInfo() {
        return this.codecInfo;
    }

    public PlasmoUdpMagicCodec getPlasmoCodec() {
        return this.plasmoCodec;
    }
}
