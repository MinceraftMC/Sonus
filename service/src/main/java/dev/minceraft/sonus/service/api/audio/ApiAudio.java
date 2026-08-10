package dev.minceraft.sonus.service.api.audio;

import dev.minceraft.sonus.api.service.audio.ISonusAudio;
import dev.minceraft.sonus.common.audio.SonusAudio;
import dev.minceraft.sonus.service.api.ApiDelegation;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ApiAudio extends ApiDelegation<SonusAudio> implements ISonusAudio {

    public ApiAudio(SonusAudio delegate) {
        super(delegate);
    }

    @Override
    public long getSequenceNumber() {
        return this.delegate.getSequenceNumber();
    }

    @Override
    public short @Nullable [] getPcm() {
        return this.delegate.pcm();
    }

    @Override
    public byte @Nullable [] getOpus() {
        return this.delegate.opus();
    }

    @Override
    public ISonusAudio copy() {
        return new ApiAudio(this.delegate.copy());
    }
}
