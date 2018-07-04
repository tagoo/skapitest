package com.sunrun.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunrun.common.notice.NoticeFactory;
import com.sunrun.common.notice.NoticeMessage;
import com.sunrun.common.notice.ReturnCode;
import com.sunrun.common.notice.ReturnData;
import com.sunrun.entity.MucRoomMember;
import com.sunrun.entity.Roster;
import com.sunrun.entity.User;
import com.sunrun.exception.IamConnectionException;
import com.sunrun.exception.NameAlreadyExistException;
import com.sunrun.exception.NotFindUserException;
import com.sunrun.service.RosterService;
import com.sunrun.service.UserService;
import com.sunrun.utils.IpUtil;
import com.sunrun.utils.helper.Property;
import com.sunrun.utils.helper.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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
        if (!StringUtils.hasText(user.getUserName()) || !StringUtils.hasText(user.getUserPassword())) {
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

    @RequestMapping("{userName}")
    public ReturnData getUser(@RequestParam(name = "lang", defaultValue = "zh")String lang,
                              @PathVariable(name = "userName")String userName){
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        UserData user = null;
        try {
            user = userService.getUser(userName);
            if (user != null) {
                noticeMessage =NoticeMessage.SUCCESS;
            } else {
                noticeMessage =NoticeMessage.USER_NOT_EXIST;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, user);
    }

    @PostMapping("save")
    public ReturnData createUser(UserData userData, @RequestParam(name = "lang", defaultValue = "zh") String lang,
                                 @RequestParam(name = "property", required = false) String property) {
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        UserData user = null;
        if (!StringUtils.hasText(userData.getUsername()) || !StringUtils.hasText(userData.getPassword())) {
            noticeMessage = NoticeMessage.USERNAME_OR_PASSWORD_IS_NULL;
        } else {
            if (StringUtils.hasText(property)) {
                Gson gson = new Gson();
                userData.setProperties(gson.fromJson(property, new TypeToken<List<Property>>() {
                }.getType()));
            }
            try {
                user = userService.createUser(userData);
                if (user != null) {
                    noticeMessage = NoticeMessage.SUCCESS;
                }
            } catch (NameAlreadyExistException e) {
                noticeMessage = NoticeMessage.USERNAME_ALREADY_EXIST;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, user);
    }

    @PostMapping("update")
    public ReturnData updateUser(UserData userData, @RequestParam(name = "lang", defaultValue = "zh") String lang,
                                 @RequestParam(name = "property", required = false) String property) {
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        UserData user = null;
        if (!StringUtils.hasText(userData.getUsername()) /*|| !StringUtils.hasText(userData.getPassword())*/) {
            noticeMessage = NoticeMessage.USERNAME_OR_PASSWORD_IS_NULL;
        } else {
            if (StringUtils.hasText(property)) {
                Gson gson = new Gson();
                userData.setProperties(gson.fromJson(property, new TypeToken<List<Property>>() {
                }.getType()));
            }
            try {
                if (userService.updateUser(userData)) {
                    noticeMessage = NoticeMessage.SUCCESS;
                }
            } catch (NotFindUserException e) {
                noticeMessage = NoticeMessage.USER_NOT_EXIST;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, user);
    }

    @RequestMapping("delete/{userName}")
    public ReturnData deleteUser(@RequestParam(name = "lang", defaultValue = "zh")String lang,
                              @PathVariable(name = "userName")String userName){
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        try {
            if(StringUtils.hasText(userName)) {
                if (userService.delete(userName)) {
                    noticeMessage = NoticeMessage.SUCCESS;
                }
            } else {
                noticeMessage = NoticeMessage.USERNAME_IS_NULL;
            }
        } catch (Exception e) {
            noticeMessage = NoticeMessage.USER_NOT_EXIST;
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }


}
