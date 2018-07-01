package com.sunrun.dao;

import com.sunrun.entity.Domain;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@CacheConfig(cacheNames = "user")
public interface DomainRepository extends JpaRepository<Domain,Integer> {
    @Cacheable
    List<Domain> findAll();
}
