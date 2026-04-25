package dev.minceraft.sonus.common.util;
// Created by booky10 in TjcSonus (19:40 17.11.2024)

import dev.minceraft.sonus.common.natives.LameNativesLoader;
import dev.minceraft.sonus.common.natives.LameNativesLoader.Decoder;
import org.jspecify.annotations.NullMarked;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static dev.minceraft.sonus.common.SonusConstants.SAMPLE_RATE;

// partially inspired by https://github.com/henkelmax/audio-player/blob/e1efb808c4e9c9a023e3029c410654109e55ba16/src/main/java/de/maxhenkel/audioplayer/utils/AudioUtils.java
@NullMarked
public final class AudioConversionUtil {

    public static AudioFormat SONUS_FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
            SAMPLE_RATE, Short.SIZE, 1, 2, SAMPLE_RATE, false);

    private AudioConversionUtil() {
    }

    public static short[] addStartEndFade(short[] audio, float seconds) {
        float samples = SAMPLE_RATE * seconds;
        for (int i = 0, len = audio.length; i < samples; i++) {
            float volume = (float) i / samples;
            audio[i] = (short) (audio[i] * volume);
            audio[len - i - 1] = (short) (audio[len - i - 1] * volume);
        }
        return audio;
    }

    public static short[] adjustVolumeLerp(short[] audioSamples, float volumeMin, float volumeMax) {
        if (volumeMin == volumeMax) {
            return adjustVolume(audioSamples, volumeMin);
        }
        for (int i = 0, len = audioSamples.length; i < len; ++i) {
            float progress = i / (float) audioSamples.length;
            float volume = Math.fma(volumeMax - volumeMin, progress, volumeMin); // lerp
            audioSamples[i] = (short) (audioSamples[i] * volume);
        }
        return audioSamples;
    }

    public static short[] adjustVolume(short[] audioSamples, float volume) {
        if (volume == 1f) {
            return audioSamples;
        }
        for (int i = 0, len = audioSamples.length; i < len; ++i) {
            audioSamples[i] = (short) (audioSamples[i] * volume);
        }
        return audioSamples;
    }

    public static byte[] adjustVolume(byte[] audioSamples, float volume) {
        if (volume == 1f) {
            return audioSamples;
        }
        for (int i = 0; i < audioSamples.length; i += 2) {
            short buf1 = audioSamples[i + 1];
            short buf2 = audioSamples[i];

            buf1 = (short) ((buf1 & 0xFF) << 8);
            buf2 = (short) (buf2 & 0xFF);

            short res = (short) (buf1 | buf2);
            res = (short) (res * volume);

            audioSamples[i] = (byte) res;
            audioSamples[i + 1] = (byte) (res >> 8);
        }
        return audioSamples;
    }

    public static short[] convertToSonus(short[] data, float volume, AudioFormat format) {
        ShortArrayInputStream arrayInput = new ShortArrayInputStream(data);
        AudioInputStream source = new AudioInputStream(arrayInput, format,
                data.length / format.getFrameSize());
        return convertToSonus(source, volume);
    }

    public static short[] convertToSonus(AudioInputStream source, float volume) {
        AudioFormat sourceFormat = source.getFormat();
        AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                sourceFormat.getSampleRate(), Short.SIZE, sourceFormat.getChannels(),
                sourceFormat.getChannels() * 2,
                sourceFormat.getSampleRate(), false);
        AudioInputStream stream1 = AudioSystem.getAudioInputStream(convertFormat, source);
        AudioInputStream stream2 = AudioSystem.getAudioInputStream(SONUS_FORMAT, stream1);
        try {
            return bytesToShorts(adjustVolume(stream2.readAllBytes(), volume));
        } catch (IOException exception) {
            throw new RuntimeException("Error while reading audio stream", exception);
        }
    }

    public static short[] bytesToShorts(byte[] bytes) {
        if (bytes.length % 2 != 0) {
            throw new IllegalArgumentException("Input bytes need to be divisible by 2");
        }
        short[] data = new short[bytes.length / 2];
        for (int i = 0, len = bytes.length; i < len; i += 2) {
            byte b1 = bytes[i], b2 = bytes[i + 1]; // LE order
            data[i / 2] = (short) (((b2 & 0xFF) << 8) | (b1 & 0xFF));
        }
        return data;
    }

    public static byte[] shortsToBytes(short[] shorts) {
        byte[] data = new byte[shorts.length * 2];
        for (int i = 0, len = shorts.length; i < len; ++i) {
            short s = shorts[i];
            // LE order
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    public static short[] decodeMp3ToSonus(LameNativesLoader natives, Path path, float volume) throws IOException {
        int size = (int) Files.size(path);
        try (InputStream input = Files.newInputStream(path)) {
            return decodeMp3ToSonus(natives, size, input, volume);
        }
    }

    public static short[] decodeMp3ToSonus(LameNativesLoader natives, InputStream input, float volume) throws IOException {
        return decodeMp3ToSonus(natives, input.available(), input, volume);
    }

    public static short[] decodeMp3ToSonus(LameNativesLoader natives, int fileSize, InputStream input, float volume) throws IOException {
        try (BufferedInputStream bufferedInput = new BufferedInputStream(input);
             Decoder decoder = natives.new Decoder(bufferedInput)) {
            return decodeMp3ToSonus(fileSize, decoder, volume);
        }
    }

    public static short[] decodeMp3ToSonus(Decoder decoder, float volume) {
        return decodeMp3ToSonus(0, decoder, volume);
    }

    private static final int INITIAL_SAMPLE_COUNT = 2048;

    // inspired by https://github.com/henkelmax/simple-voice-chat/blob/20218b8d4169ec2af56c34a0aa07c2ee711a01e1/common/src/main/java/de/maxhenkel/voicechat/plugins/impl/mp3/Mp3DecoderImpl.java
    public static short[] decodeMp3ToSonus(int fileSize, Decoder decoder, float volume) {
        short[] firstFrame = decoder.decodeNextFrame();
        if (firstFrame == null) {
            throw new IllegalArgumentException("Failed to decode first mp3 frame from " + decoder);
        }
        // seems fine for some audios I tested, pre-allocates a bit more space
        int estimatedSampleCount = fileSize > 0 ? (int) (fileSize / 9500d * decoder.getSampleRate()) : INITIAL_SAMPLE_COUNT;
        ShortArrayBuffer buffer = new ShortArrayBuffer(estimatedSampleCount);
        buffer.writeShorts(firstFrame);
        while (true) {
            short[] samples = decoder.decodeNextFrame();
            if (samples == null) {
                break;
            }
            buffer.writeShorts(samples);
        }
        AudioFormat audioFormat = decoder.createAudioFormat();
        assert audioFormat != null; // we have parsed the InputStream, this shouldn't be null
        // convert decoded mp3 pcm data to sonus format
        return convertToSonus(buffer.getBuf(), volume, audioFormat);
    }
}
