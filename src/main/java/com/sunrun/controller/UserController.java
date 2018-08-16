package com.sunrun.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunrun.common.Constant;
import com.sunrun.common.notice.NoticeFactory;
import com.sunrun.common.notice.NoticeMessage;
import com.sunrun.common.notice.ReturnCode;
import com.sunrun.common.notice.ReturnData;
import com.sunrun.entity.Roster;
import com.sunrun.entity.User;
import com.sunrun.exception.*;
import com.sunrun.po.UserPo;
import com.sunrun.security.Operate;
import com.sunrun.service.RosterService;
import com.sunrun.service.UserService;
import com.sunrun.support.iam.DomainSyncInfo;
import com.sunrun.utils.IpUtil;
import com.sunrun.entity.Property;
import com.sunrun.utils.helper.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private Operate operate;
    @Autowired
    private RosterService rosterService;
    @PostMapping("admin/login")
    public ReturnData adminLogin(User user, @RequestParam(name = "lang", defaultValue = "zh") String lang, HttpServletRequest request, HttpSession session) {
        NoticeMessage noticeMessage = null;
        Map<String,Object> data = null;
        String ip = IpUtil.getIpAddrAdvanced(request);
        logger.info("ip=" + ip);
        if (!StringUtils.hasText(user.getUserName()) || !StringUtils.hasText(user.getUserPassword())) {
            noticeMessage = NoticeMessage.USERNAME_OR_PASSWORD_IS_NULL;
        } else {
            try {
                //AES加密
                User user1 = userService.loginByUser(user, session);
                if (user1 != null) {
                    noticeMessage = NoticeMessage.SUCCESS;
                    data =  new HashMap<>();
                    data.put("user",user1);
                    data.put("token",session.getId());
                }
            } catch (NotFindUserException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.USERNAME_OR_PASSWORD_ERROR;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, data);
    }
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
            } catch (OpenfireLoginFailureException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.OPENFIRE_LOGIN_FAILURE;
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
    public ReturnData updateUser(@RequestParam(name = "lang", defaultValue = "zh")String lang) {
        NoticeMessage noticeMessage = NoticeMessage.SYNCHRONIZATION_FAILURE;
        String message = null;
        Boolean mark = false;
        try {
            mark = redisTemplate.hasKey(Constant.SYNCHRONIZATION_MARK);
            if (mark) {
                noticeMessage = NoticeMessage.SYNCHRONIZATION_RUNNING;
                logger.warn(noticeMessage.getMessage(lang));
            } else {
                redisTemplate.opsForValue().set(Constant.SYNCHRONIZATION_MARK,"true",30, TimeUnit.MINUTES);
                if (!operate.synchronizeData()) {
                    if (operate.deleteDomainResource(DomainSyncInfo.getFailedDomains())){
                    logger.info("Deleted successfully the failed synchronization domain.");
                } else {
                    operate.deleteDomainResource(DomainSyncInfo.getSuccessDomains());
                }
            } else {
                noticeMessage = NoticeMessage.SYNCHRONIZATION_SUCCESS;
            }}
        } catch (IamConnectionException e) {
            noticeMessage = NoticeMessage.CONNECT_IAM_FAILED;
            e.printStackTrace();
        }  catch (NotFindMucServiceException e) {
            message = e.getMessage();
            e.printStackTrace();
        } catch (SyncOrgException e) {
            message = e.getMessage();
            e.printStackTrace();
        } catch (CannotFindDomain e) {
            message = e.getMessage();
            e.printStackTrace();
        } catch (GetUserException e) {
            noticeMessage = NoticeMessage.CONNECT_IAM_FAILED;
            e.printStackTrace();
        } finally {
            if (!mark) {
                redisTemplate.delete(Constant.SYNCHRONIZATION_MARK);
            }
        }
        ReturnData notice = NoticeFactory.createNoticeWithFlag(noticeMessage, lang);
        if (message != null) {
            notice.setMsg(message);
        }
        notice.setSuccess(noticeMessage == NoticeMessage.SYNCHRONIZATION_SUCCESS);
        return notice;
    }

    @GetMapping("{userName}")
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

    @GetMapping("domain/{domainId}")
    public ReturnData getUserList(@RequestParam(name = "lang", defaultValue = "zh")String lang,
                                  @PathVariable(name = "domainId")Integer domainId,
                                  @RequestParam(name = "orgId",required = false)Long orgId,
                                  @RequestParam(name = "page",defaultValue = "0")int page,
                                  @RequestParam(name = "size",defaultValue = "100")int size,
                                  @RequestParam(name = "sort", required = false) String sort,
                                  @RequestParam(name = "search",required = false)String search,
                                  @RequestParam(name = "order",required = false,defaultValue = "asc")String order){
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        Pageable pageable ;
        Page<User> data = null;
        try {
            if (sort != null) {
                Sort sort1 = Sort.by(Sort.Direction.fromString(order),sort);
                pageable = PageRequest.of(page, size,sort1);
            } else {
                pageable = PageRequest.of(page,size);
            }
            data = userService.getUserListByDomainId(domainId,orgId,search,pageable);
            noticeMessage = NoticeMessage.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, data);
    }
    @GetMapping("list/{domainId}")
    public ReturnData getUserListByDomainId(@RequestParam(name = "lang", defaultValue = "zh")String lang,
                                  @PathVariable(name = "domainId")Integer domainId,
                                  @RequestParam(name = "search",required = false)String search){
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        List<User> data  = null;
        try {
            data = userService.getUserListByDomainId(domainId,search);
            noticeMessage = NoticeMessage.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, data);
    }

    @GetMapping("org/{orgId}")
    public ReturnData getUserListByOrgId(@RequestParam(name = "lang", defaultValue = "zh")String lang,
                                         @PathVariable(name = "orgId")Long orgId,
                                         @RequestParam(name = "page",defaultValue = "0")int page,
                                         @RequestParam(name = "size",defaultValue = "100")int size){
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        Page<User> data = null;
        try {
            Pageable pageable = PageRequest.of(page,size);
            data = userService.getUserListByOrgId(orgId,pageable);
            noticeMessage = NoticeMessage.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, data);
    }


    @RequestMapping("logout")
    public ReturnCode logout(@RequestParam(name = "lang", defaultValue = "zh")String lang, HttpSession session){
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        System.out.println("session is new:" + session.isNew());
        try {
            session.invalidate();
            noticeMessage = NoticeMessage.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NoticeFactory.createNotice(noticeMessage, lang);
    }
}
