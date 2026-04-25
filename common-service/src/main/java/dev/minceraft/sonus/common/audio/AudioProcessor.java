package dev.minceraft.sonus.common.audio;

import dev.minceraft.sonus.common.natives.OpusNativesLoader;
import dev.minceraft.sonus.common.natives.OpusNativesLoader.Decoder;
import dev.minceraft.sonus.common.natives.OpusNativesLoader.Encoder;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.function.IntSupplier;

import static dev.minceraft.sonus.common.SonusConstants.CHANNELS;
import static dev.minceraft.sonus.common.SonusConstants.FRAME_SIZE;
import static dev.minceraft.sonus.common.SonusConstants.SAMPLE_RATE;

/**
 * Initialization of decoder/encoder is not thread-safe!
 */
@NullMarked
public final class AudioProcessor implements AutoCloseable {

    private static final short[] ZERO_SHORT_ARRAY = new short[0];
    private static final byte[] ZERO_BYTE_ARRAY = new byte[0];

    private final OpusNativesLoader loader;
    private final IntSupplier mtu;
    private final Mode mode;

    private @MonotonicNonNull Decoder decoder;
    private @MonotonicNonNull Encoder encoder;

    public AudioProcessor(OpusNativesLoader loader, IntSupplier mtu, Mode mode) {
        this.loader = loader;
        this.mtu = mtu;
        this.mode = mode;
    }

    public short[] decode(byte[] data) {
        // 0 length data means state reset
        if (data.length == 0) {
            if (this.decoder != null) {
                this.decoder.resetState();
            }
            return ZERO_SHORT_ARRAY;
        }

        // lazy-load opus decoder
        if (this.decoder == null) {
            this.decoder = this.loader.new Decoder(SAMPLE_RATE, CHANNELS);
            this.decoder.setFrameSize(FRAME_SIZE);
        }
        return this.decoder.decode(data);
    }

    public byte[] encode(short[] pcm) {
        // 0 length data means state reset
        if (pcm.length == 0) {
            if (this.encoder != null) {
                this.encoder.resetState();
            }
            return ZERO_BYTE_ARRAY;
        }

        // lazy-load opus encoder
        if (this.encoder == null) {
            this.encoder = this.loader.new Encoder(SAMPLE_RATE, CHANNELS, this.mode);
            this.encoder.setMaxPayloadSize(this.mtu.getAsInt());
            this.encoder.setMaxPacketLossPercentage(0.05f);
        }
        return this.encoder.encode(pcm);
    }

    @Override
    public void close() {
        try (Decoder ignoredDecoder = this.decoder;
             Encoder ignoredEncoder = this.encoder) {
            // NO-OP
        }
    }

    public enum Mode {

        VOICE,
        AUDIO,
        LOW_DELAY,
    }
}
