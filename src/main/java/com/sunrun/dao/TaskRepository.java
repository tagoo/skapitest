package com.sunrun.dao;

import com.sunrun.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Integer>{
    void deleteByTaskJID(String taskJid);

    Task findByTaskJID(String taskJid);

    Page<Task> findAllByRoomJID(String roomJID, Pageable pageable);
}
