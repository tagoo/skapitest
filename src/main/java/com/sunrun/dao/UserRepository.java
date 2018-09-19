package com.sunrun.dao;

import com.sunrun.entity.User;
import com.sunrun.po.UserPo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@CacheConfig(cacheNames = "user")
public interface UserRepository extends JpaRepository<User,Long>,JpaSpecificationExecutor<User>{

    @Cacheable
    User findUserByUserNameAndUserPasswordAndRole(String username, String userPassword, User.Role role);

    @Query(value = "SELECT * FROM tbuser WHERE orgId=?1",nativeQuery = true)
    List<User> findByOrgId(Long orgId);

    int deleteByDomainId(Integer domainId);

    @Query(value = "SELECT userName,userRealName FROM tbuser WHERE domainId=?1 order by userName asc")
    List<User> findByDomainId(Integer domainId);

    @Query(value = "SELECT userName FROM tbuser WHERE domainId=?1",countQuery = "SELECT count(userName) FROM tbuser WHERE domainId=?1")
    Page<String> findNameByDomainId(Integer domainId,Pageable pageable);

    @Query(value = "SELECT COUNT(1) FROM tbuser where domainId = :domainId",nativeQuery = true)
    int selectCountByDomainId(@Param("domainId") Integer domainId);
    @Modifying
    @Query(value = "DELETE FROM tbuser where orgId = ?1")
    int deleteByOrgId(Long orgId);

    @Query(value = "SELECT * FROM tbuser WHERE orgId=?1",countQuery = "SELECT count(1) FROM tbuser WHERE orgId=?1",nativeQuery = true)
    Page<User> findByOrgId(Long orgId, Pageable pageable);

    @Query(value = "SELECT userName,userRealName FROM tbuser WHERE domainId=?1 and (userName like concat('%',?2,'%') or  userRealName like concat('%',?2,'%')) order by userName asc")
    List<User> findByDomainIdAndCondition(Integer domainId, String search);

    @Query(value = "SELECT COUNT(1) FROM tbuser where domainId = ?1 AND userName = ?2",nativeQuery = true)
    int selectCountByDomainIdAndUserName(Integer domainId, String userName);
    @Query(value = "SELECT u.*,r.name as orgName FROM tbuser u JOIN tborg r ON u.orgId = r.orgId WHERE u.domainId = ?1 AND (u.userName LIKE %?2% OR u.userRealName like %?2%)",countQuery =
            "SELECT count(*) FROM tbuser u JOIN tborg r ON u.orgId = r.orgId WHERE u.domainId = ?1 AND (u.userName LIKE %?2% OR u.userRealName like %?2%)",nativeQuery = true)
    Page<UserPo> findByDomainIdAndUserNameOrUserRealNameLike(int domainId, String search, Pageable pageable);

    User findBySourceIdAndDomainId(Long sourceId, Integer domainId);

    User findByDomainIdAndUserName(Integer domainId, String userName);
}
