package com.rodrigo.drawing_contest.exceptions;

public class RoomPasswordDontMatchException extends RuntimeException {
    public RoomPasswordDontMatchException(String message) {
        super(message);
    }
}
