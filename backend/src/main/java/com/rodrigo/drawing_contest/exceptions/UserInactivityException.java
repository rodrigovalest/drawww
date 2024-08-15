package com.rodrigo.drawing_contest.exceptions;

import com.rodrigo.drawing_contest.events.UserInactivityEvent;
import lombok.Getter;

@Getter
public class UserInactivityException extends RuntimeException {

    private UserInactivityEvent event;

    public UserInactivityException(String message, UserInactivityEvent event) {
        super(message);
        this.event = event;
    }
}
