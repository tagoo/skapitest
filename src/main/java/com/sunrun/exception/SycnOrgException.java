package com.sunrun.exception;

public class SycnOrgException extends Exception {

    private static final long serialVersionUID = -2846310998586953493L;

    public SycnOrgException(Throwable cause) {
        super(cause);
    }

    public SycnOrgException(String message) {
        super(message);
    }

    public SycnOrgException(String message, Exception e) {
    }
}
