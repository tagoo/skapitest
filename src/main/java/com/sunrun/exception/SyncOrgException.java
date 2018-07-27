package com.sunrun.exception;

public class SyncOrgException extends Exception {

    private static final long serialVersionUID = -2846310998586953493L;

    public SyncOrgException(Throwable cause) {
        super(cause);
    }

    public SyncOrgException(String message) {
        super(message);
    }

    public SyncOrgException(String message, Exception e) {
    }
}
