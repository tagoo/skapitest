package com.sunrun.controller;

import com.google.gson.Gson;
import com.sunrun.common.notice.ReturnData;
import com.sunrun.dao.UserRepository;
import com.sunrun.entity.User;
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

import java.util.List;

import static org.junit.Assert.*;
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MucRoomControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Test
    public void save() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("roomUser","yuanyong@sunrun");
        params.add("roomName","test1");
        params.add("naturalName","测试");
        params.add("description","hahah");
        params.add("members","use2@sunrun");
        params.add("admins","zhaoyi@sunrun");
        params.add("serviceName","sunrun");
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/room/save").params(params))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            Gson gson = new Gson();
            ReturnData returnData = gson.fromJson(mvcResult.getResponse().getContentAsString(), ReturnData.class);
            Assert.assertTrue(returnData.isSuccess());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void update() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("roomName","demo2222");
        params.add("naturalName","实例1111");
        params.add("description","hahah");
        params.add("admins","user58888@sunrun,user6@sunrun");
        params.add("owners","yuanyong@sunrun");
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/room/update").params(params))
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
    public void addMember() {
        List<User> byDomainId = userRepository.findByDomainId(1);
        for (User u: byDomainId) {
            MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
            params.add("roomName","test");
            params.add("roles","members");
            params.add("name",u.getUserName());
            params.add("serviceName","sunrun");
            MvcResult mvcResult = null;
            try {
                mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/room/member/add").params(params))
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

    }

    @Test
    public void removeMember() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("roomName","test");
        params.add("roles","members");
        params.add("name","demo2@sunrun");
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/room/member/remove").params(params))
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
    public void delete() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("roomName","demo2222");
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/room/member/destroy").params(params))
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
    public void getAllChatRooms() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("pageNum","1");
        params.add("pageSize","1");
        params.add("search","test");
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/room/query").params(params))
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
    public void addGroupRoleToChatRoom() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("roomName","test");
        params.add("name","哈哈");
        params.add("roles","admins");
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/room/group/role").params(params))
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
    public void getOwnerChatRooms() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("pageSize","2");
        params.add("pageNum","1");
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/room/ownquery/yuanyong@sunrun").params(params))
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
    public void updateNickName() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        MvcResult mvcResult = null;
        params.add("nickName","我是谁");
        params.add("roomID","6");
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/room/member/update/zhaoyi@sunrun1").params(params))
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
    public void getChatRoom() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/room/test").params(params))
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