package com.sunrun.controller;

import com.google.gson.Gson;
import com.sunrun.common.notice.ReturnData;
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

import static org.junit.Assert.*;
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MucRoomControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Test
    public void save() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("roomUser","yuanyong@sunrun");
        params.add("roomName","test2");
        params.add("naturalName","dds的");
        params.add("description","hahah");
        params.add("members","user2@sunrun,zhaoyi@sunrun");
        params.add("admins","user1@sunrun,user2@sunrun");
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
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("roomName","test");
        params.add("roles","members");
        params.add("name","user13@sunrun");
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

    @Test
    public void removeMember() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("roomName","demo2222");
        params.add("roles","members");
        params.add("name","user13@sunrun");
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
}