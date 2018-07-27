package com.sunrun.dao;

import com.sunrun.entity.MucRoomMember;
import com.sunrun.entity.model.MucRoomMemberKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MucRoomMemberRepository extends JpaRepository<MucRoomMember,MucRoomMemberKey> {
    @Query(value = "SELECT u.roomID,u.nickname,u.jid,u.url,u.email,u.firstName,u.lastName,u.faqentry FROM ofmucroom AS r" +
            " INNER JOIN ofmucservice AS s ON r.serviceID = s.serviceID" +
            " INNER JOIN ofmucmember AS u ON r.roomID = u.roomID" +
            " WHERE r.name = :roomName AND s.subdomain = :serviceName AND u.jid = :jid",nativeQuery = true)
    MucRoomMember findMucRoomByNameAndServiceName(@Param("roomName")String roomName, @Param("serviceName")String serviceName, @Param("jid")String jid);

}
