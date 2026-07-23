package dev.minceraft.sonus.svc.protocol.util;

import dev.minceraft.sonus.common.version.Versioned;
import net.kyori.adventure.key.Key;

import java.util.HashSet;
import java.util.Set;

import static dev.minceraft.sonus.common.version.Versioned.entry;
import static dev.minceraft.sonus.svc.protocol.version.VersionManager.V_18;
import static dev.minceraft.sonus.svc.protocol.version.VersionManager.V_20;

public final class SvcPluginChannels {

    private static final Set<Key> CHANNELS = new HashSet<>();
    private static final String NAMESPACE = "voicechat";

    public static final Key INIT = register(Key.key(NAMESPACE, "init"));
    public static final Key SECRET = register(Key.key(NAMESPACE, "secret"));
    public static final Key REQUEST_SECRET = register(Key.key(NAMESPACE, "request_secret"));
    public static final Versioned<Key> PLAYER_STATE = register(
            entry(V_18, Key.key(NAMESPACE, "player_state")),
            entry(V_20, Key.key(NAMESPACE, "state")));
    public static final Versioned<Key> PLAYER_STATES = register(
            entry(V_18, Key.key(NAMESPACE, "player_states")),
            entry(V_20, Key.key(NAMESPACE, "states")));
    public static final Versioned<Key> REMOVE_PLAYER_STATE = register(
            entry(V_20, Key.key(NAMESPACE, "remove_state"))
    );
    public static final Key SET_GROUP = register(Key.key(NAMESPACE, "set_group"));
    public static final Key CREATE_GROUP = register(Key.key(NAMESPACE, "create_group"));
    public static final Key ADD_GROUP = register(Key.key(NAMESPACE, "add_group"));
    public static final Key REMOVE_GROUP = register(Key.key(NAMESPACE, "remove_group"));
    public static final Key LEAVE_GROUP = register(Key.key(NAMESPACE, "leave_group"));
    public static final Key JOINED_GROUP = register(Key.key(NAMESPACE, "joined_group"));
    public static final Key ADD_CATEGORY = register(Key.key(NAMESPACE, "add_category"));
    public static final Key REMOVE_CATEGORY = register(Key.key(NAMESPACE, "remove_category"));
    public static final Key UPDATE_STATE = register(Key.key(NAMESPACE, "update_state"));

    private SvcPluginChannels() {
    }

    private static Key register(Key key) {
        CHANNELS.add(key);
        return key;
    }

    @SafeVarargs
    private static Versioned<Key> register(Versioned.VersionedKeyEntry<Key>... entries) {
        for (Versioned.VersionedKeyEntry<Key> entry : entries) {
            CHANNELS.add(entry.key());
        }

        return new Versioned<>(entries);
    }

    public static Set<Key> getChannels() {
        return Set.copyOf(CHANNELS);
    }
}
