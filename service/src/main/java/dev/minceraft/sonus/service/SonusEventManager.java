package dev.minceraft.sonus.service;

import dev.minceraft.sonus.service.data.ISonusPlayer;
import dev.minceraft.sonus.service.data.SonusPlayerState;
import dev.minceraft.sonus.service.rooms.IRoom;
import dev.minceraft.sonus.service.service.ISonusEventManager;
import dev.minceraft.sonus.service.service.ISonusServiceEvents;
import dev.minceraft.sonus.service.player.SonusPlayer;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NullMarked
public class SonusEventManager implements ISonusEventManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("Sonus");

    private final SonusService service;
    private final Set<ISonusServiceEvents> listeners = new HashSet<>();

    public SonusEventManager(SonusService service) {
        this.service = service;
    }

    @Override
    public void registerListener(ISonusServiceEvents events) {
        this.listeners.add(events);
    }

    @Override
    public void onPlayerSwitchBackend(UUID playerId) {
        for (ISonusServiceEvents listener : this.listeners) {
            try {
                listener.onPlayerSwitchBackend(playerId);
            } catch (Exception exception) {
                LOGGER.error("Error in onPlayerSwitchBackend for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
        this.service.getPlayerManager().onPlayerSwitchBackend(playerId);
    }

    @Override
    public void onPlayerDisconnect(ISonusPlayer player) {
        for (ISonusServiceEvents listener : this.listeners) {
            try {
                listener.onPlayerDisconnect(player);
            } catch (Exception exception) {
                LOGGER.error("Error in onPlayerDisconnect for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onPlayerQuit(ISonusPlayer player) {
        for (ISonusServiceEvents listener : this.listeners) {
            try {
                listener.onPlayerQuit(player);
            } catch (Exception exception) {
                LOGGER.error("Error in onPlayerQuit for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
        this.service.getPlayerManager().unregisterPlayer(player.getUniqueId());
    }

    @Override
    public void onPlayerNickUpdate(ISonusPlayer player, UUID previousNick) {
        for (ISonusServiceEvents listener : this.listeners) {
            try {
                listener.onPlayerNickUpdate(player, previousNick);
            } catch (Exception exception) {
                LOGGER.error("Error in onPlayerNickUpdate for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onPlayerStateUpdate(ISonusPlayer player, boolean globalUpdate) {
        for (ISonusServiceEvents listener : this.listeners) {
            try {
                listener.onPlayerStateUpdate(player, globalUpdate);
            } catch (Exception exception) {
                LOGGER.error("Error in onPlayerStateUpdate for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onChannelRegistered(UUID playerId, Set<Key> channel) {
        for (ISonusServiceEvents listener : this.listeners) {
            try {
                listener.onChannelRegistered(playerId, Set.copyOf(channel));
            } catch (Exception exception) {
                LOGGER.error("Error in onChannelRegistered for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onPrimaryRoomJoined(ISonusPlayer player, IRoom room) {
        for (ISonusServiceEvents listener : this.listeners) {
            try {
                listener.onPrimaryRoomJoined(player, room);
            } catch (Exception exception) {
                LOGGER.error("Error in onGroupJoin for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onPrimaryRoomLeaved(ISonusPlayer player, IRoom room) {
        for (ISonusServiceEvents listener : this.listeners) {
            try {
                listener.onPrimaryRoomLeaved(player, room);
            } catch (Exception exception) {
                LOGGER.error("Error in onGroupLeave for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onGroupCreate(IRoom room) {
        for (ISonusServiceEvents listener : this.listeners) {
            try {
                listener.onGroupCreate(room);
            } catch (Exception exception) {
                LOGGER.error("Error in onGroupCreate for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onGroupRemove(IRoom room) {
        for (ISonusServiceEvents listener : this.listeners) {
            try {
                listener.onGroupRemove(room);
            } catch (Exception exception) {
                LOGGER.error("Error in onGroupRemove for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void onConnectionState(ISonusPlayer player) {
        for (ISonusServiceEvents listener : this.listeners) {
            try {
                listener.onConnectionState(player);
            } catch (Exception exception) {
                LOGGER.error("Error in onConnectionState for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
        ((SonusPlayer) player).updateCommands();
    }

    @Override
    public void onPlayerVisibilityStateUpdate(ISonusPlayer player, ISonusPlayer target, SonusPlayerState state) {
        for (ISonusServiceEvents listener : this.listeners) {
            try {
                listener.onPlayerVisibilityStateUpdate(player, target, state);
            } catch (Exception exception) {
                LOGGER.error("Error in onPlayerVisibilityStateUpdate for listener {}", listener.getClass().getSimpleName(), exception);
            }
        }
    }
}
