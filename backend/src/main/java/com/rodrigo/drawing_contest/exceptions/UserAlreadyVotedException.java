package com.rodrigo.drawing_contest.exceptions;

public class UserAlreadyVotedException extends RuntimeException {
    public UserAlreadyVotedException(String message) {
        super(message);
    }
}
