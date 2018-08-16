package com.sunrun.service.impl;

import com.sunrun.dao.OrgRepository;
import com.sunrun.entity.Org;
import com.sunrun.service.OrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrgServiceImpl implements OrgService {
    @Autowired
    private OrgRepository orgRepository;
    @Override
    public List<Org> findByDomainIdAndParentId(Integer domainId, Long parentId) {
        List<Org> orgData = orgRepository.findByDomainId(domainId);
        List<Org> orgList = orgRepository.findByDomainIdAndParentId(domainId, parentId);
        Set<Long> dictionary = orgData.stream().map(o -> o.getParentId()).collect(Collectors.toSet());
        orgList.forEach(o -> o.setHasChild(dictionary.contains(o.getOrgId())));
        return orgList;
    }
}
