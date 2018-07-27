package com.sunrun.exception;

public class NotFindPropertyException extends Exception {
    private static final long serialVersionUID = -39770269948899414L;

    public NotFindPropertyException() {
    }

    public NotFindPropertyException(String message) {
        super(message);
    }
}
