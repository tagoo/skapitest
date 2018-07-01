package com.sunrun.controller;

import com.sunrun.common.notice.ReturnCode;
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
}