package dev.minceraft.sonus.service.api.participant;

import dev.minceraft.sonus.api.service.ISonusServicePlayer;
import dev.minceraft.sonus.api.service.participant.ISonusParticipant;
import dev.minceraft.sonus.service.api.ApiDelegation;
import dev.minceraft.sonus.service.api.ApiSonusPlayer;
import dev.minceraft.sonus.service.participant.IAudioParticipant;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class ApiSonusParticipant<T extends IAudioParticipant> extends ApiDelegation<T> implements ISonusParticipant {

    public ApiSonusParticipant(T delegate) {
        super(delegate);
    }

    @Override
    public UUID getUniqueId(@Nullable ISonusServicePlayer viewer) {
        return this.delegate.getUniqueId(viewer == null ? null : ((ApiSonusPlayer) viewer).getDelegate());
    }
}
