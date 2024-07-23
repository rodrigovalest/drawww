package com.rodrigo.drawing_contest.exceptions;

public class UserIsNotInAnyRoomException extends RuntimeException {
    public UserIsNotInAnyRoomException(String message) {
        super(message);
    }
}
