package dev.minceraft.sonus.api.service.participant.builtin;

import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.api.service.participant.ISonusListener;
import dev.minceraft.sonus.api.service.participant.ISonusSource;
import dev.minceraft.sonus.api.service.rooms.ISonusRoom;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

@NullMarked
public interface ISonusServicePlayer extends ISonusSource, ISonusListener {

    /**
     * Returns the display name of the player, with a viewer context.
     * The context can be used to respect nick systems. If this isn't need use {@link #getName()} instead.
     *
     * @param viewer the viewer of the player
     * @return the displayname
     */
    String getName(@Nullable ISonusServicePlayer viewer);

    /**
     * Returns the display name of the player
     *
     * @return the display name
     */
    default String getName() {
        return this.getName(null);
    }

    /**
     * Sonus periodically checks if the player is still connected to the server. This method returns the last time Sonus received a keep alive from the player.
     *
     * @return the lastest keep alive timestamp in ms
     */
    long getLastKeepAlive();

    /**
     * The Sonus backend agent will transmit the players team. Used for special rooms or context based audio routing.
     *
     * @return the players team
     */
    @Nullable
    String getTeam();

    /**
     * Checks if a player can join the specified room. Password and permission check
     *
     * @param room     the room to check access for
     * @param password the password for the room
     * @return true if the player can access the room, false otherwise
     */
    boolean canAccessRoom(ISonusRoom room, @Nullable String password);

    /**
     * Joins the player into the specified room. It ignores all permissions and forces the join.
     *
     * @param room the target room to join
     */
    void joinRoom(ISonusRoom room);

    /**
     * Removes the player from the specified room.
     *
     * @param room the target room to leave
     */
    void leaveRoom(ISonusRoom room);

    /**
     * Returns the server room of the player. The server room is a special room that is used to route audio to all players on the same server.
     *
     * @return the server room
     */
    @Nullable
    ISonusRoom getServerRoom();

    /**
     * Sets the server room.
     *
     * @param room the target server room
     */
    void setServerRoom(@Nullable ISonusRoom room);

    /**
     * Returns the primary room of the player. For example the classic group rooms with multiple players.
     *
     * @return the primary room
     */
    @Nullable
    ISonusRoom getPrimaryRoom();

    /**
     * Sets the primary room of the player. For example the classic group rooms with multiple players.
     * <p>
     * Use null to leave
     *
     * @param room target room
     */
    void setPrimaryRoom(@Nullable ISonusRoom room);
    
    boolean isMuted();

    default void setMuted(boolean muted) {
        this.setMuted(muted, false);
    }

    void setMuted(boolean mute, boolean ignorePermission);

    boolean isDeafened();

    void setDeafened(boolean deafened);

    boolean isConnected();

    boolean isVoiceActive();

    void handleAudioInput(ISonusAudio audio);

    void handleAudioInputEnd(long sequence);

    boolean canSee(ISonusServicePlayer target);

    boolean canReceive(ISonusServicePlayer target);

    Locale getLocale();

    Component renderComponent(Component component, Locale locale);

    default Component renderComponent(Component component) {
        return this.renderComponent(component, this.getLocale());
    }

    String renderPlainComponent(Component component, Locale locale);

    default String renderPlainComponent(Component component) {
        return this.renderPlainComponent(component, this.getLocale());
    }

    void sendMessage(Component message);

    boolean isOnline();

    void disconnect();
}
