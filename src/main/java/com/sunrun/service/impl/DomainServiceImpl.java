package com.sunrun.service.impl;

import com.sunrun.dao.DomainRepository;
import com.sunrun.entity.Domain;
import com.sunrun.service.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DomainServiceImpl implements DomainService {
    @Autowired
    private DomainRepository domainRepository;
    @Override
    public Page<Domain> findAll(Pageable pageable) {
        return domainRepository.findAll(pageable);
    }
}
