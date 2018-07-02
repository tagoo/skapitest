package com.sunrun.service;

import com.sunrun.entity.Roster;
import com.sunrun.entity.User;
import com.sunrun.exception.IamConnectionException;
import com.sunrun.exception.NameAlreadyExistException;
import com.sunrun.exception.NotFindUserException;
import com.sunrun.utils.helper.UserData;

import java.util.List;

public interface UserService {
    User loginByUser(User user, String serviceTicket) throws NotFindUserException;

    User save(User user);

    int remove(User user);

    List<Roster> getFriendList(String userName);

    boolean updateUserList() throws IamConnectionException;

    UserData getUser(String userName);

    UserData createUser(UserData userData) throws NameAlreadyExistException;

    boolean updateUser(UserData userData) throws NotFindUserException;

    boolean delete(String userName)throws NotFindUserException;
}
