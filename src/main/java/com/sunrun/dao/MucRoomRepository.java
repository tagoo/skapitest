package com.sunrun.dao;

import com.sunrun.entity.MucRoom;
import com.sunrun.utils.helper.ChatRoom;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MucRoomRepository extends JpaRepository<MucRoom,Integer>{
    @Query(value = "select r.roomID ,r.name ,r.serviceID,r.* FROM ofmucroom r INNER JOIN " +
            "(select roomID as roomID from ofmucmember m WHERE m.jid =:jid " +
            "UNION ALL SELECT roomID as roomID FROM ofmucaffiliation WHERE jid =:jid) t2 on r.roomID = t2.roomID",nativeQuery = true)
    List<MucRoom> findChatRoomsByJid(@Param("jid")String jid);
}
