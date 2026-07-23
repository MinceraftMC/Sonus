package dev.minceraft.sonus.service.agent;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import dev.minceraft.sonus.service.SonusConstants;
import dev.minceraft.sonus.protocol.meta.IAgentManager;
import dev.minceraft.sonus.protocol.meta.IMetaHandler;
import dev.minceraft.sonus.protocol.meta.SonusAgentPmCodec;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.server.SonusServer;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@NullMarked
public class AgentManager implements IAgentManager {

    private final SonusService service;
    private final LoadingCache<UUID, AgentListener> listeners = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public AgentListener load(UUID key) {
                    SonusServer server = AgentManager.this.service.getPlayerManager().getServer(key);
                    return new AgentListener(AgentManager.this.service, server);
                }
            });
    private final SonusAgentPmCodec codec;

    public AgentManager(SonusService service) {
        this.service = service;
        this.codec = new SonusAgentPmCodec(Set.of(Key.key(SonusConstants.PLUGIN_MESSAGE_CHANNEL)), this);
    }

    @Override
    public IMetaHandler getAgentListener(UUID serverId) {
        return this.listeners.getUnchecked(serverId);
    }

    public void init() {
        this.service.getPluginMessenger().registerCodec(this.codec);
    }
}
