package com.sunrun.service;

import com.sunrun.entity.Group;
import com.sunrun.exception.NameAlreadyExistException;
import com.sunrun.exception.NotFindGroupException;
import com.sunrun.vo.GroupName;

import java.util.List;

public interface GroupService {
    Group save(Group group, Integer domainId) throws NameAlreadyExistException;

    void addUserToGroup(String userName, String groupName,boolean isRemote) throws NotFindGroupException;

    void deleteGroup(String groupName) throws NotFindGroupException;

    List<String> findAllUserGroupList(String userName);

    void removeUserFromGroups(String userName, List<String> groupNames) throws NotFindGroupException;

    Group findGroup(String groupName);

    boolean updateGroup(Group group) throws NotFindGroupException;

    List<GroupName> findAll(Integer domainId);

    void removeUsersFromGroup(String groupName, List<String> userNames) throws NotFindGroupException;
}
