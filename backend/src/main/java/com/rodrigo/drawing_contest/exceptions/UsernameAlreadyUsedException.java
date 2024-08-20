package com.rodrigo.drawing_contest.exceptions;

public class UsernameAlreadyUsedException extends RuntimeException {
    public UsernameAlreadyUsedException(String message) {
        super(message);
    }
}
