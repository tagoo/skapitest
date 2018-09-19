package com.sunrun.dao;

import com.sunrun.entity.GroupUser;
import com.sunrun.entity.model.GroupUserKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupUserRepository extends JpaRepository<GroupUser,GroupUserKey> {
}
