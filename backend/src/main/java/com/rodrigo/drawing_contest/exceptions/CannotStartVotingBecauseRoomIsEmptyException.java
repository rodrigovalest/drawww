package com.rodrigo.drawing_contest.exceptions;

public class CannotStartVotingBecauseRoomIsEmptyException extends RuntimeException {
    public CannotStartVotingBecauseRoomIsEmptyException(String message) {
        super(message);
    }
}
