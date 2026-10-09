package dev.minceraft.sonus.common.util.codec;
// Created by booky10 in Sonus (01:26 17.07.2025)

import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.NullMarked;

@NullMarked
@FunctionalInterface
public interface BufReader<T> {

    T read(ByteBuf buf);
}
