package com.sunrun.service;

import com.sunrun.entity.Task;
import com.sunrun.entity.TaskEvent;
import com.sunrun.entity.TaskFile;
import com.sunrun.exception.NotFindTaskEventException;
import com.sunrun.exception.NotFindTaskException;
import com.sunrun.vo.TaskInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.acl.NotOwnerException;
import java.util.List;

public interface TaskService {
    Task save(TaskInfo task);

    void deleteByTaskJid(String userName, String taskJid) throws NotFindTaskException, NotOwnerException;

    Task update(Task task) throws NotFindTaskException;

    TaskEvent saveEvent(TaskEvent taskEvent) throws NotFindTaskException;

    void deleteEvent(TaskEvent taskEvent) throws NotFindTaskException;

    Page<TaskEvent> findAllEvents(String taskJID, Pageable pageable);

    void updateEvent(TaskEvent taskEvent) throws NotFindTaskException, NotFindTaskEventException;

    List<TaskFile> saveFile(String userName, String taskJID, List<TaskFile> taskFile) throws NotFindTaskException;

    void deleteFilesByIds(List<Integer> ids, String userName, String taskJID) throws NotFindTaskException;

    Page<TaskFile> findAllFiles(String taskJID, Pageable pageable);

    Page<Task> findAllByRoomJID(String roomJID, Pageable pageable);
}
