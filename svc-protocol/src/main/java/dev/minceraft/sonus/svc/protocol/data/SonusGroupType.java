package dev.minceraft.sonus.svc.protocol.data;

import dev.minceraft.sonus.common.rooms.RoomAudioType;
import net.kyori.adventure.util.Index;

public enum SonusGroupType {

    NORMAL("normal"),
    OPEN("open"),
    ISOLATED("isolated");

    public static final Index<String, SonusGroupType> ID_INDEX = Index.create(
            SonusGroupType.class, SonusGroupType::getId);

    private final String id;

    SonusGroupType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public RoomAudioType toSonus() {
        return switch (this) {
            case OPEN -> RoomAudioType.OPEN;
            case ISOLATED -> RoomAudioType.ISOLATED;
            case NORMAL -> RoomAudioType.NORMAL;
        };
    }

    public static SonusGroupType fromSonus(RoomAudioType type) {
        return switch (type) {
            case OPEN -> OPEN;
            case ISOLATED -> ISOLATED;
            case NORMAL -> NORMAL;
        };
    }
}
