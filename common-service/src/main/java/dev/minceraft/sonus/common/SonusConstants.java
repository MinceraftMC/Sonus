package dev.minceraft.sonus.common;
// Created by booky10 in Sonus (01:50 17.07.2025)

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class SonusConstants {

    public static final String PLUGIN_MESSAGE_CHANNEL = "sonus:agent";
    public static final Key PLUGIN_MESSAGE_CHANNEL_KEY = Key.key(PLUGIN_MESSAGE_CHANNEL);

    public static final int BITS_PER_SAMPLE = Short.SIZE;
    public static final int SAMPLE_RATE = 48_000; // 48 kHz
    public static final int FRAMES_INTERVAL = 20;
    public static final int FRAMES_PER_SECOND = 1000 / FRAMES_INTERVAL;
    public static final int FRAME_SIZE = SAMPLE_RATE / FRAMES_PER_SECOND;
    public static final int CHANNELS = 1; // Mono
    public static final int CURRENT_VERSION = 0;

    public static final String PERMISSION_VOICE_SPEAK = "sonus.voice.speak";
    public static final String PERMISSION_VOICE_LISTEN = "sonus.voice.listen";
    public static final String PERMISSION_CONNECT = "sonus.connect";
    public static final String PERMISSION_CONNECT_SVC = "sonus.connect.svc";
    public static final String PERMISSION_CONNECT_PLASMO = "sonus.connect.plasmo";
    public static final String PERMISSION_CONNECT_WEB = "sonus.connect.web";
    public static final String PERMISSION_GROUPS_BYPASS_PASSWORD = "sonus.groups.bypass.password";
    public static final String PERMISSION_GROUPS_USE = "sonus.groups.use";

    private SonusConstants() {
    }
}
