package com.sunrun;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SkapiApplicationTests {

	@Test
	public void contextLoads() {
	}

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Before // 在测试开始前初始化工作
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testAble() throws Exception {
		MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
		params.add("userName","test");
		params.add("userPassword","123456");
		for (int i = 0; i < 2; i++) {
			MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/user/login").params(params))
					.andExpect(MockMvcResultMatchers.status().isOk())// 模拟向testRest发送get请求
					.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;
					// charset=UTF-8
					.andReturn();// 返回执行请求的结果

			System.out.println(result.getResponse().getContentAsString());
		}
		System.out.println("删除缓存");
		/*MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/user/remove").param("userName","test"))
				.andExpect(MockMvcResultMatchers.status().isOk())// 模拟向testRest发送get请求
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;
				// charset=UTF-8
				.andReturn();// 返回执行请求的结果
		System.out.println(result.getResponse().getContentAsString());*/
		System.out.println("删除缓存后");
		MvcResult result1 = mockMvc.perform(MockMvcRequestBuilders.post("/user/login").params(params))
				.andExpect(MockMvcResultMatchers.status().isOk())// 模拟向testRest发送get请求
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;
				// charset=UTF-8
				.andReturn();// 返回执行请求的结果
		System.out.println(result1.getResponse().getContentAsString());

	}
}
