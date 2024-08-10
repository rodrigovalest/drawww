package com.rodrigo.drawing_contest.exceptions;

public class UserPasswordDoNotMatchException extends RuntimeException {
    public UserPasswordDoNotMatchException(String message) {
        super(message);
    }
}
