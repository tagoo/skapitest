package com.sunrun.service.impl;

import com.google.gson.Gson;
import com.sunrun.common.Constant;
import com.sunrun.common.config.IamConfig;
import com.sunrun.dao.UserRepository;
import com.sunrun.entity.User;
import com.sunrun.exception.*;
import com.sunrun.po.UserPo;
import com.sunrun.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceImplTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IamConfig iamConfig;
    @Test
    public void loginByUser() {
        redisTemplate.opsForValue().set(Constant.SYNCHRONIZATION_MARK,"true",1, TimeUnit.HOURS);
        Boolean aBoolean = redisTemplate.hasKey(Constant.SYNCHRONIZATION_MARK);
        Map<String,String> map = restTemplate.getForEntity("https://192.168.0.180:9531/iam/sso/login?service=" + iamConfig.getService(), HashMap.class).getBody();
        MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
        params.add("lt",map.get("lt"));
        params.add("service", iamConfig.getService());
        params.add("username", "yuanyong");
        params.add("password", "123456");
        params.add("domain", "sunrun");
        params.add("login_type", "1");
        Map<String,String> hashMap = restTemplate.postForObject("https://192.168.0.180:9531/iam/sso/login", params, HashMap.class);
        System.out.println(hashMap);
        User user = new User();
        user.setUserName("test");
        user.setUserPassword("123456");
        try {
            User u = userService.loginByUser(null,hashMap.get("st"));
            Assert.assertEquals(16390L,u.getId().longValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void updateUserList() {
        try {
            try {
                try {
                    Assert.assertEquals(true,userService.updateUserList());
                } catch (CannotFindDomain e) {
                    e.printStackTrace();
                } catch (GetUserException e) {
                    e.printStackTrace();
                } catch (SyncAlreadyRunningException e) {
                    e.printStackTrace();
                }
            } catch (NotFindMucServiceException e) {
                e.printStackTrace();
            } catch (SyncOrgException e) {
                e.printStackTrace();
            }
        } catch (IamConnectionException e) {
            e.printStackTrace();
        }
    }
}