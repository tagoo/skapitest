package com.sunrun.dao;

import com.sunrun.entity.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

@CacheConfig(cacheNames = "user")
public interface UserRepository extends JpaRepository<User,Long> {

    @Cacheable
    User findUserByUserNameAndUserPassword(String username, String userPassword);
}
