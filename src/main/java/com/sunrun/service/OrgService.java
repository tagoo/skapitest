package com.sunrun.service;

import com.sunrun.entity.Org;

import java.util.List;

public interface OrgService {
    List<Org> findByDomainIdAndParentId(Integer domainId, Long parentId);
}
