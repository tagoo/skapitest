package com.sunrun.utils.helper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.*;
public class ChatRoom {
    private String roomName;
    private String description;
    private String password;
    private String subject;
    private String naturalName;
    private int maxUsers;
    private Date creationDate;
    private Date modificationDate;
    private boolean persistent;
    private boolean publicRoom;
    private boolean registrationEnabled;
    private boolean canAnyoneDiscoverJID;
    private boolean canOccupantsChangeSubject;
    private boolean canOccupantsInvite;
    private boolean canChangeNickname;
    private boolean logEnabled;
    private boolean loginRestrictedToNickname;
    private boolean membersOnly;
    private boolean moderated;

    private List<String> broadcastPresenceRoles;

    private List<String> owners;

    private List<String> admins;

    private List<String> members;

    private List<String> outcasts;

    public ChatRoom() {
        setDefaultProp();
    }

    public void setDefaultProp(){
        this.persistent = true;//房间持久化
        this.maxUsers = 0; //不限制
        this.publicRoom = true;//True if the room is searchable and visible through service discovery
        this.registrationEnabled = true;//True if users are allowed to register with the room. By default, room registration is enabled.
        this.canAnyoneDiscoverJID = true;//True if every presence packet will include the JID of every occupant.
        this.canOccupantsChangeSubject =true;//True if participants are allowed to change the room’s subject.
        this.canOccupantsInvite = true;//True if occupants can invite other users to the room.
        this.canChangeNickname = true ;//True if room occupants are allowed to change their nicknames in the room. By default, occupants are allowed to change their nicknames.
        this.logEnabled =true;//True if room occupants are allowed to change their nicknames in the room. By default, occupants are allowed to change their nicknames.
        this.loginRestrictedToNickname =false;// True if registered users can only join the room using their registered nickname. By default, registered users can join the room using any nickname.
        this.membersOnly = true;//True if the room requires an invitation to enter. That is if the room is members-only.
        this.moderated = true;//True if the room in which only those with “voice” may send messages to all occupants.
        this.broadcastPresenceRoles = Arrays.asList("moderator","participant","visitor");//The list of roles of which presence will be broadcasted to the rest of the occupants.
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNaturalName() {
        return naturalName;
    }

    public void setNaturalName(String naturalName) {
        this.naturalName = naturalName;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreationDate() {
        return creationDate;
    }

    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getModificationDate() {
        return modificationDate;
    }

    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public boolean isPublicRoom() {
        return publicRoom;
    }

    public void setPublicRoom(boolean publicRoom) {
        this.publicRoom = publicRoom;
    }

    public boolean isRegistrationEnabled() {
        return registrationEnabled;
    }

    public void setRegistrationEnabled(boolean registrationEnabled) {
        this.registrationEnabled = registrationEnabled;
    }

    public boolean isCanAnyoneDiscoverJID() {
        return canAnyoneDiscoverJID;
    }

    public void setCanAnyoneDiscoverJID(boolean canAnyoneDiscoverJID) {
        this.canAnyoneDiscoverJID = canAnyoneDiscoverJID;
    }

    public boolean isCanOccupantsChangeSubject() {
        return canOccupantsChangeSubject;
    }

    public void setCanOccupantsChangeSubject(boolean canOccupantsChangeSubject) {
        this.canOccupantsChangeSubject = canOccupantsChangeSubject;
    }

    public boolean isCanOccupantsInvite() {
        return canOccupantsInvite;
    }

    public void setCanOccupantsInvite(boolean canOccupantsInvite) {
        this.canOccupantsInvite = canOccupantsInvite;
    }

    public boolean isCanChangeNickname() {
        return canChangeNickname;
    }

    public void setCanChangeNickname(boolean canChangeNickname) {
        this.canChangeNickname = canChangeNickname;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

    public boolean isLoginRestrictedToNickname() {
        return loginRestrictedToNickname;
    }

    public void setLoginRestrictedToNickname(boolean loginRestrictedToNickname) {
        this.loginRestrictedToNickname = loginRestrictedToNickname;
    }

    public boolean isMembersOnly() {
        return membersOnly;
    }

    public void setMembersOnly(boolean membersOnly) {
        this.membersOnly = membersOnly;
    }

    public boolean isModerated() {
        return moderated;
    }

    public void setModerated(boolean moderated) {
        this.moderated = moderated;
    }

    public List<String> getBroadcastPresenceRoles() {
        return broadcastPresenceRoles;
    }

    public void setBroadcastPresenceRoles(List<String> broadcastPresenceRoles) {
        this.broadcastPresenceRoles = broadcastPresenceRoles;
    }

    public List<String> getOwners() {
        return owners;
    }

    public void setOwners(List<String> owners) {
        this.owners = owners;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }


    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<String> getOutcasts() {
        return outcasts;
    }

    public void setOutcasts(List<String> outcasts) {
        this.outcasts = outcasts;
    }


    @Override
    public String toString() {
        return "ChatRoom{" +
                "roomName='" + roomName + '\'' +
                ", description='" + description + '\'' +
                ", password='" + password + '\'' +
                ", subject='" + subject + '\'' +
                ", naturalName='" + naturalName + '\'' +
                ", maxUsers=" + maxUsers +
                ", creationDate=" + creationDate +
                ", modificationDate=" + modificationDate +
                ", persistent=" + persistent +
                ", publicRoom=" + publicRoom +
                ", registrationEnabled=" + registrationEnabled +
                ", canAnyoneDiscoverJID=" + canAnyoneDiscoverJID +
                ", canOccupantsChangeSubject=" + canOccupantsChangeSubject +
                ", canOccupantsInvite=" + canOccupantsInvite +
                ", canChangeNickname=" + canChangeNickname +
                ", logEnabled=" + logEnabled +
                ", loginRestrictedToNickname=" + loginRestrictedToNickname +
                ", membersOnly=" + membersOnly +
                ", moderated=" + moderated +
                ", broadcastPresenceRoles=" + broadcastPresenceRoles +
                ", owners=" + owners +
                ", admins=" + admins +
                ", members=" + members +
                ", outcasts=" + outcasts +
                '}';
    }
}
