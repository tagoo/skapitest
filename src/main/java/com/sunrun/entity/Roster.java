package com.sunrun.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
@Entity(name = "ofroster")
public class Roster implements Serializable{
    private static final long serialVersionUID = -8205836045730349100L;
    @Id
    @Column(name = "rosterID")
    private Integer rosterId;
    @Column(name="username")
    private String username;
    private String jid;
    private Integer sub;
    private Integer ack;
    private Integer recv;
    private String nick;

    public Integer getRosterId() {
        return rosterId;
    }

    public void setRosterId(Integer rosterId) {
        this.rosterId = rosterId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public Integer getSub() {
        return sub;
    }

    public void setSub(Integer sub) {
        this.sub = sub;
    }

    public Integer getAck() {
        return ack;
    }

    public void setAck(Integer ack) {
        this.ack = ack;
    }

    public Integer getRecv() {
        return recv;
    }

    public void setRecv(Integer recv) {
        this.recv = recv;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
