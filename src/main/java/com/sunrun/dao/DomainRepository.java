package com.sunrun.dao;

import com.sunrun.entity.Domain;
import org.hibernate.annotations.Cache;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface DomainRepository extends JpaRepository<Domain,Integer> {
    Domain findByName(String domainName);
}
