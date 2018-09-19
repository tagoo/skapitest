package com.sunrun.service.impl;

import com.sunrun.dao.MucServiceRepository;
import com.sunrun.entity.MucService;
import com.sunrun.service.MucServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MucServiceServiceImpl implements MucServiceService {
    @Autowired
    private MucServiceRepository mucServiceRepository;
    @Override
    public MucService findById(String name) {
        return mucServiceRepository.getOne(name);
    }
}
