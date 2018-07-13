package com.sunrun.service.impl;

import com.sunrun.common.config.IamConfig;
import com.sunrun.common.notice.ReturnData;
import com.sunrun.dao.MucRoomMemberRepository;
import com.sunrun.dao.RosterRepository;
import com.sunrun.entity.*;
import com.sunrun.exception.IamConnectionException;
import com.sunrun.exception.NameAlreadyExistException;
import com.sunrun.exception.NotFindUserException;
import com.sunrun.dao.UserRepository;
import com.sunrun.exception.OpenfireLoginFailureException;
import com.sunrun.security.Operate;
import com.sunrun.service.MucRoomService;
import com.sunrun.service.UserService;
import com.sunrun.utils.RestApiUtil;
import com.sunrun.utils.XmppConnectionUtil;
import com.sunrun.utils.helper.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RosterRepository rosterRepository;
    @Autowired
    private MucRoomMemberRepository mucRoomMemberRepository;
    @Autowired
    private MucRoomService mucRoomService;
    @Autowired
    private IamConfig iamConfig;
    @Autowired
    private Operate operate;
    @PersistenceContext
    private EntityManager entityManager;
    @Resource
    private RestApiUtil restApiUtil;
    @Override
    @Cacheable(value ="user",key = "#user.userName")
    @Transactional
    public User loginByUser(User user, String serviceTicket) throws NotFindUserException, OpenfireLoginFailureException {
        User u = userRepository.findUserByUserNameAndUserPassword(user.getUserName(), user.getUserPassword());
        if (null == u) {
            throw new NotFindUserException();
        }
        try {
            if (operate.accessLogin(u,serviceTicket)) {
                logger.info("Login authentication successful from Iam,username:" + user.getUserName());
                return u;
            }
            //openfire 登录
            XmppConnectionUtil instance = XmppConnectionUtil.getInstance();
            if (instance.login(user.getUserName(),user.getUserPassword())) {
                List<MucRoom> chatRooms = mucRoomService.findChatRoomsByUserName(user.getUserName() + "@" + instance.getConnection().getServiceName().toString(), null);
                boolean flag = false;
                for (MucRoom room: chatRooms) {
                    flag = instance.joinChatRoom(user.getUserName(), room.getName(), null);
                    if (!flag) {
                        instance.disconnectAccout();
                        break;
                    }
                }
            } else {
                throw new OpenfireLoginFailureException();
            }

            return null;
        } catch (IamConnectionException e) {
           throw new RuntimeException(e);
        }
    }

    @CachePut(value = "user",key = "#user.id")
    @Override
    public User save(User user) {
        User u = userRepository.save(user);
        return u;
    }

    @CacheEvict(value = "user",key = "#user.userName")
    public int remove(User user){
        System.out.println("移除缓存"+user.getUserName());
        return 0;
    }

    @Override
    @Cacheable(value="roster",key = "#userName")
    public List<Roster> getFriendList(String userName) {
        return rosterRepository.findAllByUsername(userName);
    }

    @Override
    @Transactional
    public ReturnData updateUserList() throws IamConnectionException{
        return  operate.synchronizeData();
    }

    @Override
    public UserData getUser(String userName) {
        return restApiUtil.getUser(userName);
    }

    @Override
    public UserData createUser(UserData userData) throws NameAlreadyExistException {
        UserData user = restApiUtil.getUser(userData.getUsername());
        if (user != null) {
            throw new NameAlreadyExistException();
        }
        if(restApiUtil.creatUser(userData)) {
            return restApiUtil.getUser(userData.getUsername());
        }
        return null;
    }

    @Override
    public boolean updateUser(UserData userData) throws NotFindUserException {
        UserData user = restApiUtil.getUser(userData.getUsername());
        if (user == null) {
            throw new NotFindUserException();
        }
        return restApiUtil.updateUser(userData);
    }

    @Override
    public boolean delete(String userName) throws NotFindUserException {
        UserData user = restApiUtil.getUser(userName);
        if (user == null) {
            throw new NotFindUserException();
        }
        return restApiUtil.deleteUser(userName);
    }


}
