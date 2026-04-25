package dev.minceraft.sonus.common.natives;
// Created by booky10 in Sonus (03:41 21.12.2025)

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import javax.sound.sampled.AudioFormat;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import static java.lang.invoke.MethodType.methodType;

@NullMarked
public class LameNativesLoader extends NativesLoader {

    private final MethodHandle decoderCtor;
    private final MethodHandle decoderDecodeNextFrame;
    private final MethodHandle decoderGetSampleRate;
    private final MethodHandle decoderCreateAudioFormat;
    private final MethodHandle decoderClose;

    public LameNativesLoader() {
        super("lame4j.jar");
        try {
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();

            Class<?> decoderClass = this.loadClass("de.maxhenkel.lame4j.Mp3Decoder");
            this.decoderCtor = lookup.findConstructor(decoderClass,
                    methodType(void.class, InputStream.class));
            this.decoderDecodeNextFrame = lookup.findVirtual(decoderClass, "decodeNextFrame",
                    methodType(short[].class));
            this.decoderGetSampleRate = lookup.findVirtual(decoderClass, "getSampleRate",
                    methodType(int.class));
            this.decoderCreateAudioFormat = lookup.findVirtual(decoderClass, "createAudioFormat",
                    methodType(AudioFormat.class));
            this.decoderClose = lookup.findVirtual(decoderClass, "close",
                    methodType(void.class));
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    public final class Decoder implements AutoCloseable {

        private final MethodHandle decodeNextFrame;
        private final MethodHandle getSampleRate;
        private final MethodHandle createAudioFormat;
        private final MethodHandle close;

        public Decoder(InputStream input) {
            Object decoder;
            try {
                decoder = LameNativesLoader.this.decoderCtor.invoke(input);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
            this.decodeNextFrame = LameNativesLoader.this.decoderDecodeNextFrame.bindTo(decoder);
            this.getSampleRate = LameNativesLoader.this.decoderGetSampleRate.bindTo(decoder);
            this.createAudioFormat = LameNativesLoader.this.decoderCreateAudioFormat.bindTo(decoder);
            this.close = LameNativesLoader.this.decoderClose.bindTo(decoder);
        }

        public short @Nullable [] decodeNextFrame() {
            try {
                return (short[]) this.decodeNextFrame.invoke();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        public int getSampleRate() {
            try {
                return (int) this.getSampleRate.invoke();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        public @Nullable AudioFormat createAudioFormat() {
            try {
                return (AudioFormat) this.createAudioFormat.invoke();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        @Override
        public void close() {
            try {
                this.close.invoke();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
    }
}
