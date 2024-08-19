package com.rodrigo.drawing_contest.exceptions;

public class UserIsNotInThisRoomException extends RuntimeException {
    public UserIsNotInThisRoomException(String message) {
        super(message);
    }
}
