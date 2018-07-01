package com.sunrun.service.impl;

import com.sunrun.common.config.IamConfig;
import com.sunrun.dao.RosterRepository;
import com.sunrun.entity.Roster;
import com.sunrun.entity.User;
import com.sunrun.exception.IamConnectionException;
import com.sunrun.exception.NotFindUserException;
import com.sunrun.dao.UserRepository;
import com.sunrun.security.Operate;
import com.sunrun.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RosterRepository rosterRepository;
    @Autowired
    private IamConfig iamConfig;
    @Autowired
    private Operate operate;
    @Override
    @Cacheable(value ="user",key = "#user.userName")
    @Transactional
    public User loginByUser(User user, String serviceTicket) throws NotFindUserException {
        User u = userRepository.findUserByUserNameAndUserPassword(user.getUserName(), user.getUserPassword());
        if (null == u) {
            throw new NotFindUserException();
        }
        try {
            if (operate.accessLogin(u,serviceTicket)) {
                logger.info("Login authentication successful from Iam,username:" + user.getUserName());
                return u;
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
    public boolean updateUserList() throws IamConnectionException{
        return  operate.synchronizeData();
    }

}
