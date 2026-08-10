package dev.minceraft.sonus.svc.adapter;

import dev.minceraft.sonus.common.adapter.IPlayerManager;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.common.protocol.tcp.AbstractPluginMessageCodec;
import dev.minceraft.sonus.common.protocol.tcp.IPluginMessageSource;
import dev.minceraft.sonus.common.protocol.tcp.holder.PmDataHolderBuf;
import dev.minceraft.sonus.svc.adapter.connection.SvcConnection;
import dev.minceraft.sonus.svc.protocol.SvcPacketContext;
import dev.minceraft.sonus.svc.protocol.meta.SvcMetaPacket;
import dev.minceraft.sonus.svc.protocol.meta.servicebound.RequestSecretSvcPacket;
import dev.minceraft.sonus.svc.protocol.registries.SvcMetaPacketRegistry;
import dev.minceraft.sonus.svc.protocol.util.SvcPluginChannels;
import dev.minceraft.sonus.svc.protocol.version.VersionManager;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.minceraft.sonus.common.SonusConstants.PERMISSION_CONNECT_SVC;

@NullMarked
public class SvcPluginMessageCodec extends AbstractPluginMessageCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger("Sonus");

    private final SvcProtocolAdapter protocolAdapter;

    public SvcPluginMessageCodec(SvcProtocolAdapter protocolAdapter) {
        super(SvcPluginChannels.getChannels());
        this.protocolAdapter = protocolAdapter;
    }

    @Override
    public void handle(ByteBuf packet, Key channel, IPluginMessageSource source) {
        if (!(source instanceof IPluginMessageSource.Player)) {
            return;
        }

        SvcSessionManager sessionManager = this.protocolAdapter.getAdapter().getSessions();
        SvcConnection connection = sessionManager.getConnection(source.getPlayerId());

        PmDataHolderBuf data = PmDataHolderBuf.newInstance(packet, channel);
        SvcMetaPacket metaPacket;
        try {
            SvcPacketContext ctx = connection != null ? connection.getContext() : SvcPacketContext.INITIAL;
            metaPacket = SvcMetaPacketRegistry.BUF_REGISTRY.decode(data, ctx);
        } finally {
            data.recycle();
        }
        if (metaPacket == null) {
            return;
        }

        if (connection == null) {
            if (metaPacket instanceof RequestSecretSvcPacket secret) {
                // initialize connection when client requests secret
                IPlayerManager players = this.protocolAdapter.getAdapter().getService().getPlayerManager();
                ISonusPlayer player = players.getPlayer(source.getPlayerId());
                connection = this.initConnection(secret, player);

                if (connection != null) {
                    // add connection to session if initialized
                    sessionManager.addConnection(connection);
                    player.setConnected(true);
                } else {
                    return; // initialization wasn't successful
                }
            } else {
                return; // first packet needs to be a request
            }
        } else if (metaPacket instanceof RequestSecretSvcPacket) {
            LOGGER.warn("Duplicate SVC connection request received by {}", source.getPlayerId());
            return;
        }
        // handle metadata packet
        metaPacket.handle(connection.getMetaHandler());
    }

    private @Nullable SvcConnection initConnection(RequestSecretSvcPacket packet, @Nullable ISonusPlayer player) {
        if (!VersionManager.SUPPORTED_VERSIONS.contains(packet.getCompatibilityVersion())) {
            return null; // incompatible version
        } else if (player == null || !player.hasPermission(PERMISSION_CONNECT_SVC, true)) {
            return null; // no player instance present or no permission to connect via SVC
        }
        if (!player.setAdapter(this.protocolAdapter.getAdapter())) {
            return null; // player is not allowed to use this adapter
        }
        return new SvcConnection(this.protocolAdapter, player);
    }
}
