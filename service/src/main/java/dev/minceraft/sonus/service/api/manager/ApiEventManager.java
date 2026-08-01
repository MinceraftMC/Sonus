package dev.minceraft.sonus.service.api.manager;

import dev.minceraft.sonus.api.service.event.ISonusEvents;
import dev.minceraft.sonus.api.service.manager.ISonusEventManager;
import dev.minceraft.sonus.common.adapter.service.ISonusServiceEvents;
import dev.minceraft.sonus.common.participant.builtin.IRoom;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.service.SonusService;
import dev.minceraft.sonus.service.api.ApiRoom;
import dev.minceraft.sonus.service.api.participant.builtin.ApiSonusPlayer;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NullMarked
public class ApiEventManager implements ISonusServiceEvents, ISonusEventManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("Sonus");

    private final Set<ISonusEvents> listeners = new HashSet<>();

    public ApiEventManager(SonusService service) {
        service.getEventManager().registerListener(this);
    }

    @Override
    public void registerListener(ISonusEvents events) {
        listeners.add(events);
    }

    @Override
    public void onPlayerSwitchBackend(UUID playerId) {
        for (ISonusEvents listener : this.listeners) {
            try {
                listener.onPlayerSwitchBackend(playerId);
            } catch (Exception exception) {
                LOGGER.warn("Error in onPlayerSwitchBackend for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onConnectionState(ISonusPlayer player) {
        for (ISonusEvents listener : this.listeners) {
            try {
                listener.onConnectionState(new ApiSonusPlayer(player));
            } catch (Exception exception) {
                LOGGER.warn("Error in onConnectionState for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onGroupRemove(IRoom room) {
        for (ISonusEvents listener : this.listeners) {
            try {
                listener.onGroupRemove(new ApiRoom(room));
            } catch (Exception exception) {
                LOGGER.warn("Error in onGroupRemove for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onGroupCreate(IRoom room) {
        for (ISonusEvents listener : this.listeners) {
            try {
                listener.onGroupCreate(new ApiRoom(room));
            } catch (Exception exception) {
                LOGGER.warn("Error in onGroupCreate for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onPrimaryRoomLeaved(ISonusPlayer player, IRoom room) {
        for (ISonusEvents listener : this.listeners) {
            try {
                listener.onPrimaryRoomLeaved(new ApiSonusPlayer(player), new ApiRoom(room));
            } catch (Exception exception) {
                LOGGER.warn("Error in onPrimaryRoomLeaved for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onPrimaryRoomJoined(ISonusPlayer player, IRoom room) {
        for (ISonusEvents listener : this.listeners) {
            try {
                listener.onPrimaryRoomJoined(new ApiSonusPlayer(player), new ApiRoom(room));
            } catch (Exception exception) {
                LOGGER.warn("Error in onPrimaryRoomJoined for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onChannelRegistered(UUID playerId, Set<Key> channel) {
        for (ISonusEvents listener : this.listeners) {
            try {
                listener.onChannelRegistered(playerId, channel);
            } catch (Exception exception) {
                LOGGER.warn("Error in onChannelRegistered for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onPlayerStateUpdate(ISonusPlayer player, boolean globalUpdate) {
        for (ISonusEvents listener : this.listeners) {
            try {
                listener.onPlayerStateUpdate(new ApiSonusPlayer(player), globalUpdate);
            } catch (Exception exception) {
                LOGGER.warn("Error in onPlayerStateUpdate for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onPlayerNickUpdate(ISonusPlayer player, UUID previousNick) {
        for (ISonusEvents listener : this.listeners) {
            try {
                listener.onPlayerNickUpdate(new ApiSonusPlayer(player), previousNick);
            } catch (Exception exception) {
                LOGGER.warn("Error in onPlayerNickUpdate for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onPlayerQuit(ISonusPlayer player) {
        for (ISonusEvents listener : this.listeners) {
            try {
                listener.onPlayerQuit(new ApiSonusPlayer(player));
            } catch (Exception exception) {
                LOGGER.warn("Error in onPlayerQuit for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onPlayerDisconnect(ISonusPlayer player) {
        for (ISonusEvents listener : this.listeners) {
            try {
                listener.onPlayerDisconnect(new ApiSonusPlayer(player));
            } catch (Exception exception) {
                LOGGER.warn("Error in onPlayerDisconnect for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }
}
