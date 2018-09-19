package com.sunrun.dao;

import com.sunrun.entity.GroupProp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupPropRepository extends JpaRepository<GroupProp,String>{

    List<GroupProp> findByDomainId(Integer domainId);
}
