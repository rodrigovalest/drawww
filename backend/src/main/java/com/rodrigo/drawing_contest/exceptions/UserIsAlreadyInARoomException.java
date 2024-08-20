package com.rodrigo.drawing_contest.exceptions;

public class UserIsAlreadyInARoomException extends RuntimeException {
    public UserIsAlreadyInARoomException(String message) {
        super(message);
    }
}
