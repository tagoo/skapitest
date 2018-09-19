package com.sunrun.dao;

import com.sunrun.entity.MucRoomMember;
import com.sunrun.entity.model.MucRoomMemberKey;
import com.sunrun.po.MucRoomPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MucRoomMemberRepository extends JpaRepository<MucRoomMember,MucRoomMemberKey> {
    @Query(value = "SELECT u.roomID,u.nickname,u.jid,u.url,u.email,u.firstName,u.lastName,u.faqentry FROM ofmucroom AS r" +
            " INNER JOIN ofmucservice AS s ON r.serviceID = s.serviceID" +
            " INNER JOIN ofmucmember AS u ON r.roomID = u.roomID" +
            " WHERE r.name = :roomName AND s.subdomain = :serviceName AND u.jid = :jid",nativeQuery = true)
    MucRoomMember findMucRoomByNameAndServiceName(@Param("roomName")String roomName, @Param("serviceName")String serviceName, @Param("jid")String jid);

    @Query(value = "SELECT DISTINCT new com.sunrun.po.MucRoomPo(r.roomID, r.name, r.naturalName, s.subdomain) FROM ofmucmember AS m  INNER JOIN ofmucroom AS r ON m.roomID = r.roomID  LEFT JOIN ofmucservice AS s ON s.serviceID = r.serviceID WHERE m.jid in ?1")
    List<MucRoomPo> findRoomByGroupJidIn(List<String> collect);

}
