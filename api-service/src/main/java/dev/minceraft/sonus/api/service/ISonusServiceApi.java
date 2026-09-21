package dev.minceraft.sonus.api.service;

import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.api.service.manager.ISonusCategoryManager;
import dev.minceraft.sonus.api.service.manager.ISonusEventManager;
import dev.minceraft.sonus.api.service.manager.ISonusPlayerManager;
import dev.minceraft.sonus.api.service.manager.ISonusRoomManager;
import dev.minceraft.sonus.api.service.participant.builtin.ISonusBasicSource;
import net.kyori.adventure.util.Services;
import org.jspecify.annotations.NullMarked;

/**
 * The main API class for the Sonus Service
 */
@NullMarked
public interface ISonusServiceApi {

    /**
     * Utility method to get the instance of the SonusServiceApi
     * <strong>WARNING: If Sonus is not properly enabled before using this method, it will throw an exception</strong>
     *
     * @return the instance of the SonusServiceApi
     * @throws IllegalStateException if SonusService is not enabled before calling this method
     */
    static ISonusServiceApi getInstance() {
        if (InstanceHolder.API == null) {
            try {
                InstanceHolder.API = Services.service(ISonusServiceApi.class).orElseThrow();
            } catch (Exception exception) {
                throw new IllegalStateException("The Sonus Service Api has not been initialized yet. " +
                        "Please make sure to use this api after Sonus has been loaded.", exception);
            }
        }
        return InstanceHolder.API;
    }

    /**
     * Get the player manager. Manages all players that can interact with the Sonus Service.
     *
     * @return the player manager
     */
    ISonusPlayerManager getPlayerManager();

    /**
     * Get the room manager. Manages all rooms that are created in the Sonus Service.
     *
     * @return the room manager
     */
    ISonusRoomManager getRoomManager();

    /**
     * Get the event manager. Manages all events that occur in the Sonus Service.
     *
     * @return the event manager
     */
    ISonusEventManager getEventManager();

    /**
     * Get the category manager. Manages all audio categories.
     *
     * @return the category manager
     */
    ISonusCategoryManager getCategoryManager();

    /**
     * Creates the sonus audio wrapper with pcm data
     * <p>
     * You can also convert it into opus with {@link ISonusAudio#getOpus()}
     *
     * @param sequence the sequence number of the audio part
     * @param pcm      the raw pcm data
     * @return an instance of ISonusAudio holding the information
     */
    ISonusAudio audioFromPcm(long sequence, short[] pcm);

    /**
     * Create the sonus audio wrapper with opus data
     * <p>
     * You can also convert it into pcm with {@link ISonusAudio#getPcm()}
     *
     * @param sequence the sequence number of the audio part
     * @param opus     the raw opus data
     * @return an instance of ISonusAudio holding the information
     */
    ISonusAudio audioFromOpus(long sequence, byte[] opus);

    /**
     * Creates a basic source, see {@link ISonusBasicSource}
     *
     * @return an instance of ISonusBasicSource
     */
    ISonusBasicSource createBasicSource();
}
