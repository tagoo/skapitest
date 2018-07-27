package com.sunrun.listener;

public class TaskMessageEvent {
    private String roomJID;
    private String userName;
    private String taskJID;
    private String taskName;
    private String content;

    public String getRoomJID() {
        return roomJID;
    }

    public void setRoomJID(String roomJID) {
        this.roomJID = roomJID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTaskJID() {
        return taskJID;
    }

    public void setTaskJID(String taskJID) {
        this.taskJID = taskJID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "TaskMessageEvent{" +
                "roomJID='" + roomJID + '\'' +
                ", userName='" + userName + '\'' +
                ", taskJID='" + taskJID + '\'' +
                ", taskName='" + taskName + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
