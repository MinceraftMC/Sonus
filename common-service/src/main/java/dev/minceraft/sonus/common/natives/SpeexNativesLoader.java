package dev.minceraft.sonus.common.natives;
// Created by booky10 in Sonus (03:41 21.12.2025)

import org.jspecify.annotations.NullMarked;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import static java.lang.invoke.MethodType.methodType;

@NullMarked
public class SpeexNativesLoader extends NativesLoader {

    private final MethodHandle agcCtor;
    private final MethodHandle agcSetTarget;
    private final MethodHandle agcAgc;
    private final MethodHandle agcClose;

    public SpeexNativesLoader() {
        super("speex4j.jar");
        try {
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();

            Class<?> agcClass = this.loadClass("de.maxhenkel.speex4j.AutomaticGainControl");
            this.agcCtor = lookup.findConstructor(agcClass,
                    methodType(void.class, int.class, int.class));
            this.agcSetTarget = lookup.findVirtual(agcClass, "setTarget",
                    methodType(void.class, int.class));
            this.agcAgc = lookup.findVirtual(agcClass, "agc",
                    methodType(boolean.class, short[].class));
            this.agcClose = lookup.findVirtual(agcClass, "close",
                    methodType(void.class));
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    public final class AutomaticGainControl implements AutoCloseable {

        private final MethodHandle setTarget;
        private final MethodHandle agc;
        private final MethodHandle close;

        public AutomaticGainControl(int frameSize, int sampleRate) {
            Object agc;
            try {
                agc = SpeexNativesLoader.this.agcCtor.invoke(frameSize, sampleRate);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
            this.setTarget = SpeexNativesLoader.this.agcSetTarget.bindTo(agc);
            this.agc = SpeexNativesLoader.this.agcAgc.bindTo(agc);
            this.close = SpeexNativesLoader.this.agcClose.bindTo(agc);
        }

        public void setTarget(int target) {
            try {
                this.setTarget.invoke(target);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        public boolean agc(short[] input) {
            try {
                return (boolean) this.agc.invoke(input);
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
