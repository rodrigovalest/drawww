package com.rodrigo.drawing_contest.events;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
@Getter
public class UserInactivityEvent extends ApplicationEvent {

    private final String username;

    public UserInactivityEvent(Object source, String username) {
        super(source);
        this.username = username;
    }
}
