package com.sunrun.service;

import com.sunrun.entity.Org;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface OrgService {
    List<Org> findByDomainIdAndParentId(Integer domainId, Long parentId, Sort sort);
}
