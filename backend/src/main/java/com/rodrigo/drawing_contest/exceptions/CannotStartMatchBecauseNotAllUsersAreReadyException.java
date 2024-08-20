package com.rodrigo.drawing_contest.exceptions;

public class CannotStartMatchBecauseNotAllUsersAreReadyException extends RuntimeException {
    public CannotStartMatchBecauseNotAllUsersAreReadyException(String message) {
        super(message);
    }
}
