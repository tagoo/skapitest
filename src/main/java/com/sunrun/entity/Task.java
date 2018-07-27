package com.sunrun.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbTask")
public class Task implements Serializable {
    private static final long serialVersionUID = 7517145216861238371L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "roomJID")
    private String roomJID;
    @Column(name = "taskJID",unique = true,nullable = false)
    private String taskJID;
    @Column(name = "taskName",nullable = false)
    private String taskName;
    @Column(name = "taskDescription")
    private String taskDescription;
    @Column(name = "userName")
    private String userName;
    @Column(name = "createTime")
    private LocalDateTime createTime;
    private Integer status;

    public Task() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoomJID() {
        return roomJID;
    }

    public void setRoomJID(String roomJID) {
        this.roomJID = roomJID;
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

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
