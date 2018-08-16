package com.sunrun.common.filter;

import com.google.gson.Gson;
import com.sunrun.common.notice.NoticeFactory;
import com.sunrun.common.notice.NoticeMessage;
import com.sunrun.common.notice.ReturnCode;
import com.sunrun.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


public class LoginInterceptor implements HandlerInterceptor {
    private static final String CURRENT_USER = "currentUser";
    private static final String LANG = "lang";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object attr = request.getSession().getAttribute(CURRENT_USER);
        Object langStr = request.getSession().getAttribute(LANG);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        if (attr != null && attr instanceof User) {
            return true;
        } else  {
            PrintWriter writer = response.getWriter();
            String lang = langStr == null ? "zh" : langStr.toString();
            ReturnCode notice = NoticeFactory.createNotice(NoticeMessage.USER_NOT_LOGIN, lang);
            Gson gson = new Gson();
            writer.write(gson.toJson(notice));
            writer.close();
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
