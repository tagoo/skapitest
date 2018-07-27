package com.sunrun.service.impl;

import com.sunrun.entity.Task;
import com.sunrun.entity.TaskEvent;
import com.sunrun.service.TaskService;
import com.sunrun.vo.TaskInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.*;
@SpringBootTest
@RunWith(SpringRunner.class)
public class TaskServiceImplTest {
    @Autowired
    private TaskService taskService;
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void save() {
        TaskInfo taskInfo = new TaskInfo();
        TaskEvent taskEvent = new TaskEvent();
        taskEvent.setUserName("zhaoyi@sunrun");
        taskEvent.setContent("通知大家");
        taskEvent.setTaskJID(UUID.randomUUID().toString());
        Task task = new Task();
        task.setRoomJID("test@sunrun.sunrun");
        task.setUserName(taskEvent.getUserName());
        task.setTaskJID(taskEvent.getTaskJID());
        task.setStatus(1);
        task.setTaskName("抓小偷heheh");
        taskInfo.setTaskData(task);
        taskInfo.setEventData(taskEvent);
        taskService.save(taskInfo);
    }
}