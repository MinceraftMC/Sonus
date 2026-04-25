package dev.minceraft.sonus.common.config.serializer;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public final class AddressSerializer implements TypeSerializer<InetSocketAddress> {

    public static final AddressSerializer INSTANCE = new AddressSerializer();
    private static final String PORT_REGEX = "(:(\\d{1,4}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5]))";
    private static final Pattern IPV6_REGEX = Pattern.compile("\\[(([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}|([\\da-fA-F]{1,4}:){1,7}:|([\\da-fA-F]{1,4}:){1,6}:[\\da-fA-F]{1,4}|([\\da-fA-F]{1,4}:){1,5}(:[\\da-fA-F]{1,4}){1,2}|([\\da-fA-F]{1,4}:){1,4}(:[\\da-fA-F]{1,4}){1,3}|([\\da-fA-F]{1,4}:){1,3}(:[\\da-fA-F]{1,4}){1,4}|([\\da-fA-F]{1,4}:){1,2}(:[\\da-fA-F]{1,4}){1,5}|[\\da-fA-F]{1,4}:((:[\\da-fA-F]{1,4}){1,6})|:((:[\\da-fA-F]{1,4}){1,7}|:)|fe80:(:[\\da-fA-F]{0,4}){0,4}%[\\da-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?\\d)?\\d)\\.){3}(25[0-5]|(2[0-4]|1?\\d)?\\d)|([\\da-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?\\d)?\\d)\\.){3}(25[0-5]|(2[0-4]|1?\\d)?\\d))]" + PORT_REGEX);

    // getting the hostname of an address takes 5 seconds in docker
    // I don't want to know which thing makes this timeout, but just work around it for now
    private static final Map<InetSocketAddress, String> STRINGIFY_CACHE = new IdentityHashMap<>();

    private AddressSerializer() {
    }

    @Override
    public @Nullable InetSocketAddress deserialize(Type type, ConfigurationNode node) {
        if (node.virtual()) {
            return null;
        }
        String address = Objects.requireNonNull(node.getString());

        InetSocketAddress socketAddress;
        if (IPV6_REGEX.matcher(address).matches()) {
            int adrStart = address.indexOf('[') + 1, adrEnd = address.indexOf(']');
            int port = Integer.parseInt(address.substring(adrEnd + 1 /* ] */ + 1 /* : */));
            socketAddress = new InetSocketAddress(address.substring(adrStart, adrEnd), port);
        } else {
            int portIndex = address.indexOf(':');
            int port = Integer.parseInt(address.substring(portIndex + 1));
            socketAddress = new InetSocketAddress(address.substring(0, portIndex), port);
        }

        synchronized (STRINGIFY_CACHE) {
            STRINGIFY_CACHE.put(socketAddress, address);
        }
        return socketAddress;
    }

    @Override
    public void serialize(Type type, @Nullable InetSocketAddress obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        String cachedAddress;
        synchronized (STRINGIFY_CACHE) {
            cachedAddress = STRINGIFY_CACHE.get(obj);
        }
        if (cachedAddress != null) {
            node.set(cachedAddress);
            return;
        }

        String hostName = obj.getHostName();
        if (hostName == null) {
            hostName = obj.getAddress().getHostAddress();
            if (obj.getAddress() instanceof Inet6Address) {
                hostName = "[" + hostName + "]";
            }
        }
        node.set(hostName + ":" + obj.getPort());
    }
}
