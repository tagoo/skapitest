package com.sunrun.security;

public class ValidateFactory {
    public static Validate createValidate() {
        return new IamOperate();
    }
}
