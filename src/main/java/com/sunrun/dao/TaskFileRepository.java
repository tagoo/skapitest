package com.sunrun.dao;

import com.sunrun.entity.TaskFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskFileRepository extends JpaRepository<TaskFile,Integer>{
    int deleteByTaskJID(String taskJid);

    @Modifying
    @Query(value = "delete from tbtaskfile where id in (:ids)", nativeQuery = true)
    int deleteByIds(@Param("ids") List<Integer> ids);

    Page<TaskFile> findAllByTaskJID(String taskJID, Pageable pageable);
}
