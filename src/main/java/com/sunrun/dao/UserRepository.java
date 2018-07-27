package com.sunrun.dao;

import com.sunrun.entity.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@CacheConfig(cacheNames = "user")
public interface UserRepository extends JpaRepository<User,Long> {

    @Cacheable
    User findUserByUserNameAndUserPassword(String username, String userPassword);

    List<User> findByOrgId(Long orgId);

    int deleteByDomainId(Long domainId);

    List<User> findByDomainId(Long domainId);

    @Query(value = "SELECT COUNT(1) FROM tbuser where domainId = :domainId",nativeQuery = true)
    int selectCountByDomainId(@Param("domainId") Long domainId);
    @Modifying
    @Query(value = "DELETE FROM tbuser where orgId = ?1")
    int deleteByOrgId(Long orgId);
}
