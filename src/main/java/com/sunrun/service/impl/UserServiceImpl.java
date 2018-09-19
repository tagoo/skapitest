package com.sunrun.service.impl;

import com.sunrun.common.Constant;
import com.sunrun.common.OpenfireSystemProperties;
import com.sunrun.common.config.IamConfig;
import com.sunrun.common.config.OfConfig;
import com.sunrun.dao.DomainRepository;
import com.sunrun.dao.MucRoomMemberRepository;
import com.sunrun.dao.RosterRepository;
import com.sunrun.entity.*;
import com.sunrun.exception.*;
import com.sunrun.dao.UserRepository;
import com.sunrun.security.Operate;
import com.sunrun.service.MucRoomService;
import com.sunrun.service.UserService;
import com.sunrun.support.iam.SystemPropertyInfo;
import com.sunrun.utils.IamUtil;
import com.sunrun.utils.JidGenerator;
import com.sunrun.utils.ObjectUtil;
import com.sunrun.utils.RestApiUtil;
import com.sunrun.utils.helper.UserData;
import com.sunrun.vo.IamValidateRespData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
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
import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

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
    @Autowired
    private DomainRepository domainRepository;
    @Override
    @Transactional
    public User loginByUser(HttpSession session, String serviceTicket) throws NotFindUserException, IamConnectionException {
        Object o = operate.accessLogin(session,serviceTicket).get(Constant.IAM_VALIDATE_RESPONSE_DATA);
        User user = null;
        if (o != null && o instanceof IamValidateRespData) {
            IamValidateRespData respData = (IamValidateRespData)o;
            if (respData.getUser_id() != null) {
                user = userRepository.findBySourceIdAndDomainId(respData.getUser_id(), respData.getDomain_id());
            }
        }
        if (user == null) {
            throw new NotFindUserException();
        }
        session.setAttribute(Constant.CURRENT_USER,user);
        user.setUserPassword(null);
        log.info("The user(userName:{},sourceId:{}) login success",user.getUserName(),user.getSourceId());
        return user;
    }

    @Override
    public User loginByUser(User user, HttpSession session) throws NotFindUserException, NoAdminAccessException {
        User u = userRepository.findUserByUserNameAndUserPasswordAndRole(user.getUserName(), DigestUtils.sha1Hex(user.getUserPassword().getBytes()), User.Role.admin);
        if (null == u) {
            throw new NotFindUserException();
        }
        Property property = SystemPropertyInfo.getProperties().get(SystemPropertyInfo.ADMIN_AUTHORIZED_JIDS);
        if (property != null && StringUtils.hasText(property.getValue())) {
            String[] split = property.getValue().split(",");
            List<String> admins = Arrays.asList(split);
            if (admins.contains(JidGenerator.generate(u.getUserName(),SystemPropertyInfo.getProperties().get(OpenfireSystemProperties.XMPP_DOMAIN).getValue()))){
                session.setAttribute(Constant.CURRENT_USER,u);
                u.setUserPassword(null);
                return u;
            } else {
                throw new NoAdminAccessException();
            }
        } else {
            throw new NoAdminAccessException();
        }
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
    public UserData getUser(String userName) {
        return restApiUtil.getUser(userName);
    }

    @Override
    @Cacheable(value ="user",key = "#domainName+#userName")
    public User getUser(String userName, String domainName) throws NotFindDomainException, NotFindUserException {
        Domain domain = domainRepository.findByName(domainName);
        if (null == domain) {
            throw new NotFindDomainException();
        }
        User user = userRepository.findByDomainIdAndUserName(domain.getId(),userName);
        if (null == user) {
            throw new NotFindUserException();
        }
        user.setUserPassword(null);
        return user;
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
    @Transactional(rollbackFor = Exception.class)
    public User updateUser(User user, String domainName) throws NotFindUserException, NotFindDomainException {
        Domain domain = domainRepository.findByName(domainName);
        if (null == domain) {
            throw new NotFindDomainException();
        }
        User local = userRepository.findByDomainIdAndUserName(domain.getId(), user.getUserName());
        if (null == local) {
            throw new NotFindUserException();
        }
        //update the user in the database
        ObjectUtil.packData(user,local);
        User save = userRepository.save(local);
        //update the user in IAM system
        user.setSourceId(local.getSourceId());
        boolean result = IamUtil.getInstance().updateUser(user);
        if (result) {
            log.info("Update successfully the user ({}) in IAM system", user.getUserName());
        }
        return save;
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
