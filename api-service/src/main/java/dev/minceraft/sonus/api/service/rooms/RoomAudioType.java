package dev.minceraft.sonus.api.service.rooms;

/**
 * The room isolation types:
 * <ul>
 * <li>{@link RoomAudioType#OPEN}</li>
 * <li>{@link RoomAudioType#NORMAL}</li>
 * <li>{@link RoomAudioType#ISOLATED}</li>
 * </ul>
 */
public enum RoomAudioType {

    /**
     * Non-members can her player in the room, and the members can hear non-members too.
     */
    OPEN(true, true),
    /**
     * Non-members can't hear members, but members can hear them.
     */
    NORMAL(false, true),
    /**
     * Non-members can't hear members, and members can't hear them either.
     */
    ISOLATED(false, false),
    ;

    private final boolean speakToOthers;
    private final boolean listenToOthers;

    RoomAudioType(boolean speakToOthers, boolean listenToOthers) {
        this.speakToOthers = speakToOthers;
        this.listenToOthers = listenToOthers;
    }

    public boolean isSpeakToOthers() {
        return this.speakToOthers;
    }

    public boolean isListenToOthers() {
        return this.listenToOthers;
    }
}
