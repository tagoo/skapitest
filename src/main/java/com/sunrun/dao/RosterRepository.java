package com.sunrun.dao;

import com.sunrun.entity.Roster;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
@CacheConfig(cacheNames = "roster")
public interface RosterRepository extends JpaRepository<Roster,Integer> {
    @Cacheable
    @Query()
    List<Roster> findAllByUsername(String userName);
}
