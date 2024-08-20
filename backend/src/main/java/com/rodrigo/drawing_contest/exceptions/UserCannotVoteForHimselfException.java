package com.rodrigo.drawing_contest.exceptions;

public class UserCannotVoteForHimselfException  extends RuntimeException {
    public UserCannotVoteForHimselfException(String message) {
        super(message);
    }
}
