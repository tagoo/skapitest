package com.sunrun.service;

import com.sunrun.entity.Domain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DomainService {

    Page<Domain> findAll(Pageable pageable);
}
