package dev.minceraft.sonus.api.service.participant.builtin;

import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.api.service.participant.ISonusListener;
import dev.minceraft.sonus.api.service.participant.ISonusSource;
import dev.minceraft.sonus.api.service.rooms.ISonusRoom;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

/**
 * The player interface for the sonus service player
 */
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

    /**
     * If the player is muted, he can't send audio
     *
     * @return true if the player is muted, false otherwise
     */
    boolean isMuted();

    /**
     * Safe mute or unmute a player. It respects permissions checks.
     * <p>
     * If force to unmute or mute a player see {@link #setMuted(boolean, boolean)}.
     *
     * @param muted
     */
    default void setMuted(boolean muted) {
        this.setMuted(muted, false);
    }

    /**
     * Mutes or unmutes a player.
     * <p>
     * If you don't want to ignore the permission, see {@link #setMuted(boolean)}.
     * </p>
     *
     * @param mute             mute true, unmute false
     * @param ignorePermission whenever ignore the permissions or not
     */
    void setMuted(boolean mute, boolean ignorePermission);

    /**
     * If the player is deafened, he can't hear any audio.
     *
     * @return true if deafened, false if not
     */
    boolean isDeafened();

    /**
     * Deaf or undeaf a player.
     *
     * @param deafened deaf true, undeaf false
     */
    void setDeafened(boolean deafened);

    /**
     * If a player has connected to the voice chat.
     * <p>
     * <strong>WARNING: This does not have to mean the player has active voice connection. Initializing connection as example
     * see {@link #isVoiceActive()} for that.</strong>
     *
     * @return whether a player is connected or not
     */
    boolean isConnected();

    /**
     * If a player has an active voice connection. Voice adapter has been set.
     *
     * @return whether a player has active voice connection or not
     */
    boolean isVoiceActive();

    /**
     * Simulates a microphone input. Consider to send an audio end via {@link #handleAudioInputEnd(long)} after sending the last audio part.
     *
     * @param audio audio frame.
     */
    void handleAudioInput(ISonusAudio audio);

    /**
     * Simulated a microphone input end packet.
     *
     * @param sequence latest sequence number
     */
    void handleAudioInputEnd(long sequence);

    /**
     * Checks if the player can see the target. It can respect vanish systems.
     *
     * @param target the target player the player should see
     * @return whether he can see him or not
     */
    boolean canSee(ISonusServicePlayer target);

    /**
     * Checks if the player can receive any data from target. Same primary room or same server
     *
     * @param target the target player the player should receive data from
     * @return whether he can receive data from him or not
     */
    boolean canReceive(ISonusServicePlayer target);

    /**
     * Get locale of the player
     *
     * @return the locale
     */
    Locale getLocale();

    /**
     * Renders a kyori component with the given language. If you want to render a component with the players language, see {@link #renderComponent(Component)}.
     *
     * @param component the raw component to render
     * @param locale    the given locale
     * @return rendered component
     */
    Component renderComponent(Component component, Locale locale);

    /**
     * Renders a kyori component with the players' language.
     *
     * @param component the raw component to render
     * @return rendered component
     */
    default Component renderComponent(Component component) {
        return this.renderComponent(component, this.getLocale());
    }

    /**
     * Renders a kyori component to a plain string with the given language. If you want to render a component with the players language, see {@link #renderPlainComponent(Component)}.
     *
     * @param component the raw component to render
     * @param locale the given locale
     * @return rendered plain string
     */
    String renderPlainComponent(Component component, Locale locale);

    /**
     * Renders a kyori component to a plain string with the players' language.
     *
     * @param component the raw component to render
     * @return rendered plain string
     */
    default String renderPlainComponent(Component component) {
        return this.renderPlainComponent(component, this.getLocale());
    }

    /**
     * Sends a message to the player
     *
     * @param message the message
     */
    void sendMessage(Component message);

    /**
     * Disconnects the player of the current session. This session will be restored after a server switch.
     */
    void disconnect();
}
