package dev.minceraft.sonus.common.util.codec;
// Created by booky10 in Sonus (9:34 PM 06.03.2026)

import io.netty.handler.codec.CodecException;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class QuietCodecException extends CodecException {

    public QuietCodecException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
