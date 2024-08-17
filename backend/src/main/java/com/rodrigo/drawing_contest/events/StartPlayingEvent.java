package com.rodrigo.drawing_contest.events;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@ToString
@Getter
public class StartPlayingEvent extends ApplicationEvent {

    private UUID roomId;

    public StartPlayingEvent(Object source, UUID roomId) {
        super(source);
        this.roomId = roomId;
    }
}
