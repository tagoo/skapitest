package com.sunrun.controller;

import com.sunrun.common.notice.NoticeFactory;
import com.sunrun.common.notice.NoticeMessage;
import com.sunrun.common.notice.ReturnData;
import com.sunrun.exception.NameAlreadyExistException;
import com.sunrun.exception.NotFindGroupException;
import com.sunrun.service.GroupService;
import com.sunrun.entity.Group;
import com.sunrun.vo.GroupName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @PostMapping()
    public ReturnData create(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                             @RequestParam(name = "domainId",required = false)Integer domainId,
                             @ModelAttribute Group group){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        Group save = null;
        if (StringUtils.hasText(group.getName())){
            if (group.getDescription() == null) {
                group.setDescription("");
            }
            try {
               save = groupService.save(group,domainId);
               if (null != save) {
                   noticeMessage = NoticeMessage.SUCCESS;
               }
            } catch (NameAlreadyExistException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.GROUP_NAME_ALREADY_EXIST;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            noticeMessage = NoticeMessage.GROUP_NAME_IS_EMPTY;
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, save);
    }

    @GetMapping("{userName}/groups")
    public ReturnData getUserGroupList(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                       @PathVariable(name = "userName") String userName){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        List<String> allUserGroupList = null;
        if (StringUtils.hasText(userName)) {
            allUserGroupList = groupService.findAllUserGroupList(userName);
            noticeMessage = NoticeMessage.SUCCESS;
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, allUserGroupList);
    }

    @GetMapping()
    public ReturnData findAllGroup(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                       @RequestParam(name = "domainId",required = false) Integer domainId){
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        List<GroupName> all = null;
        try {
            all = groupService.findAll(domainId);
            noticeMessage = NoticeMessage.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, all);
    }
    @GetMapping("{groupName}")
    public ReturnData findGroup(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                @PathVariable(name = "groupName") String groupName){
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        Group group = null;
        try {
            group = groupService.findGroup(groupName);
            noticeMessage = NoticeMessage.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, group);
    }

    @PostMapping("users/remove/{userName}")
    public ReturnData removeUserFromGroup(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                          @PathVariable(name = "userName") String userName,
                                          @RequestParam(name = "groupNames")List<String> groupNames){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        if (StringUtils.hasText(userName) && groupNames != null && !groupNames.isEmpty()) {
            try {
                groupService.removeUserFromGroups(userName,groupNames);
                noticeMessage = NoticeMessage.SUCCESS;
            } catch (NotFindGroupException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.NOT_FIND_GROUP;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }
    @PostMapping("remove/{groupName}")
    public ReturnData removeUsersFromGroup(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                          @PathVariable(name = "groupName") String groupName,
                                          @RequestParam(name = "userNames")List<String> userNames){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        if (StringUtils.hasText(groupName) && userNames != null && !userNames.isEmpty()) {
            try {
                groupService.removeUsersFromGroup(groupName,userNames);
                noticeMessage = NoticeMessage.SUCCESS;
            } catch (NotFindGroupException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.NOT_FIND_GROUP;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }

    @PostMapping("users/add/{userName}")
    public ReturnData addUserToGroup(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                          @PathVariable(name = "userName") String userName,
                                          @RequestParam(name = "groupName") String groupName){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        if (StringUtils.hasText(groupName) ) {
            try {
                groupService.addUserToGroup(userName,groupName,true);
                noticeMessage = NoticeMessage.SUCCESS;
            } catch (NotFindGroupException e) {
                noticeMessage = NoticeMessage.NOT_FIND_GROUP;
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }

    @RequestMapping("delete/{groupName}")
    public ReturnData deleteGroup(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                          @PathVariable(name = "groupName") String groupName){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        if (StringUtils.hasText(groupName)) {
            try {
                groupService.deleteGroup(groupName);
                noticeMessage = NoticeMessage.SUCCESS;
            } catch (NotFindGroupException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.NOT_FIND_GROUP;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }

    @RequestMapping("update/{groupName}")
    public ReturnData updateGroup(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                  @PathVariable(name = "groupName") String groupName,
                                  @ModelAttribute Group group){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        if (StringUtils.hasText(groupName)) {
            try {
                group.setName(groupName);
                groupService.updateGroup(group);
                noticeMessage = NoticeMessage.SUCCESS;
            } catch (NotFindGroupException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.NOT_FIND_GROUP;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }
}
