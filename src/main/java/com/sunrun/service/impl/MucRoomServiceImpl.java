package com.sunrun.service.impl;

import com.github.pagehelper.Page;
import com.sunrun.dao.*;
import com.sunrun.entity.Domain;
import com.sunrun.entity.MucRoom;
import com.sunrun.entity.MucRoomMember;
import com.sunrun.entity.model.MucRoomMemberKey;
import com.sunrun.exception.*;
import com.sunrun.service.MucRoomService;
import com.sunrun.utils.RestApiUtil;
import com.sunrun.utils.helper.ArraysUtil;
import com.sunrun.utils.helper.ChatRoom;
import com.sunrun.entity.Property;
import com.sunrun.utils.helper.Role;
import com.sunrun.utils.helper.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import javax.persistence.Query;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class MucRoomServiceImpl implements MucRoomService {
    @Autowired
    private MucRoomRepository mucRoomRepository;
    @Autowired
    private MucRoomMemberRepository mucRoomMemberRepository;
    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MucServiceRepository mucServiceRepository;
    @Resource
    private RestApiUtil restApiUtil;
    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ChatRoom save(ChatRoom chatRoom, String serviceName, Integer domainId) throws NameAlreadyExistException {
        serviceName = domainId != null ? getServiceName(domainId): serviceName;
        ChatRoom room = restApiUtil.getChatRoom(chatRoom.getRoomName(), serviceName);
        if (room != null) {
            throw new NameAlreadyExistException();
        }
        ResponseEntity<String> responseEntity = restApiUtil.createChatRoom(chatRoom, serviceName);
        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
            logger.info("Create chatRoom(name:{})success",chatRoom.getRoomName());
            return restApiUtil.getChatRoom(chatRoom.getRoomName(), serviceName);
        } else {
            return null;
        }
    }
    @Override
    public boolean update(ChatRoom chatRoom, String serviceName, Integer domainId) throws NotFindRoomException {
        serviceName = domainId != null ? getServiceName(domainId): serviceName;
        ChatRoom room = restApiUtil.getChatRoom(chatRoom.getRoomName(), serviceName);
        if (room != null) {
           return  restApiUtil.updateChatRoom(chatRoom,serviceName);
        } else {
            throw new NotFindRoomException();
        }
    }

    private String getServiceName(Integer domainId) {
        if (null == domainId) return null;
        Optional<Domain> op = domainRepository.findById(domainId);
        if (op.isPresent()) {
            return mucServiceRepository.getOne(op.get().getName()).getSubdomain();
        }  else {
            return null;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean addMember(String roomName, String serviceName, Integer domainId, Role role, List<String> names) throws NotFindRoomException, CrossDomainException {
        Domain domain = null;
        if (domainId != null) {
            Optional<Domain> op = domainRepository.findById(domainId);
            if (op.isPresent()) {
                domain = op.get();
                serviceName = mucServiceRepository.getOne(domain.getName()).getSubdomain();
            }
        } else if(StringUtils.hasText(serviceName)) {
           domain = domainRepository.findByName(serviceName);
        }
        if (domain != null) {
            for (String userName: names) {
                if (userRepository.selectCountByDomainIdAndUserName(domain.getId(),userName) !=1){
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
    public boolean removeMember(String roomName, String serviceName, Role role, String name) {
        return restApiUtil.removeMember(roomName,serviceName,role,name);
    }

    @Override
    public boolean delete(String roomName, String serviceName) throws NotFindRoomException {
        ChatRoom chatRoom = restApiUtil.getChatRoom(roomName, serviceName);
        if (chatRoom == null) {
            throw new NotFindRoomException();
        }
        return restApiUtil.deleteChatRoom(roomName,serviceName);
    }

    @Override
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
    public List<MucRoom> findChatRoomsByUserName(String jid, Pageable pageable) {
        StringBuilder sql = new StringBuilder("SELECT r.* FROM ofmucroom r INNER JOIN (SELECT roomID as roomID from ofmucmember m WHERE m.jid = :jid UNION all SELECT roomID as roomID FROM ofmucaffiliation WHERE jid = :jid) t2 on r.roomID = t2.roomID");
        if (pageable != null) {
            sql.append(" limit ");
            sql.append((pageable.getPageNumber()-1) * pageable.getPageSize());
            sql.append("," + pageable.getPageSize());
        }
        Query query = entityManager.createNativeQuery(sql.toString(),MucRoom.class);
        query.setParameter("jid",jid);
        List<MucRoom> resultList = query.getResultList();
        entityManager.close();
        return resultList;
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
