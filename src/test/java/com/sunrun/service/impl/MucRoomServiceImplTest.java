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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
@SpringBootTest
@RunWith(SpringRunner.class)
public class MucRoomServiceImplTest {
    @Autowired
    private MucRoomService mucRoomService;
    @Test
    public void save() {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setDescription("test测试1");
        chatRoom.setRoomName("testroom2dd2111");
        chatRoom.setNaturalName("测试112");
        chatRoom.setOwners(Arrays.asList("yuanyong@sunrun"));
        chatRoom.setAdmins(Arrays.asList("user1@sunrun","user2@sunrun"));
        chatRoom.setOutcasts(Arrays.asList("user19@sunrun","user18@sunrun"));
        try {
            Assert.assertNotNull(mucRoomService.save(chatRoom,"conference"));
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