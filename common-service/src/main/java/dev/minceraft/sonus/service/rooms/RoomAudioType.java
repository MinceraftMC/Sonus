package dev.minceraft.sonus.service.rooms;

public enum RoomAudioType {

    OPEN(true, true),
    NORMAL(false, true),
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
