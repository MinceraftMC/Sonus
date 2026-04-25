package dev.minceraft.sonus.common.protocol.util;

import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class TypeUtil {

    private TypeUtil() {
    }

    @SuppressWarnings("unchecked") // should be pretty safe
    public static <T> Class<T> resolveType(TypeToken<T> token) {
        return (Class<T>) GenericTypeReflector.erase(token.getType());
    }
}
