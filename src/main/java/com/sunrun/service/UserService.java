package com.sunrun.service;

import com.sunrun.entity.Roster;
import com.sunrun.entity.User;
import com.sunrun.exception.*;
import com.sunrun.utils.helper.UserData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface UserService {
    User loginByUser(User user, String serviceTicket) throws NotFindUserException, OpenfireLoginFailureException;

    User save(User user);

    int remove(User user);

    List<Roster> getFriendList(String userName);

    boolean updateUserList() throws IamConnectionException, NotFindMucServiceException, SyncOrgException, CannotFindDomain, GetUserException, SyncAlreadyRunningException;

    UserData getUser(String userName);

    UserData createUser(UserData userData) throws NameAlreadyExistException;

    boolean updateUser(UserData userData) throws NotFindUserException;

    boolean delete(String userName)throws NotFindUserException;

    User loginByUser(User user, HttpSession session) throws NotFindUserException;

    Page<User> getUserListByDomainId(Integer domainId, Long orgId, String search, Pageable pageable);

    Page<User> getUserListByOrgId(Long orgId, Pageable pageable);

    List<User> getUserListByDomainId(Integer domainId, String search);
}
