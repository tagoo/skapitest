package com.sunrun.controller;

import com.mysql.jdbc.StringUtils;
import com.sunrun.common.notice.NoticeFactory;
import com.sunrun.common.notice.NoticeMessage;
import com.sunrun.common.notice.ReturnCode;
import com.sunrun.common.notice.ReturnData;
import com.sunrun.entity.Roster;
import com.sunrun.entity.User;
import com.sunrun.exception.IamConnectionException;
import com.sunrun.exception.NotFindUserException;
import com.sunrun.service.RosterService;
import com.sunrun.service.UserService;
import com.sunrun.utils.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private RosterService rosterService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ReturnData login(User user, @RequestParam(name = "lang", defaultValue = "zh") String lang, @RequestParam(name = "st") String st, HttpServletRequest request) {
        NoticeMessage noticeMessage = null;
        String ip = IpUtil.getIpAddrAdvanced(request);
        logger.info("ip=" + ip);
        User data = null;
        if (StringUtils.isEmptyOrWhitespaceOnly(user.getUserName()) || StringUtils.isEmptyOrWhitespaceOnly(user.getUserPassword())) {
            noticeMessage = NoticeMessage.USERNAME_OR_PASSWORD_IS_NULL;
        } else {
            try {
                data = userService.loginByUser(user,st);
                if (data == null) {
                    noticeMessage = NoticeMessage.ACCOUNT_AUTHENTICATE_FAILED;
                } else {
                    noticeMessage = NoticeMessage.SUCCESS;
                }
            } catch (NotFindUserException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.USERNAME_OR_PASSWORD_ERROR;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, data);
    }

    @RequestMapping("remove")
    public ReturnCode remove(User user) {
        userService.remove(user);
        return NoticeFactory.createNotice(NoticeMessage.SUCCESS, "zh");
    }

    @GetMapping("friends/{userName}")
    public ReturnData getFriendList(@PathVariable String userName,@RequestParam(name = "lang", defaultValue = "zh")String lang){
        NoticeMessage noticeMessage = NoticeMessage.SUCCESS;
        List<Roster> data = null;
        try {
            data = userService.getFriendList(userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, data);
    }

    @RequestMapping("synchronization")
    public ReturnCode updateUser(@RequestParam(name = "lang", defaultValue = "zh")String lang) {
        try {
            userService.updateUserList();
        } catch (IamConnectionException e) {
            e.printStackTrace();
        }
        return NoticeFactory.createNotice(NoticeMessage.SUCCESS, lang);
    }
}
