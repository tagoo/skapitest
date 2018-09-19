package com.sunrun.exception;

public class SyncOrgException extends Exception {


    public SyncOrgException(Throwable cause) {
        super(cause);
    }

    public SyncOrgException(String message) {
        super(message);
    }

    public SyncOrgException(String message, Exception e) {
    }
}
