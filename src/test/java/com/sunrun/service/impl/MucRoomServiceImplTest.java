package com.sunrun.service.impl;

import com.sunrun.exception.NameAlreadyExistException;
import com.sunrun.exception.NotFindRoomException;
import com.sunrun.service.MucRoomService;
import com.sunrun.utils.helper.ChatRoom;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MucRoomServiceImplTest {
    @Autowired
    private MucRoomService mucRoomService;
    @Test
    public void save() {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setDescription("test测试1");
        chatRoom.setRoomName("test");
        chatRoom.setNaturalName("测试112");
        chatRoom.setOwners(Arrays.asList("admin@sunrun"));
        chatRoom.setMembers(Arrays.asList("demo2@sunrun"));
        chatRoom.setAdmins(Arrays.asList("demo1@sunrun"));
        chatRoom.setDefaultProp();
        try {
            Assert.assertNotNull(mucRoomService.save(chatRoom,"chat"));
        } catch (NameAlreadyExistException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void update() {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setDescription("哈啊哈");
        chatRoom.setRoomName("testroom2dd2111");
        chatRoom.setNaturalName("哈哈哈");
        chatRoom.setOwners(Arrays.asList("zhaoyi@sunrun"));
        chatRoom.setAdmins(Arrays.asList("user3@sunrun","user4@sunrun"));
        try {
            Assert.assertTrue(mucRoomService.update(chatRoom,"conference"));
        } catch (NotFindRoomException e) {
            e.printStackTrace();
        }
    }
}