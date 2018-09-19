package com.sunrun.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.sunrun.common.Constant;
import com.sunrun.common.OpenfireSystemProperties;
import com.sunrun.common.notice.NoticeFactory;
import com.sunrun.common.notice.NoticeMessage;
import com.sunrun.common.notice.ReturnData;
import com.sunrun.entity.Domain;
import com.sunrun.entity.MucRoom;
import com.sunrun.entity.MucRoomMember;
import com.sunrun.exception.*;
import com.sunrun.po.MucRoomPo;
import com.sunrun.service.DomainService;
import com.sunrun.service.MucRoomService;
import com.sunrun.service.MucServiceService;
import com.sunrun.support.iam.SystemPropertyInfo;
import com.sunrun.utils.XmppConnectionUtil;
import com.sunrun.utils.helper.ArraysUtil;
import com.sunrun.utils.helper.ChatRoom;
import com.sunrun.utils.helper.Role;
import lombok.extern.slf4j.Slf4j;
import org.jxmpp.jid.impl.JidCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/room")
@Slf4j
public class MucRoomController {
    @Autowired
    private MucRoomService mucRoomService;
    @Autowired
    private DomainService domainService;
    @Autowired
    private MucServiceService mucServiceService;
    @PostMapping("save")
    public ReturnData save(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                           @RequestParam(value = "nickName", defaultValue = "") String nickName,
                           @RequestParam(value = "serviceName", required = false) String serviceName,
                           @RequestParam(value = "domainId", required = false) Integer domainId,
                           ChatRoom chatRoom) {
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        if (StringUtils.hasText(chatRoom.getRoomName()) && StringUtils.hasText(chatRoom.getNaturalName())) {
            if (chatRoom.getDescription() == null) {
                chatRoom.setDescription("");
            }
            if (null != chatRoom.getOwners()) {
                chatRoom.setOwners(ArraysUtil.toListWithDomain(chatRoom.getOwners()));
                if (null != chatRoom.getMembers()) {
                    chatRoom.setMembers(ArraysUtil.toListWithDomain(chatRoom.getMembers()));
                }
                if (null != chatRoom.getAdmins()) {
                    chatRoom.setAdmins(ArraysUtil.toListWithDomain(chatRoom.getAdmins()));
                }
                try {
                    if (null != domainId) {
                        serviceName = getServiceName(domainId);
                    }
                    ChatRoom room = mucRoomService.save(chatRoom, serviceName);
                    if (room != null) {
                        noticeMessage = NoticeMessage.SUCCESS;
                    }
                } catch (NameAlreadyExistException e) {
                    e.printStackTrace();
                    noticeMessage = NoticeMessage.ROOM_NAME_ALREADY_EXIST;
                } catch (NotFindDomainException e) {
                    noticeMessage = NoticeMessage.DOMAIN_NOT_EXIST;
                    e.printStackTrace();
                }
            } else {
                noticeMessage = NoticeMessage.ROOM_NEED_AT_LEAST_OWNER;
            }
        }  else {
            noticeMessage = NoticeMessage.ROOM_NAME_IS_EMPTY;
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }

    @RequestMapping(value = "update/{roomName}",method = RequestMethod.POST)
    public ReturnData update(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                             @RequestParam(value = "serviceName", required = false) String serviceName,
                             @RequestParam(value = "domainId", required = false) Integer domainId,
                             @PathVariable(value = "roomName") String roomName,
                             ChatRoom chatRoom) {
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        if (StringUtils.hasText(roomName)) {
            try {
                if (null != domainId) {
                    serviceName = getServiceName(domainId);
                }
                chatRoom.setRoomName(roomName);
                if (mucRoomService.update(chatRoom, serviceName)) {
                    noticeMessage = NoticeMessage.SUCCESS;
                }
            } catch (NotFindRoomException e) {
                noticeMessage = NoticeMessage.NOT_FIND_ROOM;
            } catch (NotFindDomainException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.DOMAIN_NOT_EXIST;
            }
        } else {
            noticeMessage = NoticeMessage.ROOM_NAME_IS_EMPTY;
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }

    @RequestMapping("{roomName}/member/add")
    public ReturnData addMember(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                @RequestParam(name = "serviceName", required = false) String serviceName,
                                @RequestParam(name = "domainId", required = false) Integer domainId,
                                @RequestParam(name = "roles", defaultValue = "members") String roles,
                                @PathVariable(name = "roomName") String roomName,
                                @RequestParam(name = "userNames") List<String> userNames){

        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        if (!StringUtils.hasText(roomName)){
            noticeMessage = NoticeMessage.ROOM_NAME_IS_EMPTY;
        } else if (userNames == null || userNames.isEmpty()) {
            noticeMessage = NoticeMessage.USERNAME_IS_NULL;
        } else {
            try {
                if (null != domainId) {
                    serviceName = getServiceName(domainId);
                }
                if (mucRoomService.addMember(roomName,serviceName,Role.valueOf(roles),userNames)){
                    noticeMessage = NoticeMessage.SUCCESS;
                }
            } catch (CrossDomainException e) {
                noticeMessage = NoticeMessage.USER_REPEAT_ROOM_MEMBER;
            } catch (NotFindRoomException e) {
                noticeMessage = NoticeMessage.NOT_FIND_ROOM;
            } catch (NotFindDomainException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.DOMAIN_NOT_EXIST;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }

    @RequestMapping("{roomName}/member/remove")
    public ReturnData removeMember(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                @RequestParam(name = "serviceName", required = false) String serviceName,
                                @RequestParam(name= "roles", defaultValue = "members") String roles,
                                @PathVariable(name = "roomName") String roomName,
                                @RequestParam(name = "userNames") List<String> userNames){
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        if (!StringUtils.hasText(roomName)){
            noticeMessage = NoticeMessage.ROOM_NAME_IS_EMPTY;
        } else if (userNames.isEmpty()) {
            noticeMessage = NoticeMessage.USERNAME_IS_NULL;
        } else {
            try {
                mucRoomService.removeMember(roomName, serviceName, Role.valueOf(roles), userNames);
                noticeMessage = NoticeMessage.SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }

    @RequestMapping(value = "destroy/{roomName}",method = RequestMethod.POST)
    public ReturnData delete(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                             @RequestParam(value = "serviceName", required = false) String serviceName,
                             @RequestParam(value = "domainId", required = false) Integer domainId,
                             @PathVariable(name = "roomName") String roomName){
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        if (!StringUtils.hasText(roomName)){
            noticeMessage = NoticeMessage.ROOM_NAME_IS_EMPTY;
        } else {
            try {
                if (null != domainId) {
                    serviceName = getServiceName(domainId);
                }
                if (mucRoomService.delete(roomName, serviceName)) {
                    noticeMessage = NoticeMessage.SUCCESS;
                }
            } catch (NotFindRoomException e) {
                noticeMessage = NoticeMessage.NOT_FIND_ROOM;
            } catch (NotFindDomainException e) {
                noticeMessage = NoticeMessage.DOMAIN_NOT_EXIST;
                e.printStackTrace();
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }
    @GetMapping("{roomName}")
    public ReturnData getChatRoom(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                  @RequestParam(value = "serviceName", required = false) String serviceName,
                                  @RequestParam(value = "domainId", required = false) Integer domainId,
                                  @PathVariable(name = "roomName") String roomName){
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        ChatRoom chatRoom = null;
        if (!StringUtils.hasText(roomName)){
            noticeMessage = NoticeMessage.ROOM_NAME_IS_EMPTY;
        } else {
            try {
                if (null != domainId) {
                    serviceName = getServiceName(domainId);
                }
                chatRoom = mucRoomService.getChatRoom(roomName, serviceName);
                noticeMessage = NoticeMessage.SUCCESS;
            } catch (NotFindDomainException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.DOMAIN_NOT_EXIST;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, chatRoom);
    }

    @RequestMapping("query/all")
    public ReturnData getAllChatRooms(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                      @RequestParam(name = "type", required = false, defaultValue = "public") String type,
                                      @RequestParam(name = "search",required = false) String search,
                                      @RequestParam(name = "serviceName", required = false) String serviceName,
                                      @RequestParam(name = "domainId", required = false) Integer domainId,
                                      @RequestParam(name = "page", required = false, defaultValue = "0") Integer pageNum,
                                      @RequestParam(name = "size", required = false,defaultValue = "50") Integer pageSize){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        Map<String,Object> returnData = new HashMap<>();
        if (type != null && ("public".equals(type) || "all".equals(type))) {
            try {
                Page<ChatRoom> page = new Page<>(pageNum,pageSize);
                List<ChatRoom> data = mucRoomService.getChatRoomList(domainId, serviceName, type, search, page);
                returnData.put("content",data);
                returnData.put("total",page.getTotal());
                noticeMessage = NoticeMessage.SUCCESS;
            } catch (DomainInvalidException e) {
                noticeMessage = NoticeMessage.DOMAIN_INVALID;
                log.error(noticeMessage.getCnMessage());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, returnData);
    }

    @RequestMapping("group/role")
    public ReturnData addGroupRoleToChatRoom(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                             @RequestParam(name = "roles", defaultValue = "members") String roles,
                                             @RequestParam(name = "roomName", required = false) String roomName,
                                             @RequestParam(name = "serviceName", required = false) String serviceName,
                                             @RequestParam(name = "name", required = false) String name) {
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        if (!StringUtils.hasText(roomName)) {
            noticeMessage = NoticeMessage.ROOM_NAME_IS_EMPTY;
        }
        if (!StringUtils.hasText(name)) {
            noticeMessage = NoticeMessage.GROUP_NAME_IS_EMPTY;
        }
        try {
            if (mucRoomService.addGroupRoleToChatRoom(roomName, serviceName, name, Role.valueOf(roles))) {
                noticeMessage = NoticeMessage.SUCCESS;
            }
        } catch (NotFindRoomException e) {
            noticeMessage = NoticeMessage.NOT_FIND_ROOM;
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }

    @GetMapping("{userName}/rooms")
    public ReturnData getOwnerChatRooms(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                        @RequestParam(name = "domainId") Integer domainId,
                                        @RequestParam(name = "pageNum", required = false) Integer pageNum,
                                        @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                        @PathVariable(name = "userName") String userName) {
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        List<MucRoomPo> chatRoomsByUserName = null;
        Pageable pageable = null;
        if (!StringUtils.hasText(userName)) {
            noticeMessage = NoticeMessage.USERNAME_IS_NULL;
        } else {

            if (pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0) {
                pageable = PageRequest.of(pageNum, pageSize);
            }
            try {
                chatRoomsByUserName = mucRoomService.findChatRoomsByUserName(userName,domainId,pageable);
                noticeMessage = NoticeMessage.SUCCESS;
            } catch (NotFindUserException e) {
                noticeMessage = NoticeMessage.USER_NOT_EXIST;
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, chatRoomsByUserName);
    }

    @RequestMapping("member/update/{jid}")
    public ReturnData updateNickName(@RequestParam(name = "lang", defaultValue = "zh")String lang,
                                     @RequestParam(name = "roomID") Long roomID,
                                     @RequestParam(name = "nickName") String nickName,
                                     @PathVariable(name = "jid") String jid){
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        MucRoomMember update = null;
        if(!StringUtils.hasText(jid)) {
            noticeMessage = NoticeMessage.USERNAME_IS_NULL;
        }else if (roomID == null) {
            noticeMessage = NoticeMessage.ROOM_NAME_IS_EMPTY;
        } else {
            MucRoomMember member = new MucRoomMember();
            member.setRoomID(roomID);
            member.setNickname(nickName);
            member.setJid(jid);
            update = mucRoomService.updateMemberNickname(member);
            if (update != null) {
                noticeMessage = NoticeMessage.SUCCESS;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, update);
    }

    @RequestMapping("{roomName}/chatHistory")
    public ReturnData getMessageHistory(@RequestParam(name = "lang", defaultValue = "zh")String lang,
                                     @RequestParam(name = "serviceName", required = false) String serviceName,
                                     @RequestParam(name = "domainId", required = false) Integer domainId,
                                     @PathVariable(name = "roomName") String roomName){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        if (null != domainId) {
            try {
                serviceName = getServiceName(domainId);
            } catch (NotFindDomainException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.DOMAIN_NOT_EXIST;
            }
            //TODO
            //mucRoomService.getMessageHistory(roomName,serviceName);
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }
    private String getServiceName(Integer domainId) throws NotFindDomainException {
        if (null == domainId) return null;
        Domain domain = domainService.findById(domainId);
        if (null == domain) {
            throw new NotFindDomainException();
        }
        return mucServiceService.findById(domain.getName()).getSubdomain();
    }
}
