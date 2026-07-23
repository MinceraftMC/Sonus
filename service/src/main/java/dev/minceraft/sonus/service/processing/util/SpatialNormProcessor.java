package dev.minceraft.sonus.service.processing.util;
// Created by booky10 in Sonus (03:56 16.11.2025)

import dev.minceraft.sonus.service.IAudioSource;
import dev.minceraft.sonus.service.audio.SonusAudio;
import dev.minceraft.sonus.service.data.ISonusPlayer;
import dev.minceraft.sonus.common.data.Vec3d;
import dev.minceraft.sonus.service.SonusService;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class SpatialNormProcessor {

    private static final double LN_MIN_DISTANCE = Math.log(10d); // arbitrary value, seems fine when looking at f(x)=((ln(25)-ln(x))/(ln(25)-ln(10)))

    private SpatialNormProcessor() {
    }

    public static @Nullable Vec3d normalizeAudio(SonusService service, ISonusPlayer player, IAudioSource source, SonusAudio audio) {
        Vec3d listenerPos = player.getPosition();
        Vec3d sourcePos = source.getPosition();
        if (listenerPos == null || sourcePos == null) {
            return null; // no position set, don't know where to play, don't send audio
        }
        double distance = Math.sqrt(listenerPos.distanceSquared(sourcePos));
        double maxDistance = service.getConfig().getVoiceChatRange();
        if (distance >= maxDistance) {
            return null; // out of range, don't even try to calculate
        }

        // scale audio gain logarithmically by distance
        double logMaxDistance = Math.log(maxDistance);
        float gain = Math.clamp((float) ((logMaxDistance - Math.log(distance)) / (logMaxDistance - LN_MIN_DISTANCE)), 0f, 1f);
        if (gain == 0f) {
            return null; // don't send audio
        } else if (gain != 1f) {
            short[] pcm = audio.pcm();
            for (int i = 0, len = pcm.length; i < len; i++) {
                pcm[i] = (short) (pcm[i] * gain);
            }
        }

        // calculate normalized sound source position
        return new Vec3d(
                listenerPos.getX() + (sourcePos.getX() - listenerPos.getX()) / distance,
                listenerPos.getY() + (sourcePos.getY() - listenerPos.getY()) / distance,
                listenerPos.getZ() + (sourcePos.getZ() - listenerPos.getZ()) / distance
        );
    }
}
