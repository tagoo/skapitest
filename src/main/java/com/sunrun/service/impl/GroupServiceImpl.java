package com.sunrun.service.impl;

import com.sunrun.dao.GroupPropRepository;
import com.sunrun.dao.GroupUserRepository;
import com.sunrun.entity.Group;
import com.sunrun.entity.GroupProp;
import com.sunrun.entity.GroupUser;
import com.sunrun.exception.NameAlreadyExistException;
import com.sunrun.exception.NotFindGroupException;
import com.sunrun.po.GroupData;
import com.sunrun.service.GroupService;
import com.sunrun.utils.RestApiUtil;
import com.sunrun.vo.GroupName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@CacheConfig(cacheNames = "group")
public class GroupServiceImpl implements GroupService {
    @Resource
    private RestApiUtil restApiUtil;
    @Autowired
    private GroupUserRepository groupUserRepository;
    @Autowired
    private GroupPropRepository groupPropRepository;

    @Override
    @CachePut(key = "#group.name")
    public Group save(Group group, Integer domainId) throws NameAlreadyExistException {
        Group source = restApiUtil.getGroup(group.getName());
        if (null != source) {
            throw new NameAlreadyExistException();
        }
        if (restApiUtil.createGroup(group)) {
            if (null != domainId) {
                groupPropRepository.save(new GroupProp(group.getName(),domainId));
            }
            GroupData data = restApiUtil.getGroup(group.getName());
            data.setDomainId(domainId);
            return data;
        } else {
            throw new RuntimeException("Failed create a new group(" + group.getName()+")");
        }
    }

    @Override
    @CacheEvict(key = "#groupName",beforeInvocation = true)
    public void addUserToGroup(String userName, String groupName, boolean isRemote) throws NotFindGroupException {
        Group source = restApiUtil.getGroup(groupName);
        if (null == source) {
            throw new NotFindGroupException();
        }
        if (isRemote) {
            if (restApiUtil.addUserToGroup(userName,groupName)) {
                log.info("Add {} to the group({})",userName, groupName);
            } else {
                log.warn("Failed to add {} to the group({})",userName, groupName);
            }
        } else {
            groupUserRepository.saveAndFlush(new GroupUser(groupName,userName));
        }
    }

    @Override
    @CacheEvict(key = "#groupName")
    public void deleteGroup(String groupName) throws NotFindGroupException {
        Group group = restApiUtil.getGroup(groupName);
        if (null == group) {
            throw new NotFindGroupException();
        }
        groupPropRepository.deleteById(groupName);
        restApiUtil.deleteGroup(groupName);
    }

    @Override
    public List<String> findAllUserGroupList(String userName) {
        return restApiUtil.getUserGroups(userName);
    }

    @Override
    @CacheEvict(allEntries = true, beforeInvocation = true)
    public void removeUserFromGroups(String userName, List<String> groupNames) throws NotFindGroupException {
        for (String groupName: groupNames) {
            GroupData group = restApiUtil.getGroup(groupName);
            if (null == group) {
                throw new NotFindGroupException();
            }
        }
        try {
            restApiUtil.removeUserFromGroup(userName,groupNames);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to remove %s from %s",userName,groupNames));
        }
    }

    @Override
    @CacheEvict(key = "#groupName")
    public void removeUsersFromGroup(String groupName, List<String> userNames) throws NotFindGroupException {
        GroupData group = restApiUtil.getGroup(groupName);
        if (null == group) {
            throw new NotFindGroupException();
        }
        List<String> list = new ArrayList<>(1);
        list.add(groupName);
        for (String userName: userNames) {
            try {
                restApiUtil.removeUserFromGroup(userName,list);
            } catch (Exception e) {
                throw new RuntimeException(String.format("Failed to remove %s from %s",userName,list));
            }
        }
    }

    @Override
    @Cacheable(key = "#groupName")
    public Group findGroup(String groupName) {
        Optional<GroupProp> op = groupPropRepository.findById(groupName);
        GroupData group = restApiUtil.getGroup(groupName);
        if (group != null && op.isPresent()) {
            group.setDomainId(op.get().getDomainId());
        }
        return group;
    }

    @Override
    @CacheEvict(key = "#source.name")
    public boolean updateGroup(Group source) throws NotFindGroupException {
        Group group = restApiUtil.getGroup(source.getName());
        if (group == null) {
            throw new NotFindGroupException();
        }
        return restApiUtil.updateGroup(source);
    }

    @Override
    public List<GroupName> findAll(Integer domainId) {
        if (null != domainId) {
            List<GroupProp> list = groupPropRepository.findByDomainId(domainId);
            return list.stream().map(u -> new GroupName(u.getGroupName())).collect(Collectors.toList());
        }
        return restApiUtil.getAllGroup();
    }
}
