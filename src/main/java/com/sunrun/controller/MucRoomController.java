package com.sunrun.controller;

import com.github.pagehelper.PageInfo;
import com.sunrun.common.Pagination;
import com.sunrun.common.notice.NoticeFactory;
import com.sunrun.common.notice.NoticeMessage;
import com.sunrun.common.notice.ReturnData;
import com.sunrun.entity.MucRoom;
import com.sunrun.exception.AlreadyExistException;
import com.sunrun.exception.NameAlreadyExistException;
import com.sunrun.exception.NotFindRoomException;
import com.sunrun.service.MucRoomService;
import com.sunrun.utils.helper.ChatRoom;
import com.sunrun.utils.helper.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/room")
public class MucRoomController {
    @Autowired
    private MucRoomService mucRoomService;

    @PostMapping("save")
    public ReturnData save(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                           @RequestParam("roomUser") String roomUser,
                           @RequestParam(value = "nickName", defaultValue = "") String nickName,
                           @RequestParam(value = "serviceName", required = false) String serviceName,
                           ChatRoom chatRoom) {
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        ChatRoom room = null;
        if (StringUtils.hasText(chatRoom.getRoomName()) && StringUtils.hasText(chatRoom.getNaturalName())) {
            if (chatRoom.getDescription() == null) {
                chatRoom.setDescription("");
            }
            if (StringUtils.hasText(roomUser)) {
                chatRoom.setOwners(Arrays.asList(roomUser));
                try {
                    room = mucRoomService.save(chatRoom, serviceName);
                    if (room != null) {
                        noticeMessage = NoticeMessage.SUCCESS;
                    }
                } catch (NameAlreadyExistException e) {
                    e.printStackTrace();
                    noticeMessage = NoticeMessage.ROOM_NAME_ALREADY_EXIST;
                }
            } else {
                noticeMessage = NoticeMessage.USERNAME_IS_NULL;
            }
        } else {
            noticeMessage = NoticeMessage.ROOM_NAME_IS_EMPTY;
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, room);
    }

