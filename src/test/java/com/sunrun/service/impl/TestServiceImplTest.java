package com.sunrun.service.impl;

import com.sunrun.service.TestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestServiceImplTest {
    @Autowired
    private TestService testService;
    @Test
    public void login() {
        testService.login();
    }
}