package com.sunrun.entity.model;

import java.io.Serializable;
import java.util.Objects;

public class GroupUserKey implements Serializable {

    private static final long serialVersionUID = -2206451178022677813L;
    private String groupName;
    private String username;
    private Byte administrator;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Byte getAdministrator() {
        return administrator;
    }

    public void setAdministrator(Byte administrator) {
        this.administrator = administrator;
    }

    public GroupUserKey() {
    }

    public GroupUserKey(String groupName, String username, Byte administrator) {
        this.groupName = groupName;
        this.username = username;
        this.administrator = administrator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupUserKey)) return false;
        GroupUserKey that = (GroupUserKey) o;
        return Objects.equals(getGroupName(), that.getGroupName()) &&
                Objects.equals(getUsername(), that.getUsername()) &&
                Objects.equals(getAdministrator(), that.getAdministrator());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGroupName(), getUsername(), getAdministrator());
    }
}
