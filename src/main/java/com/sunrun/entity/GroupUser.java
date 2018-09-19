package com.sunrun.entity;


import com.sunrun.entity.model.GroupUserKey;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity(name = "ofgroupuser")
@IdClass(GroupUserKey.class)
@Setter
@Getter
@ToString
public class GroupUser {
    @Id
    @Column(name = "groupName")
    private String groupName;
    @Id
    @Column(name = "username")
    private String username;
    @Id
    @Column(name = "administrator")
    private Byte administrator;

    public GroupUser() {
    }

    public GroupUser(String groupName, String username) {
        this.groupName = groupName;
        this.username = username;
        this.administrator = 0;
    }
}
