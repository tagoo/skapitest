package com.sunrun.security;

import com.sunrun.exception.IamConnectionException;


import javax.servlet.http.HttpSession;
import java.util.Map;

public interface Validate {
    Map<String,Object> accessLogin(HttpSession session,String serviceTicket) throws IamConnectionException;
}
