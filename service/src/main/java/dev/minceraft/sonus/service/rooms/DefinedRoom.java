package dev.minceraft.sonus.service.rooms;

import dev.minceraft.sonus.service.IAudioSource;
import dev.minceraft.sonus.service.audio.SonusAudio;
import dev.minceraft.sonus.service.data.ISonusPlayer;
import dev.minceraft.sonus.service.rooms.options.RoomDefinition;
import dev.minceraft.sonus.service.SonusService;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class DefinedRoom extends AbstractRoom {

    private RoomDefinition definition;

    public DefinedRoom(SonusService service, UUID roomId, RoomDefinition definition) {
        super(service, roomId);
        this.definition = definition;
    }

    @Override
    protected void sendAudio0(IAudioSource source, SonusAudio audio) {
        for (ISonusPlayer receiver : this.members.values()) {
            if (receiver.getSenderId(receiver).equals(source.getSenderId(receiver))) {
                continue;
            }
            RoomDefinition.RelationState state = this.definition.getState(source, receiver);
            switch (state) {
                case STATIC -> receiver.sendStaticAudio(source, audio.copy());
                case SPATIAL -> receiver.sendSpatialAudio(source, audio.copy());
                case SPATIAL_NORMALIZED -> receiver.sendSpatialNormedAudio(source, audio.copy());
            }
        }
    }

    @Override
    protected void sendAudioEnd0(IAudioSource source, long sequence) {
        for (ISonusPlayer receiver : this.members.values()) {
            if (receiver.getSenderId(receiver).equals(source.getSenderId(receiver))) {
                continue;
            }
            RoomDefinition.RelationState state = this.definition.getState(source, receiver);
            switch (state) {
                case STATIC -> receiver.sendStaticAudioEnd(source, sequence);
                case SPATIAL, SPATIAL_NORMALIZED -> receiver.sendSpatialAudioEnd(source, sequence);
            }
        }
    }

    public void updateDefinition(RoomDefinition definition) {
        this.definition = definition;
    }
}
