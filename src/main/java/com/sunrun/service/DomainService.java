package com.sunrun.service;

import com.sunrun.entity.Domain;
import com.sunrun.exception.NotFindDomainException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DomainService {

    Page<Domain> findAll(Pageable pageable);

    Domain findByName(String domainName) throws NotFindDomainException;

    Domain findById(Integer domainId);
}
