package com.sunrun.entity;

import com.sunrun.entity.model.MucRoomMemberKey;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity(name = "ofmucmember")
@IdClass(MucRoomMemberKey.class)
public class MucRoomMember {
    @Column(name = "roomID")
    private Long roomID;
    @Column(name = "jid")
    private String jid;
    private String nickname;
    private String firstName;
    private String lastName;
    private String url;
    private String email;
    private String faqentry;

    public MucRoomMember() {
    }

    @Id
    public Long getRoomID() {
        return roomID;
    }

    public void setRoomID(Long roomID) {
        this.roomID = roomID;
    }
    @Id
    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFaqentry() {
        return faqentry;
    }

    public void setFaqentry(String faqentry) {
        this.faqentry = faqentry;
    }

    @Override
    public String toString() {
        return "MucRoomMember{" +
                "roomID=" + roomID +
                ", jid='" + jid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", url='" + url + '\'' +
                ", email='" + email + '\'' +
                ", faqentry='" + faqentry + '\'' +
                '}';
    }
}
