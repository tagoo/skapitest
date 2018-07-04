package com.sunrun.entity.model;

import java.io.Serializable;
import java.util.Objects;

public class MucRoomMemberKey implements Serializable {
    private static final long serialVersionUID = -1474567025960671592L;
    private Long roomID;
    private String jid;

    public Long getRoomID() {
        return roomID;
    }

    public void setRoomID(Long roomID) {
        this.roomID = roomID;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public MucRoomMemberKey() {
    }

    public MucRoomMemberKey(Long roomID, String jid) {
        this.roomID = roomID;
        this.jid = jid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomID, jid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MucRoomMemberKey)) return false;
        MucRoomMemberKey that = (MucRoomMemberKey) o;
        return Objects.equals(roomID, that.roomID) &&
                Objects.equals(jid, that.jid);
    }
}
