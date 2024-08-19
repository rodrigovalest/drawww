package com.rodrigo.drawing_contest.exceptions;

public class UserNotUpForVoteException extends RuntimeException {
    public UserNotUpForVoteException(String message) {
        super(message);
    }
}
