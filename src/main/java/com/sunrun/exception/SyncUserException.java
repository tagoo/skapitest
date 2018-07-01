package com.sunrun.exception;

public class SyncUserException extends RuntimeException {
    public SyncUserException(IamConnectionException e) {
    }

    public SyncUserException(String message, IamConnectionException e) {
    }
}
