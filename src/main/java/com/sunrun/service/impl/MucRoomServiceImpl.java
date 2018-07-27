package com.sunrun.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sunrun.common.Pagination;
import com.sunrun.dao.MucRoomMemberRepository;
import com.sunrun.dao.MucRoomRepository;
import com.sunrun.entity.MucRoom;
import com.sunrun.entity.MucRoomMember;
import com.sunrun.entity.model.MucRoomMemberKey;
import com.sunrun.exception.AlreadyExistException;
import com.sunrun.exception.NameAlreadyExistException;
import com.sunrun.exception.NotFindRoomException;
import com.sunrun.service.MucRoomService;
import com.sunrun.utils.RestApiUtil;
import com.sunrun.utils.helper.ChatRoom;
import com.sunrun.entity.Property;
import com.sunrun.utils.helper.Role;
import com.sunrun.utils.helper.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class MucRoomServiceImpl implements MucRoomService {
    @Autowired
    private MucRoomRepository mucRoomRepository;
    @Autowired
    private MucRoomMemberRepository mucRoomMemberRepository;
    @Resource
    private RestApiUtil restApiUtil;
    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ChatRoom save(ChatRoom chatRoom, String serviceName) throws NameAlreadyExistException {
        ChatRoom room = restApiUtil.getChatRoom(chatRoom.getRoomName(), serviceName);
        if (room != null) {
            throw new NameAlreadyExistException();
        }
        ResponseEntity<String> responseEntity = restApiUtil.creatChatRoom(chatRoom, serviceName);
        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
            logger.info("Create chatRoom(name:" + chatRoom.getRoomName() + ") success");
            return restApiUtil.getChatRoom(chatRoom.getRoomName(), serviceName);
        } else {
            return null;
        }
    }
    @Override
    public boolean update(ChatRoom chatRoom, String serviceName) throws NotFindRoomException {
        ChatRoom room = restApiUtil.getChatRoom(chatRoom.getRoomName(), serviceName);
        if (room != null) {
           return  restApiUtil.updateChatRoom(chatRoom,serviceName);
        } else {
            throw new NotFindRoomException();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean addMember(String roomName, String serviceName, Role roles, String jid) throws AlreadyExistException, NotFindRoomException {
        ChatRoom chatRoom = restApiUtil.getChatRoom(roomName, serviceName);
        if (chatRoom != null) {
            if (chatRoom.getOwners().contains(jid)) {
                throw new AlreadyExistException();
            } else {
               if (restApiUtil.addMember(roomName,serviceName,roles,jid)) {
                   if (roles == Role.members) {
                       int index = jid.indexOf("@");
                       String name = index > -1 ? jid.substring(0,index):jid;
                       UserData user = restApiUtil.getUser(name);
                       List<Property> url = user.getProperties().stream().filter(u -> u.getKey().equals("url")).collect(Collectors.toList());
                       if (!url.isEmpty()) {
                           MucRoomMember member = mucRoomMemberRepository.findMucRoomByNameAndServiceName(roomName, serviceName, jid);
                           member.setUrl(url.get(0).getValue());
                       }

                   }
                   return true;
               }
               return false;
            }
        } else {
            throw new NotFindRoomException();
        }
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
    public PageInfo<ChatRoom> getChatRoomList(String serviceName, String type, String search, Pagination pagination) {
        //分页不生效，因为不是直接查的mysql数据库，只能让前端自己实现分页
        if (pagination != null) {
            PageHelper.startPage(pagination.getPageNum(), pagination.getPageSize());
        }
        List<ChatRoom> list = restApiUtil.getAllChatRooms(serviceName, type, search);
        PageInfo<ChatRoom> info = new PageInfo<>(list);
        return info;
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
    public List<MucRoom> findChatRoomsByUserName(String jid,Pagination pagination) {
        StringBuilder sql = new StringBuilder("SELECT r.* FROM ofmucroom r INNER JOIN (SELECT roomID as roomID from ofmucmember m WHERE m.jid = :jid UNION all SELECT roomID as roomID FROM ofmucaffiliation WHERE jid = :jid) t2 on r.roomID = t2.roomID");
        if (pagination != null) {
            sql.append(" limit ");
            sql.append((pagination.getPageNum()-1) * pagination.getPageSize());
            sql.append("," + pagination.getPageSize());
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
