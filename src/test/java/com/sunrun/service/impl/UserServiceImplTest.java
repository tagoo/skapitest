package com.sunrun.service.impl;

import com.sunrun.common.config.IamConfig;
import com.sunrun.entity.User;
import com.sunrun.exception.IamConnectionException;
import com.sunrun.exception.NotFindUserException;
import com.sunrun.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceImplTest {
    @Autowired
    private UserService userService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private IamConfig iamConfig;
    @Test
    public void loginByUser() {
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
            User u = userService.loginByUser(user,hashMap.get("st"));
            Assert.assertEquals(16390L,u.getId().longValue());
        } catch (NotFindUserException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void updateUserList() {
        try {
            Assert.assertEquals(true,userService.updateUserList());
        } catch (IamConnectionException e) {
            e.printStackTrace();
        }
    }
}