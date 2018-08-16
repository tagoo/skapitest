package com.sunrun.dao;

import com.sunrun.entity.Org;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@CacheConfig(cacheNames = "user")
public interface OrgRepository extends JpaRepository<Org,Long> {

    int deleteByDomainId(Integer domainId);

    @Query(value = "SELECT COUNT(1) FROM tborg where domainId = ?1",nativeQuery = true)
    int selectCountByDomainId(Integer domainId);

    List<Org> findByDomainId(Integer id);

    List<Org> findByDomainIdAndParentId(Integer domainId, Long parentId);
}
