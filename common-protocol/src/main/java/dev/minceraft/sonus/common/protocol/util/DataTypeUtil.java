package dev.minceraft.sonus.common.protocol.util;
// Created by booky10 in Sonus (01:19 17.07.2025)

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import dev.minceraft.sonus.common.util.GameProfile;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

@NullMarked
public record DataTypeUtil(Function<ByteBuf, Integer> sizeReader, BiConsumer<ByteBuf, Integer> sizeWriter) {

    public static final DataTypeUtil VAR_INT = new DataTypeUtil(VarInt::read, VarInt::write);
    public static final DataTypeUtil INT = new DataTypeUtil(ByteBuf::readInt, ByteBuf::writeInt);

    public static UUID readUniqueId(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static void writeUniqueId(ByteBuf buf, UUID uniqueId) {
        buf.writeLong(uniqueId.getMostSignificantBits());
        buf.writeLong(uniqueId.getLeastSignificantBits());
    }

    @SuppressWarnings("PatternValidation")
    public static Key readKey(ByteBuf buf) {
        return Key.key(Utf8String.read(buf));
    }

    public static void writeKey(ByteBuf buf, Key key) {
        Utf8String.write(buf, key.asMinimalString());
    }

    public static <T> T readIfOrElse(ByteBuf buf, Function<ByteBuf, T> reader, Supplier<T> defaultValue) {
        return buf.readBoolean() ? reader.apply(buf) : defaultValue.get();
    }

    public static void writeIf(ByteBuf buf, boolean condition, Consumer<ByteBuf> writer) {
        buf.writeBoolean(condition);
        if (condition) {
            writer.accept(buf);
        }
    }

    public static <T> void writeNullable(ByteBuf buf, @Nullable T value, BiConsumer<ByteBuf, T> writer) {
        if (value != null) {
            buf.writeBoolean(true);
            writer.accept(buf, value);
        } else {
            buf.writeBoolean(false);
        }
    }

    public static <T> void writeNullableIf(ByteBuf buf, @Nullable T value, Predicate<T> condition, BiConsumer<ByteBuf, T> writer) {
        if (value != null && condition.test(value)) {
            buf.writeBoolean(true);
            writer.accept(buf, value);
        } else {
            buf.writeBoolean(false);
        }
    }

    public static <T> @Nullable T readNullable(ByteBuf buf, Function<ByteBuf, T> reader) {
        if (buf.readBoolean()) {
            return reader.apply(buf);
        }
        return null;
    }

    public static void writeComponentJson(ByteBuf buf, Component component) {
        String componentJson = GsonComponentSerializer.gson().serialize(component);
        Utf8String.write(buf, componentJson);
    }

    public static Component readComponentJson(ByteBuf buf) {
        String componentJson = Utf8String.read(buf);
        return GsonComponentSerializer.gson().deserialize(componentJson);
    }

    public <R, C, V> Table<R, C, V> readTable(ByteBuf buf, BufReader<R> rowReader, BufReader<C> columnReader, BufReader<V> valueReader) {
        HashBasedTable<R, C, V> table = HashBasedTable.create();
        int columnSize = this.sizeReader.apply(buf);
        for (int i = 0; i < columnSize; i++) {
            C column = columnReader.read(buf);
            int rowSize = this.sizeReader.apply(buf);
            for (int j = 0; j < rowSize; j++) {
                R row = rowReader.read(buf);
                V val = valueReader.read(buf);
                table.put(row, column, val);
            }
        }
        return table;
    }

    public <R, C, V> void writeTable(ByteBuf buf, Table<R, C, V> table, BufWriter<R> rowWriter, BufWriter<C> columnWriter, BufWriter<V> valueWriter) {
        Set<Map.Entry<C, Map<R, V>>> columns = table.columnMap().entrySet();
        this.sizeWriter.accept(buf, columns.size());
        for (Map.Entry<C, Map<R, V>> column : columns) {
            columnWriter.write(buf, column.getKey());
            this.writeMap(buf, column.getValue(), rowWriter, valueWriter);
        }
    }

    public <K, V> Map<K, V> readMap(ByteBuf buf, BufReader<K> keyReader, BufReader<V> valueReader) {
        return this.readMap(buf, keyReader, valueReader, HashMap::new);
    }

    public <M extends Map<K, V>, K, V> M readMap(
            ByteBuf buf,
            BufReader<K> keyReader,
            BufReader<V> valueReader,
            IntFunction<M> mapSupplier
    ) {
        int size = this.sizeReader.apply(buf);
        M map = mapSupplier.apply(size);
        for (int i = 0; i < size; i++) {
            K key = keyReader.read(buf);
            V value = valueReader.read(buf);
            map.put(key, value);
        }
        return map;
    }

    public <M extends Multimap<K, V>, K, V> M readMultiMap(
            ByteBuf buf,
            BufReader<K> keyReader,
            BufReader<V> valueReader,
            Supplier<M> mapSupplier
    ) {
        Map<K, Collection<V>> read = this.readMap(buf, keyReader, b -> this.readCollection(b, valueReader, ArrayList::new), HashMap::new);
        M map = mapSupplier.get();
        read.forEach(map::putAll);
        return map;
    }

    public <K, V> void writeMap(ByteBuf buf, Map<K, V> map, BufWriter<K> keyWriter, BufWriter<V> valueWriter) {
        this.sizeWriter.accept(buf, map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            keyWriter.write(buf, entry.getKey());
            valueWriter.write(buf, entry.getValue());
        }
    }

    public <K, V> void writeMultiMap(ByteBuf buf, Multimap<K, V> map, BufWriter<K> keyWriter, BufWriter<V> valueWriter) {
        this.writeMap(buf, map.asMap(), keyWriter, (b, col) -> this.writeCollection(b, col, valueWriter));
    }

    public <C extends Collection<T>, T> C readCollection(ByteBuf buf, BufReader<T> reader, IntFunction<C> collectionSupplier) {
        int size = this.sizeReader.apply(buf);
        C collection = collectionSupplier.apply(size);
        for (int i = 0; i < size; i++) {
            T item = reader.read(buf);
            collection.add(item);
        }
        return collection;
    }

    public <T> void writeCollection(ByteBuf buf, Collection<T> collection, BufWriter<T> writer) {
        this.sizeWriter.accept(buf, collection.size());
        for (T item : collection) {
            writer.write(buf, item);
        }
    }

    public void writeByteArray(ByteBuf buf, byte[] data) {
        this.sizeWriter.accept(buf, data.length);
        if (data.length > 0) {
            buf.writeBytes(data);
        }
    }

    public byte[] readByteArray(ByteBuf buf) {
        return this.readByteArray(buf, Integer.MAX_VALUE);
    }

    public byte[] readByteArray(ByteBuf buf, int maxLength) {
        int length = this.sizeReader.apply(buf);
        if (length == 0) {
            return new byte[0];
        } else if (length < 0) {
            throw new IllegalStateException("Illegal negative array length " + length);
        } else if (length > maxLength) {
            throw new IllegalStateException("Byte array with length " + length + " exceeds maximum of " + maxLength);
        } else if (!buf.isReadable(length)) {
            throw new IllegalStateException("Can't read " + length + " bytes for byte array");
        }
        byte[] data = new byte[length];
        buf.readBytes(data);
        return data;
    }

    public GameProfile readGameProfile(ByteBuf buf, Function<ByteBuf, String> stringReader) {
        UUID id = readUniqueId(buf);
        String name = stringReader.apply(buf);
        List<GameProfile.Property> properties = this.readCollection(buf, b -> this.readGameProfileProperty(b, stringReader), ArrayList::new);
        return new GameProfile(id, name, properties);
    }

    public void writeGameProfile(ByteBuf buf, GameProfile profile, BiConsumer<ByteBuf, String> stringWriter) {
        writeUniqueId(buf, profile.uniqueId());
        stringWriter.accept(buf, profile.name());
        this.writeCollection(buf, profile.properties(), (b, property) -> this.writeGameProfileProperty(b, property, stringWriter));
    }

    public GameProfile.Property readGameProfileProperty(ByteBuf buf, Function<ByteBuf, String> stringReader) {
        String name = stringReader.apply(buf);
        String value = stringReader.apply(buf);
        String signature = stringReader.apply(buf);
        return new GameProfile.Property(name, value, signature);
    }

    public void writeGameProfileProperty(ByteBuf buf, GameProfile.Property property, BiConsumer<ByteBuf, String> stringWriter) {
        stringWriter.accept(buf, property.name());
        stringWriter.accept(buf, property.value());
        stringWriter.accept(buf, property.signature());
    }
}