    @RequestMapping("update")
    public ReturnData update(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                             @RequestParam(value = "serviceName", required = false) String serviceName,
                             ChatRoom chatRoom) {
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        if (StringUtils.hasText(chatRoom.getRoomName())){
            try {
                if (mucRoomService.update(chatRoom,serviceName)) {
                    noticeMessage = NoticeMessage.SUCCESS;
                }
            } catch (NotFindRoomException e) {
               noticeMessage = NoticeMessage.NOT_FIND_ROOM;
            }
        } else {
            noticeMessage = NoticeMessage.ROOM_NAME_IS_EMPTY;
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }

    @RequestMapping("member/add")
    public ReturnData addMember(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                @RequestParam(value = "serviceName", required = false) String serviceName,
                                @RequestParam(name= "roles", defaultValue = "members") String roles,
                                @RequestParam(name = "roomName") String roomName,
                                @RequestParam(name = "name") String name){

        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        if (!StringUtils.hasText(roomName)){
            noticeMessage = NoticeMessage.ROOM_NAME_IS_EMPTY;
        } else if (!StringUtils.hasText(name)) {
            noticeMessage = NoticeMessage.USERNAME_IS_NULL;
        } else {
            try {
                if (mucRoomService.addMember(roomName,serviceName, Role.valueOf(roles),name)){
                    noticeMessage = NoticeMessage.SUCCESS;
                }
            } catch (AlreadyExistException e) {
                noticeMessage = NoticeMessage.USER_REPEAT_ROOM_MEMBER;
            } catch (NotFindRoomException e) {
                noticeMessage = NoticeMessage.NOT_FIND_ROOM;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }

    @RequestMapping("member/remove")
    public ReturnData removeMember(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                @RequestParam(value = "serviceName", required = false) String serviceName,
                                @RequestParam(name= "roles", defaultValue = "members") String roles,
                                @RequestParam(name = "roomName") String roomName,
                                @RequestParam(name = "name") String name){

        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        if (!StringUtils.hasText(roomName)){
            noticeMessage = NoticeMessage.ROOM_NAME_IS_EMPTY;
        } else if (!StringUtils.hasText(name)) {
            noticeMessage = NoticeMessage.USERNAME_IS_NULL;
        } else {
            if (mucRoomService.removeMember(roomName,serviceName, Role.valueOf(roles),name)){
                noticeMessage = NoticeMessage.SUCCESS;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }

    @RequestMapping("member/destroy")
    public ReturnData delete(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                             @RequestParam(value = "serviceName", required = false) String serviceName,
                             @RequestParam(name = "roomName") String roomName){
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        if (!StringUtils.hasText(roomName)){
            noticeMessage = NoticeMessage.ROOM_NAME_IS_EMPTY;
        }
        try {
            if (mucRoomService.delete(roomName,serviceName)){
                noticeMessage = NoticeMessage.SUCCESS;
            }
        } catch (NotFindRoomException e) {
            noticeMessage = NoticeMessage.NOT_FIND_ROOM;
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }

    @RequestMapping("query")
    public ReturnData getAllChatRooms(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                      @RequestParam(name = "type", required = false) String type,
                                      @RequestParam(name = "search",required = false) String search,
                                      @RequestParam(name = "serviceName", required = false) String serviceName,
                                      @RequestParam(name = "pageSize",required = false) Integer pageSize,
                                      @RequestParam(name = "pageNum",required = false)Integer pageNum){
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        if (type != null && ("public".equals(type) || "all".equals(type))) {
            noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        }
        Pagination pagination = null;
        if (pageNum != null && pageSize != null) {
            pagination = new Pagination(pageNum, pageSize);
        }
        PageInfo<ChatRoom> info = mucRoomService.getChatRoomList(serviceName, type, search, pagination);
        Map<String,Object> data = null;
        if (info != null) {
            noticeMessage = NoticeMessage.SUCCESS;
            data = new HashMap<>();
            data.put("rooms",info.getList());
            if (pagination != null) {
                data.put("total",info.getTotal());
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, data);
    }

    @RequestMapping("group/role")
    public ReturnData addGroupRoleToChatRoom(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                      @RequestParam(name = "roles", defaultValue = "members") String roles,
                                      @RequestParam(name = "roomName",required = false) String roomName,
                                      @RequestParam(name = "serviceName", required = false) String serviceName,
                                      @RequestParam(name = "name",required = false) String name){
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        if (!StringUtils.hasText(roomName)){
            noticeMessage = NoticeMessage.ROOM_NAME_IS_EMPTY;
        }
        if (!StringUtils.hasText(name)) {
            noticeMessage = NoticeMessage.GROUP_NAME_IS_EMPTY;
        }
        try {
            if (mucRoomService.addGroupRoleToChatRoom(roomName,serviceName,name,Role.valueOf(roles))){
                noticeMessage = NoticeMessage.SUCCESS;
            }
        } catch (NotFindRoomException e) {
            noticeMessage = NoticeMessage.NOT_FIND_ROOM;
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }

    @GetMapping("ownquery/{userName}")
    public ReturnData getOwnerChatRooms(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                        @RequestParam(name = "pageNum", required = false) Integer pageNum,
                                        @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                        @PathVariable(name = "userName") String userName) {
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        List<MucRoom> chatRoomsByUserName = null;
        Pagination pagination = null;
        if (!StringUtils.hasText(userName)){
            noticeMessage = NoticeMessage.USERNAME_IS_NULL;
        } else {
            if (pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0) {
                pagination = new Pagination(pageNum, pageSize);
            }
            try {
                chatRoomsByUserName = mucRoomService.findChatRoomsByUserName(userName,pagination);
                noticeMessage = NoticeMessage.SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, chatRoomsByUserName);
    }
}
