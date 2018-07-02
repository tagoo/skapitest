package com.sunrun.service;


import com.github.pagehelper.PageInfo;
import com.sunrun.common.Pagination;
import com.sunrun.exception.NameAlreadyExistException;
import com.sunrun.exception.NotFindRoomException;
import com.sunrun.utils.helper.ChatRoom;
import com.sunrun.utils.helper.Role;


public interface MucRoomService {
    ChatRoom save(ChatRoom chatRoom,String serviceName) throws NameAlreadyExistException;

    boolean update(ChatRoom chatRoom, String serviceName) throws NotFindRoomException;

    boolean addMember(String roomName, String serviceName, Role roles, String name);

    boolean removeMember(String roomName, String serviceName, Role role, String name);

    boolean delete(String roomName, String serviceName) throws NotFindRoomException;

    PageInfo<ChatRoom> getChatRoomList(String serviceName, String type, String search, Pagination pagination);

    boolean addGroupRoleToChatRoom(String roomName, String serviceName, String name, Role role) throws NotFindRoomException;

    void findChatRoomsByUserName(String userName);
}
