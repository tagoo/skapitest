package com.sunrun.dao;

import com.sunrun.entity.TaskEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskEventRepository extends JpaRepository<TaskEvent,Integer> {

    void deleteByTaskJID(String taskJid);

    TaskEvent findByIdAndTaskJID(Integer id, String taskJId);

    Page<TaskEvent> findAllByTaskJID(String taskJID, Pageable pageable);
}
