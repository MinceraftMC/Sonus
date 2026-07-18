package dev.minceraft.sonus.plasmo.adapter;

import dev.minceraft.sonus.common.protocol.tcp.AbstractPluginMessageCodec;
import dev.minceraft.sonus.common.protocol.tcp.IPluginMessageSource;
import dev.minceraft.sonus.plasmo.adapter.connection.PlasmoConnection;
import dev.minceraft.sonus.plasmo.protocol.PlasmoPmChannels;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPacketRegistry;
import dev.minceraft.sonus.plasmo.protocol.tcp.TcpPlasmoPacket;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.key.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlasmoPluginMessageCodec extends AbstractPluginMessageCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger("Sonus");
    private final PlasmoAdapter adapter;

    public PlasmoPluginMessageCodec(PlasmoAdapter adapter) {
        super(PlasmoPmChannels.CHANNELS);
        this.adapter = adapter;
    }

    @Override
    public void handle(ByteBuf packet, Key channel, IPluginMessageSource source) {
        if (!(source instanceof IPluginMessageSource.Player)) {
            return;
        }
        PlasmoConnection connection = this.adapter.getSessionManager().getConnectionByUniqueId(source.getPlayerId());
        if (connection == null) {
            LOGGER.warn("{} tried to connect without initial invite", source.getPlayerId());
            return;
        }
        TcpPlasmoPacket<?> packetObj = TcpPacketRegistry.REGISTRY.decode(packet);
        if (packetObj != null) {
            packetObj.handle(connection.getMetaHandler());
        }
    }
}
