package com.sunrun.dao;

import com.sunrun.entity.Org;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;

@CacheConfig(cacheNames = "user")
public interface OrgRepository extends JpaRepository<Org,Long> {

}
