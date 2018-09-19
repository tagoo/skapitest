package com.sunrun.service.impl;

import com.github.pagehelper.Page;
import com.sunrun.common.OpenfireSystemProperties;
import com.sunrun.dao.*;
import com.sunrun.entity.Domain;
import com.sunrun.entity.MucRoomMember;
import com.sunrun.entity.User;
import com.sunrun.entity.model.MucRoomMemberKey;
import com.sunrun.exception.*;
import com.sunrun.po.MucRoomPo;
import com.sunrun.service.MucRoomService;
import com.sunrun.support.iam.SystemPropertyInfo;
import com.sunrun.utils.Base32Util;
import com.sunrun.utils.RestApiUtil;
import com.sunrun.utils.helper.ArraysUtil;
import com.sunrun.utils.helper.ChatRoom;
import com.sunrun.entity.Property;
import com.sunrun.utils.helper.Role;
import com.sunrun.utils.helper.UserData;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.jivesoftware.smack.util.MD5;

import org.jxmpp.jid.Jid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.*;
import java.util.stream.Collectors;


@Service
@CacheConfig(cacheNames = "room")
@Slf4j
public class MucRoomServiceImpl implements MucRoomService {

    @Autowired
    private MucRoomMemberRepository mucRoomMemberRepository;
    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private UserRepository userRepository;
    @Resource
    private RestApiUtil restApiUtil;
    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @CachePut(key = "#chatRoom.roomName + #serviceName")
    public ChatRoom save(ChatRoom chatRoom, String serviceName) throws NameAlreadyExistException {
        ChatRoom room = restApiUtil.getChatRoom(chatRoom.getRoomName(), serviceName);
        if (room != null) {
            throw new NameAlreadyExistException();
        }
        ResponseEntity<String> responseEntity = restApiUtil.createChatRoom(chatRoom, serviceName);
        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
            log.info("Create chatRoom(name:{})success",chatRoom.getRoomName());
            return restApiUtil.getChatRoom(chatRoom.getRoomName(), serviceName);
        } else {
            return null;
        }
    }
    @Override
    @CachePut(key = "#chatRoom.roomName + #serviceName")
    public boolean update(ChatRoom chatRoom, String serviceName) throws NotFindRoomException {
        ChatRoom room = restApiUtil.getChatRoom(chatRoom.getRoomName(), serviceName);
        if (room == null) {
            throw new NotFindRoomException();
        }
        return  restApiUtil.updateChatRoom(chatRoom,serviceName);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CacheEvict(key = "#roomName + #serviceName")
    public boolean addMember(String roomName, String serviceName, Role role, List<String> names) throws NotFindRoomException, CrossDomainException {
        Domain domain = null;
        if (StringUtils.hasText(serviceName)) {
            domain = domainRepository.findByName(serviceName);
        }
        if (domain != null) {
            for (String userName : names) {
                if (userRepository.selectCountByDomainIdAndUserName(domain.getId(), userName) != 1) {
                    throw new CrossDomainException();
                }
            }
        }
        List<String> jidList = ArraysUtil.toListWithDomain(names);
        ChatRoom chatRoom = restApiUtil.getChatRoom(roomName, serviceName);
        if (null == chatRoom) {
            throw new NotFindRoomException();
        }
        boolean flag = true;
        for (String jid : jidList) {
            if (restApiUtil.addMember(roomName, serviceName, role, jid)) {
                if (role == Role.members) {
                    int index = jid.indexOf("@");
                    String name = index > -1 ? jid.substring(0, index) : jid;
                    UserData user = restApiUtil.getUser(name);
                    List<Property> url = user.getProperties().stream().filter(u -> u.getKey().equals("url")).collect(Collectors.toList());
                    if (!url.isEmpty()) {
                        MucRoomMember member = mucRoomMemberRepository.findMucRoomByNameAndServiceName(roomName, serviceName, jid);
                        member.setUrl(url.get(0).getValue());
                    }
                }
            } else {
                flag = false;
            }
        }
        return flag;
    }

    @Override
    @CacheEvict(key = "#roomName + #serviceName")
    public void removeMember(String roomName, String serviceName, Role role, List<String> userName) {
        if (null != userName && !userName.isEmpty()) {
            userName.forEach(name -> restApiUtil.removeMember(roomName,serviceName,role,name));
        }
    }

    @Override
    @CacheEvict(key = "#roomName + #serviceName")
    public boolean delete(String roomName, String serviceName) throws NotFindRoomException {
        ChatRoom chatRoom = restApiUtil.getChatRoom(roomName, serviceName);
        if (chatRoom == null) {
            throw new NotFindRoomException();
        }
        return restApiUtil.deleteChatRoom(roomName,serviceName);
    }

    @Override
    @Cacheable(key = "#roomName + #serviceName")
    public ChatRoom getChatRoom(String roomName, String serviceName) {
        return restApiUtil.getChatRoom(roomName, serviceName);
    }

    @Override
    public List<ChatRoom> getChatRoomList(Integer domainId, String serviceName, String type, String search, Page<ChatRoom> page) throws DomainInvalidException {
        if (null != domainId) {
            Optional<Domain> op = domainRepository.findById(domainId);
            if (op.isPresent()) {
                serviceName = op.get().getName();
            }
        }
        List<ChatRoom> allChatRooms = restApiUtil.getAllChatRooms(serviceName, type, search);
        List<ChatRoom> collect = allChatRooms.stream().sorted(Comparator.comparing(ChatRoom::getCreationDate)).collect(Collectors.toList());
        List<ChatRoom> data = new ArrayList<>();
        int offset = (page.getPageNum())*page.getPageSize();
        int limit = offset + page.getPageSize();
        if (offset >= collect.size()) {
            return Collections.EMPTY_LIST;
        }else if (limit <= collect.size()) {
            for(int i = offset ; i < limit; i++) {
                data.add(collect.get(i));
            }
        } else {
            for(int i = offset ; i < collect.size(); i++) {
                data.add(collect.get(i));
            }
        }
        page.setTotal(collect.size());
        return data;
    }

    @Override
    public boolean addGroupRoleToChatRoom(String roomName, String serviceName, String name, Role role) throws NotFindRoomException {
        ChatRoom chatRoom = restApiUtil.getChatRoom(roomName, serviceName);
        if (chatRoom == null) {
            throw new NotFindRoomException();
        }
        return restApiUtil.addGroupRoleToChatRoom(roomName, serviceName, name, role);
    }

    @Override
    public List<MucRoomPo> findChatRoomsByUserName(String jid, Integer domainId, Pageable pageable) throws NotFindUserException {
        String domainName = SystemPropertyInfo.getProperties().get(OpenfireSystemProperties.XMPP_DOMAIN).getValue();
        String queryUserName = jid;
        if (jid.contains("@")) {
            queryUserName = jid.substring(0,jid.lastIndexOf("@"));
        }
        User user = userRepository.findByDomainIdAndUserName(domainId, queryUserName);
        if (user == null) {
            throw new NotFindUserException();
        }

        List<String> userGroups = restApiUtil.getUserGroups(jid);
        List<MucRoomPo> rooms = null;
        if (null != userGroups && !userGroups.isEmpty()) {
            List<String> collect = userGroups.stream().map(g -> Base32Util.encode(g) + "@" + domainName + "/" + MD5.hex(g).toLowerCase()).collect(Collectors.toList());
            rooms = mucRoomMemberRepository.findRoomByGroupJidIn(collect);
        }
        if (!jid.contains("@")) {
            jid += "@" + domainName;
        }
        StringBuilder sql = new StringBuilder("SELECT r.roomID,r.name,r.naturalName,s.subdomain FROM ofmucroom r INNER JOIN (SELECT roomID as roomID from ofmucmember m WHERE m.jid = :jid UNION all SELECT roomID as roomID FROM ofmucaffiliation WHERE jid = :jid) t2 on r.roomID = t2.roomID LEFT JOIN ofmucservice AS s ON s.serviceID = r.serviceID");
        if (pageable != null) {
            sql.append(" limit ");
            sql.append((pageable.getPageNumber()-1) * pageable.getPageSize());
            sql.append("," + pageable.getPageSize());
        }
        Session session = entityManager.unwrap(Session.class);
        NativeQuery query = session.createNativeQuery(sql.toString());
        query.addScalar("roomID",StandardBasicTypes.LONG)
              .addScalar("name", StandardBasicTypes.STRING)
              .addScalar("naturalName", StandardBasicTypes.STRING)
              .addScalar("subdomain", StandardBasicTypes.STRING);
        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.aliasToBean(MucRoomPo.class));
        query.setParameter("jid",jid);
        List<MucRoomPo> resultList = query.getResultList();
        entityManager.close();
        if (rooms != null) {
            rooms.forEach(r -> {if (!resultList.contains(r)){
                resultList.add(r);
            }});
        }
        return resultList;
    }

    private boolean isGroup(Jid jid) {
        return jid.getResourceOrNull() != null &&
               Base32Util.isBase32(jid.getLocalpartOrNull().asUnescapedString()) && jid.getResourceOrNull().toString().equals(MD5.hex(Base32Util.decode(jid.getLocalpartOrNull().toString())).toLowerCase());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public MucRoomMember updateMemberNickname(MucRoomMember member) {
        MucRoomMember local = entityManager.find(MucRoomMember.class, new MucRoomMemberKey(member.getRoomID(), member.getJid()));
        if (local != null) {
            local.setNickname(member.getNickname());
            return entityManager.merge(local);
        }
        return null;
    }

}
