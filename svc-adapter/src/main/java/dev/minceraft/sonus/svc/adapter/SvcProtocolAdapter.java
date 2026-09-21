package dev.minceraft.sonus.svc.adapter;
// Created by booky10 in Sonus (02:19 10.08.2025)


import dev.minceraft.sonus.common.adapter.ISonusService;
import dev.minceraft.sonus.common.protocol.adapter.UdpSonusAdapter;
import dev.minceraft.sonus.common.protocol.udp.IUdpServer;
import dev.minceraft.sonus.common.protocol.udp.UdpBasedContext;
import dev.minceraft.sonus.svc.adapter.pipeline.SvcCipherCodec;
import dev.minceraft.sonus.svc.adapter.pipeline.SvcFrameCodec;
import dev.minceraft.sonus.svc.adapter.pipeline.SvcHandler;
import dev.minceraft.sonus.svc.adapter.pipeline.SvcPacketCodec;
import dev.minceraft.sonus.svc.adapter.pipeline.SvcPlayerMarkerCodec;
import dev.minceraft.sonus.svc.adapter.pipeline.SvcUdpContext;
import dev.minceraft.sonus.svc.protocol.SvcUdpMagicCodec;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SvcProtocolAdapter implements UdpSonusAdapter {

    private final SvcAdapter adapter;
    private final SvcUdpMagicCodec svcCodec = new SvcUdpMagicCodec(this);

    public SvcProtocolAdapter(SvcAdapter adapter) {
        this.adapter = adapter;
        ISonusService service = adapter.getService();
        IUdpServer udpServer = service.getUdpServer();

        udpServer.registerCodec(this.svcCodec);
        udpServer.registerHandler("svc-player-marker", new SvcPlayerMarkerCodec(this.svcCodec, this));
        udpServer.registerHandler("svc-frame", new SvcFrameCodec(this.svcCodec));
        udpServer.registerHandler("svc-cipher", new SvcCipherCodec(this.svcCodec));
        udpServer.registerHandler("svc-codec", new SvcPacketCodec(this.svcCodec));
        udpServer.registerHandler("svc-handler", new SvcHandler(this.svcCodec));

        service.getPluginMessenger().registerCodec(new SvcPluginMessageCodec(this));
    }

    @Override
    public byte getMagicByte() {
        return SvcUdpMagicCodec.MAGIC_BYTE;
    }

    @Override
    public UdpBasedContext<?> newPipelineContext() {
        return SvcUdpContext.newInstance();
    }

    public SvcAdapter getAdapter() {
        return this.adapter;
    }

    public SvcUdpMagicCodec getSvcCodec() {
        return this.svcCodec;
    }
}
