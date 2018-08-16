package com.sunrun.service;


import com.github.pagehelper.Page;
import com.sunrun.entity.MucRoom;
import com.sunrun.entity.MucRoomMember;
import com.sunrun.exception.*;
import com.sunrun.utils.helper.ChatRoom;
import com.sunrun.utils.helper.Role;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface MucRoomService {
    ChatRoom save(ChatRoom chatRoom, String serviceName, Integer domainId) throws NameAlreadyExistException;

    boolean update(ChatRoom chatRoom, String serviceName, Integer domainId) throws NotFindRoomException;

    boolean removeMember(String roomName, String serviceName, Role role, String name);

    boolean delete(String roomName, String serviceName) throws NotFindRoomException;

    List<ChatRoom> getChatRoomList(Integer domainId, String serviceName, String type, String search, Page<ChatRoom> page) throws DomainInvalidException;

    boolean addGroupRoleToChatRoom(String roomName, String serviceName, String name, Role role) throws NotFindRoomException;

    List<MucRoom> findChatRoomsByUserName(String userName, Pageable pagination);

    MucRoomMember updateMemberNickname(MucRoomMember member);

    ChatRoom getChatRoom(String roomName, String serviceName);

    boolean addMember(String roomName, String serviceName, Integer domainId, Role role, List<String> names) throws  NotFindRoomException, CrossDomainException;;
}
