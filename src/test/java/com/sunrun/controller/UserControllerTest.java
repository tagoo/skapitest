package com.sunrun.controller;
import com.google.gson.Gson;
import com.sunrun.common.notice.ReturnData;
import com.sunrun.utils.helper.Property;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void login() throws Exception {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("userName","test");
        params.add("userPassword","123456");
        for (int i = 0 ;i < 2; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/user/login").params(params))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            System.out.println(mvcResult.getResponse().getContentAsString());
        }

    }

    @Test
    public void getUser() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
       /* params.add("userName","test");
        params.add("userPassword","123456");*/
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/user/demo_1").params(params))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            Gson gson = new Gson();
            ReturnData returnData = gson.fromJson(mvcResult.getResponse().getContentAsString(), ReturnData.class);
            System.out.println(returnData);
            Assert.assertTrue(returnData.isSuccess());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createUser() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("username","mm11");
        params.add("email","123456@sin.com");
        params.add("name","小新");
        params.add("password","123456");
        ArrayList<Property> properties = new ArrayList<>();
        properties.add(new Property("orgId","1"));
        properties.add(new Property("url","https://www.xxxx.com"));
        Gson gson = new Gson();
        params.add("property",gson.toJson(properties));
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/user/save").params(params))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            ReturnData returnData = gson.fromJson(mvcResult.getResponse().getContentAsString(), ReturnData.class);
            System.out.println(mvcResult.getResponse().getContentAsString());
            Assert.assertTrue(returnData.isSuccess());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateUser() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("username","demo");
        params.add("email","123456@sina.com");
        params.add("name","小新test");
        params.add("password","1234567");
        ArrayList<Property> properties = new ArrayList<>();
        properties.add(new Property("orgId","2"));
        properties.add(new Property("domainId","1"));
        properties.add(new Property("orgName","测试部"));
        Gson gson = new Gson();
        params.add("property",gson.toJson(properties));
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/user/update").params(params))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            ReturnData returnData = gson.fromJson(mvcResult.getResponse().getContentAsString(), ReturnData.class);
            System.out.println(mvcResult.getResponse().getContentAsString());
            Assert.assertTrue(returnData.isSuccess());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteUser() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/user/delete/demo").params(params))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            Gson gson = new Gson();
            ReturnData returnData = gson.fromJson(mvcResult.getResponse().getContentAsString(), ReturnData.class);
            System.out.println(mvcResult.getResponse().getContentAsString());
            Assert.assertTrue(returnData.isSuccess());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void updateUser1() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/user/synchronization").params(params))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            Gson gson = new Gson();
            ReturnData returnData = gson.fromJson(mvcResult.getResponse().getContentAsString(), ReturnData.class);
            System.out.println(mvcResult.getResponse().getContentAsString());
            Assert.assertTrue(returnData.isSuccess());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}