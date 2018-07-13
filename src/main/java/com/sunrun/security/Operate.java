package com.sunrun.security;

import com.sunrun.entity.Domain;
import com.sunrun.exception.CannotFindDomain;
import com.sunrun.security.sync.Synchronize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface Operate extends Validate,Synchronize {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    boolean deleteDomainResource(List<Domain> save) throws CannotFindDomain;
}
