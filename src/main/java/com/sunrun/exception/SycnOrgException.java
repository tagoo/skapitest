package com.sunrun.exception;

public class SycnOrgException extends RuntimeException {

    public SycnOrgException(IamConnectionException e) {
    }

    public SycnOrgException(String message, IamConnectionException e) {
    }
}
