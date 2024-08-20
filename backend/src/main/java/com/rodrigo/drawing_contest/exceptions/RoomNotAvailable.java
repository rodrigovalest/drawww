package com.rodrigo.drawing_contest.exceptions;

public class RoomNotAvailable extends RuntimeException {
    public RoomNotAvailable(String message) {
        super(message);
    }
}
