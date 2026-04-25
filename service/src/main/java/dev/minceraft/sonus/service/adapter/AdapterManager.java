package dev.minceraft.sonus.service.adapter;
// Created by booky10 in Sonus (01:45 10.08.2025)

import dev.minceraft.sonus.common.adapter.SonusAdapter;
import dev.minceraft.sonus.common.protocol.adapter.UdpSonusAdapter;
import dev.minceraft.sonus.service.SonusService;
import net.kyori.adventure.util.Services;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@NullMarked
public final class AdapterManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("Sonus");

    private final SonusService service;
    private final Set<SonusAdapter> adapters = Services.services(SonusAdapter.class);
    private final @Nullable UdpSonusAdapter[] adaptersByMagic = new UdpSonusAdapter[0xFF + 1];

    public AdapterManager(SonusService service) {
        this.service = service;
    }

    public void load() {
        for (SonusAdapter adapter : this.adapters) {
            try {
                LOGGER.info("Loading {} adapter...", adapter.getClass().getSimpleName());
                adapter.load(this.service);
            } catch (Throwable throwable) {
                LOGGER.warn("Failed to load {}", adapter.getClass().getSimpleName(), throwable);
            }
        }
    }

    public void init() {
        for (SonusAdapter adapter : this.adapters) {
            try {
                if (!adapter.getAdapterInfo().enabled()) {
                    LOGGER.info("{} adapter is disabled, skipping initialization.", adapter.getClass().getSimpleName());
                    continue;
                }
                LOGGER.info("Initializing {} adapter...", adapter.getClass().getSimpleName());
                adapter.init(this.service);
                UdpSonusAdapter proto = adapter.getUdpAdapter();
                if (proto != null) {
                    this.adaptersByMagic[proto.getMagicByte() & 0xFF] = proto;
                }
            } catch (Throwable throwable) {
                LOGGER.warn("Failed to initialize {}", adapter.getClass().getSimpleName(), throwable);
            }
        }
    }

    public void shutdown() {
        for (SonusAdapter adapter : this.adapters) {
            try {
                LOGGER.info("Shutting down {} adapter...", adapter.getClass().getSimpleName());
                adapter.shutdown(this.service);
            } catch (Throwable throwable) {
                LOGGER.warn("Failed to shutdown {}", adapter.getClass().getSimpleName(), throwable);
            }
        }
    }

    public @Nullable UdpSonusAdapter getAdapter(byte magicByte) {
        return this.adaptersByMagic[magicByte & 0xFF];
    }

    public Set<SonusAdapter> getAdapters() {
        return this.adapters;
    }

    @Nullable
    public <T extends SonusAdapter> T getAdapter(Class<T> adapterClass) {
        for (SonusAdapter adapter : this.adapters) {
            if (adapterClass.isInstance(adapter)) {
                return adapterClass.cast(adapter);
            }
        }
        return null;
    }
}
