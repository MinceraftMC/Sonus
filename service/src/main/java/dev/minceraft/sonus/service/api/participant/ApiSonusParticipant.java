package dev.minceraft.sonus.service.api.participant;

import dev.minceraft.sonus.api.service.participant.ISonusParticipant;
import dev.minceraft.sonus.api.service.participant.builtin.ISonusServicePlayer;
import dev.minceraft.sonus.common.participant.IAudioParticipant;
import dev.minceraft.sonus.common.participant.builtin.ISonusPlayer;
import dev.minceraft.sonus.service.api.ApiDelegation;
import dev.minceraft.sonus.service.api.participant.builtin.ApiBasicSource;
import dev.minceraft.sonus.service.api.participant.builtin.ApiSonusPlayer;
import dev.minceraft.sonus.service.participant.BasicAudioSource;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public class ApiSonusParticipant<T extends IAudioParticipant> extends ApiDelegation<T> implements ISonusParticipant {

    public ApiSonusParticipant(T delegate) {
        super(delegate);
    }

    public static <T extends ISonusParticipant> T toApi(IAudioParticipant serviceParticipant) {
        return (T) switch (serviceParticipant) {
            case ISonusPlayer player -> new ApiSonusPlayer(player);
            case BasicAudioSource basicSource -> new ApiBasicSource(basicSource);
            default -> throw new IllegalArgumentException("Unknown participant type: " + serviceParticipant.getClass().getName());
        };
    }

    public static IAudioParticipant toService(ISonusParticipant apiParticipant) {
        return switch (apiParticipant) {
            case ApiSonusPlayer player -> player.getDelegate();
            case ApiBasicSource basicSource -> basicSource.getDelegate();
            default -> throw new IllegalArgumentException("Unknown participant type: " + apiParticipant.getClass().getName());
        };
    }

    @Override
    public UUID getUniqueId(@Nullable ISonusServicePlayer viewer) {
        return this.delegate.getUniqueId(viewer == null ? null : ((ApiSonusPlayer) viewer).getDelegate());
    }
}
