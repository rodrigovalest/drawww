package com.rodrigo.drawing_contest.exceptions;

public class ActionDoNotMatchWithRoomStatusException extends RuntimeException {
    public ActionDoNotMatchWithRoomStatusException(String message) {
        super(message);
    }
}
