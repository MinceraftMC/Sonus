package dev.minceraft.sonus.api.service.participant.builtin;

import dev.minceraft.sonus.api.service.participant.ISonusSource;
import dev.minceraft.sonus.api.service.util.WorldRotatedVec3d;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public interface ISonusBasicSource extends ISonusSource {

    void setCategoryId(@Nullable UUID categoryId);

    void setServerId(@Nullable UUID serverId);

    void setPosition(@Nullable WorldRotatedVec3d position);
}
