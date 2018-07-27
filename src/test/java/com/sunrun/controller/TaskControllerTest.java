package com.sunrun.controller;

import com.google.gson.Gson;
import com.sunrun.common.notice.ReturnData;
import com.sunrun.entity.Property;
import com.sunrun.entity.TaskFile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc()
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Test
    public void delete() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("userName","zhaoyi");
        MvcResult mvcResult = null;
        Gson gson = new Gson();
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/task/delete/19e198ca-35b8-461a-8ab3-7faa42c487c8").params(params))
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
    public void update() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("userName","yuanyong");
        params.add("taskJID","1fc34c6c-ccd2-4331-802b-e2ddfe5bc112");
        params.add("taskDescription","抓贼");
        params.add("taskName","抓偷心贼");
        MvcResult mvcResult = null;
        Gson gson = new Gson();
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/task/update").params(params))
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
    public void addEvent() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("userName","yuanyong");
        params.add("taskJID","1fc34c6c-ccd2-4331-802b-e2ddfe5bc112");
        params.add("content","准备集合");
        params.add("location","广州");
        MvcResult mvcResult = null;
        Gson gson = new Gson();
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/task/event/add").params(params))
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
    public void deleteEvent() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("userName","yuanyong@sunrun");
        MvcResult mvcResult = null;
        Gson gson = new Gson();
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/task/event/2").params(params))
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
    public void findEvents() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        MvcResult mvcResult = null;
        Gson gson = new Gson();
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/task/event/1fc34c6c-ccd2-4331-802b-e2ddfe5bc112").params(params))
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
    public void updateEvent() {
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("id","1");
        params.add("content","开始行动");
        params.add("userName","zhaoyi@sunrun");
        params.add("location","深圳");
        MvcResult mvcResult = null;
        Gson gson = new Gson();
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/task/event/update").params(params))
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
    public void saveFile() {
        StringBuffer sb = new StringBuffer("?");
        sb.append("taskJID=1fc34c6c-ccd2-4331-802b-e2ddfe5bc112").append("&userName=zhaoyi@sunrun");
        sb.append("&fileData[0].fileName=大笨猪.png").append("&fileData[1].fileName=密码.txt").append("&fileData[2].fileName=mm.doc");
        MvcResult mvcResult = null;
        Gson gson = new Gson();
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/task/file/add"+sb.toString()))
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
    public void deleteFiles() {
        StringBuffer sb = new StringBuffer("?");
        sb.append("taskJID=1fc34c6c-ccd2-4331-802b-e2ddfe5bc112").append("&userName=zhaoyi@sunrun");
        sb.append("&ids=5&ids=4");
        MvcResult mvcResult = null;
        Gson gson = new Gson();
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/task/file"+sb.toString()))
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
    public void findFiles() {
        MvcResult mvcResult = null;
        Gson gson = new Gson();
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/task/file/1fc34c6c-ccd2-4331-802b-e2ddfe5bc112?page=1&size=3"))
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
    public void findTasks() {
        MvcResult mvcResult = null;
        Gson gson = new Gson();
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/task/room/test@sunrun.sunrun"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            ReturnData returnData = gson.fromJson(mvcResult.getResponse().getContentAsString(), ReturnData.class);
            System.out.println(mvcResult.getResponse().getContentAsString());
            Assert.assertTrue(returnData.isSuccess());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}