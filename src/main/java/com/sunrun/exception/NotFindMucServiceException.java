package com.sunrun.exception;

public class NotFindMucServiceException extends Exception {
    private static final long serialVersionUID = -3777222069595167773L;

    public NotFindMucServiceException() {
    }

    public NotFindMucServiceException(String message) {
        super(message);
    }
}
