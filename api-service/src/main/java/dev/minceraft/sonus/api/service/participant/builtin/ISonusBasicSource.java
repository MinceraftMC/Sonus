package dev.minceraft.sonus.api.service.participant.builtin;

import dev.minceraft.sonus.api.service.participant.ISonusSource;
import dev.minceraft.sonus.api.service.util.WorldRotatedVec3d;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * A basic source is a source that can be used to play audio.
 * It can be used to play audio from a specific position in the world, or from a specific server.
 * All fields are optional.
 */
@NullMarked
public interface ISonusBasicSource extends ISonusSource {

    /**
     * Sets the category id of this source.
     *
     * @param categoryId the category id of this source, or null if there is no category
     */
    void setCategoryId(@Nullable UUID categoryId);

    /**
     * Sets the server id of this source.
     *
     * @param serverId the server id of this source, or null if there is no server
     */
    void setServerId(@Nullable UUID serverId);

    /**
     * Sets the position of this source.
     *
     * @param position the position of this source, or null if there is no position
     */
    void setPosition(@Nullable WorldRotatedVec3d position);
}
