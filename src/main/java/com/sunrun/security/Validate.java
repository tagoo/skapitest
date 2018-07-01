package com.sunrun.security;

import com.sunrun.entity.User;
import com.sunrun.exception.IamConnectionException;

public interface Validate {
    boolean accessLogin(User user, String serviceTicket) throws IamConnectionException;
}
