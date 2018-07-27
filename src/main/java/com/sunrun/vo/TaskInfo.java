package com.sunrun.vo;

import com.sunrun.entity.Task;
import com.sunrun.entity.TaskEvent;
import com.sunrun.entity.TaskFile;
import com.sunrun.listener.TaskListener;
import com.sunrun.listener.TaskMessageEvent;

import java.util.List;
import java.util.stream.Collectors;

public class TaskInfo {
    private Task taskData;
    private List<TaskFile> fileData;
    private TaskEvent eventData;
    private TaskListener taskListener;

    public void addListener(TaskListener listener) {
        this.taskListener = listener;
    }
    public void removeListener() {
        this.taskListener = null;
    }

    public Task getTaskData() {
        return taskData;
    }
    public void operate(){
        if (taskListener != null) {
            this.taskListener.process(getTaskMessageEvent());
        }
    }

    private TaskMessageEvent getTaskMessageEvent() {
        TaskMessageEvent event = new TaskMessageEvent();
        if (taskData != null) {
            event.setRoomJID(taskData.getRoomJID());
            event.setTaskJID(taskData.getTaskJID());
            event.setTaskName(taskData.getTaskName());
            event.setUserName(taskData.getUserName());
        }
        if (eventData != null) {
            if (event.getUserName() == null) {
                event.setUserName(eventData.getUserName());
            }
            if (event.getTaskJID() == null) {
                event.setTaskJID(eventData.getTaskJID());
            }
            event.setContent(eventData.getContent());
        }
        if (fileData != null && !fileData.isEmpty()) {
            List<String> fileNames = fileData.stream().map(u -> u.getFileName()).collect(Collectors.toList());
            event.setContent(fileNames.toString());
        }
        return event;
    }

    public void setTaskData(Task taskData) {
        this.taskData = taskData;
    }

    public List<TaskFile> getFileData() {
        return fileData;
    }

    public void setFileData(List<TaskFile> fileData) {
        this.fileData = fileData;
    }

    public TaskEvent getEventData() {
        return eventData;
    }

    public void setEventData(TaskEvent eventData) {
        this.eventData = eventData;
    }



}
