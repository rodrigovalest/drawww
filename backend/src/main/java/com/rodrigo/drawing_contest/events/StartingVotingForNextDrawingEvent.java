package com.rodrigo.drawing_contest.events;

import com.rodrigo.drawing_contest.models.room.Room;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
@Getter
public class StartingVotingForNextDrawingEvent extends ApplicationEvent {

    private Room room;

    public StartingVotingForNextDrawingEvent(Object source, Room room) {
        super(source);
        this.room = room;
    }
}
