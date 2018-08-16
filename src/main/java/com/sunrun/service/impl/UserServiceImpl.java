package com.sunrun.service.impl;

import com.sunrun.common.config.IamConfig;
import com.sunrun.common.config.OfConfig;
import com.sunrun.dao.MucRoomMemberRepository;
import com.sunrun.dao.RosterRepository;
import com.sunrun.entity.*;
import com.sunrun.exception.*;
import com.sunrun.dao.UserRepository;
import com.sunrun.security.Operate;
import com.sunrun.service.MucRoomService;
import com.sunrun.service.UserService;
import com.sunrun.utils.RestApiUtil;
import com.sunrun.utils.XmppConnectionUtil;
import com.sunrun.utils.helper.UserData;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
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
    @Autowired
    private OfConfig ofConfig;
    @PersistenceContext
    private EntityManager entityManager;
    @Resource
    private RestApiUtil restApiUtil;
    @Autowired
    private RedisTemplate redisTemplate;
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

    @Override
    public User loginByUser(User user, HttpSession session) throws NotFindUserException {
        User u = userRepository.findUserByUserNameAndUserPassword(user.getUserName(), DigestUtils.sha1Hex(user.getUserPassword().getBytes()));
        if (null == u) {
            throw new NotFindUserException();
        }
        session.setAttribute("currentUser",u);
        u.setUserPassword(null);
        return u;
        //todo 是否需要去openfire登录
       /* XmppConnection xmppConnection = new XmppConnection(ofConfig);
        boolean login = xmppConnection.login(user.getUserName(), user.getUserPassword());
        AbstractXMPPConnection connection = xmppConnection.getConnection();
        xmppConnection.setPresence(4);
        if (login) {
            session.setAttribute("currentUser",u);
            return u;
        }*/

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
    public boolean updateUserList() throws IamConnectionException, NotFindMucServiceException, SyncOrgException, CannotFindDomain, GetUserException, SyncAlreadyRunningException {
        return  operate.synchronizeData();
    }

    @Override
    @Cacheable(value ="user",key = "#userName")
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

    @Override
    public Page<User> getUserListByDomainId(Integer domainId, Long orgId, String search, Pageable pageable) {
        return userRepository.findAll((root,cq,cb) -> {
            List<Predicate> list = new ArrayList<>();
            Join<User, Org> org = root.join(root.getModel().getSingularAttribute("org", Org.class), JoinType.LEFT);
            if (StringUtils.hasText(search)) {
                    list.add(cb.or(cb.like(root.get("userName").as(String.class), "%" + search + "%"), cb.like(root.get("userRealName").as(String.class), "%" + search + "%")));
                }
            if (null != orgId) {
                list.add(cb.equal(org.get("orgId").as(Long.class),orgId));
            } else if(null != domainId) {
                list.add(cb.equal(root.get("domainId").as(Long.class),domainId));
            }
            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
            },pageable);
    }

    @Override
    public Page<User> getUserListByOrgId(Long orgId, Pageable pageable) {
        return userRepository.findByOrgId(orgId,pageable);
    }

    @Override
    public List<User> getUserListByDomainId(Integer domainId, String search) {
        if (search == null) {
            return userRepository.findByDomainId(domainId);
        } else {
            return userRepository.findByDomainIdAndCondition(domainId,search);
        }
    }
}
