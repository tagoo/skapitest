package com.sunrun.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class MucRoomPo {
    private Long roomID;
    private String name;
    private String naturalName;
    private String subdomain;

    public MucRoomPo(Long roomID, String name, String naturalName, String subdomain) {
        this.roomID = roomID;
        this.name = name;
        this.naturalName = naturalName;
        this.subdomain = subdomain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MucRoomPo)) return false;
        if (!super.equals(o)) return false;
        MucRoomPo mucRoomPo = (MucRoomPo) o;
        return Objects.equals(getRoomID(), mucRoomPo.getRoomID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getRoomID());
    }
}
