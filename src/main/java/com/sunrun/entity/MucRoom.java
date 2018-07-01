package com.sunrun.entity;

import com.sunrun.entity.model.MucRoomKey;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "ofmucroom")
@IdClass(MucRoomKey.class)
public class MucRoom implements Serializable{
    private static final long serialVersionUID = 7658873639808849725L;
    @Column(name = "serviceID")
    private Long serviceID;
    @Column(name = "name")
    private String name;// 群唯一标识
    /*@GeneratedValue(generator = "snowflakeId")
    @GenericGenerator(name = "snowflakeId",strategy = "com.sunrun.utils.IDGenerator")*/
    private Long roomID;// 房间ID
    private String creationDate;// 创建日期
    private String modificationDate;// 最近修改日期
    private String naturalName;// 群名称
    private String description;// 群简介
    private String subject;// 群主题
    private String lockedDate;// 锁定日期
    private String emptyDate;// 清空日期
    private short canChangeSubject;// 允许修改主题
    private Integer maxUsers = 1000;// 群最大人数
    private short publicRoom = 1;// 公共房间
    private short moderated = 0;// 房间是适度的
    private short membersOnly = 0;// 房间仅对成员开放
    private short canInvite = 1;// 允许占有者邀请其他人
    private String roomPassword = "";// 群密码
    private short canDiscoverJid = 1;// 能够发现占有者真实 JID的角色
    private short logEnabled = 1;// 登录房间对话
    private short rolesToBroadcast = 7;// 可以广播的角色
    private short useReservedNick = 1;// 仅允许注册的昵称登录
    private short canChangeNick = 1;// 允许使用者修改昵称
    private short canRegister = 1;// 允许用户注册房间

    public MucRoom() {
    }

    @Id
    public Long getServiceID() {
        return serviceID;
    }
    @Id
    public String getName() {
        return name;
    }

    public void setServiceID(Long serviceID) {
        this.serviceID = serviceID;
    }

    public Long getRoomID() {
        return roomID;
    }

    public void setRoomID(Long roomID) {
        this.roomID = roomID;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNaturalName() {
        return naturalName;
    }

    public void setNaturalName(String naturalName) {
        this.naturalName = naturalName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLockedDate() {
        return lockedDate;
    }

    public void setLockedDate(String lockedDate) {
        this.lockedDate = lockedDate;
    }

    public String getEmptyDate() {
        return emptyDate;
    }

    public void setEmptyDate(String emptyDate) {
        this.emptyDate = emptyDate;
    }

    public short getCanChangeSubject() {
        return canChangeSubject;
    }

    public void setCanChangeSubject(short canChangeSubject) {
        this.canChangeSubject = canChangeSubject;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }

    public short getPublicRoom() {
        return publicRoom;
    }

    public void setPublicRoom(short publicRoom) {
        this.publicRoom = publicRoom;
    }

    public short getModerated() {
        return moderated;
    }

    public void setModerated(short moderated) {
        this.moderated = moderated;
    }

    public short getMembersOnly() {
        return membersOnly;
    }

    public void setMembersOnly(short membersOnly) {
        this.membersOnly = membersOnly;
    }

    public short getCanInvite() {
        return canInvite;
    }

    public void setCanInvite(short canInvite) {
        this.canInvite = canInvite;
    }

    public String getRoomPassword() {
        return roomPassword;
    }

    public void setRoomPassword(String roomPassword) {
        this.roomPassword = roomPassword;
    }

    public short getCanDiscoverJid() {
        return canDiscoverJid;
    }

    public void setCanDiscoverJid(short canDiscoverJid) {
        this.canDiscoverJid = canDiscoverJid;
    }

    public short getLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(short logEnabled) {
        this.logEnabled = logEnabled;
    }

    public short getRolesToBroadcast() {
        return rolesToBroadcast;
    }

    public void setRolesToBroadcast(short rolesToBroadcast) {
        this.rolesToBroadcast = rolesToBroadcast;
    }

    public short getUseReservedNick() {
        return useReservedNick;
    }

    public void setUseReservedNick(short useReservedNick) {
        this.useReservedNick = useReservedNick;
    }

    public short getCanChangeNick() {
        return canChangeNick;
    }

    public void setCanChangeNick(short canChangeNick) {
        this.canChangeNick = canChangeNick;
    }

    public short getCanRegister() {
        return canRegister;
    }

    public void setCanRegister(short canRegister) {
        this.canRegister = canRegister;
    }

    public void setDefaultProp() {
        this.maxUsers = 1000;// 群最大人数
        this.publicRoom = 1;// 公共房间
        this.moderated = 0;// 房间是适度的
        this.membersOnly = 0;// 房间仅对成员开放
        this.canInvite = 1;// 允许占有者邀请其他人
        this.roomPassword = "";// 群密码
        this.canDiscoverJid = 1;// 能够发现占有者真实 JID的角色
        this.logEnabled = 1;// 登录房间对话
        this.rolesToBroadcast = 7;// 可以广播的角色
        this.useReservedNick = 1;// 仅允许注册的昵称登录
        this.canChangeNick = 1;// 允许使用者修改昵称
        this.canRegister = 1;// 允许用户注册房间
    }

    @Override
    public String toString() {
        return "MucRoom{" +
                "serviceID=" + serviceID +
                ", name='" + name + '\'' +
                ", roomID=" + roomID +
                ", creationDate='" + creationDate + '\'' +
                ", modificationDate='" + modificationDate + '\'' +
                ", naturalName='" + naturalName + '\'' +
                ", description='" + description + '\'' +
                ", subject='" + subject + '\'' +
                ", lockedDate='" + lockedDate + '\'' +
                ", emptyDate='" + emptyDate + '\'' +
                ", canChangeSubject=" + canChangeSubject +
                ", maxUsers=" + maxUsers +
                ", publicRoom=" + publicRoom +
                ", moderated=" + moderated +
                ", membersOnly=" + membersOnly +
                ", canInvite=" + canInvite +
                ", roomPassword='" + roomPassword + '\'' +
                ", canDiscoverJid=" + canDiscoverJid +
                ", logEnabled=" + logEnabled +
                ", rolesToBroadcast=" + rolesToBroadcast +
                ", useReservedNick=" + useReservedNick +
                ", canChangeNick=" + canChangeNick +
                ", canRegister=" + canRegister +
                '}';
    }
}
