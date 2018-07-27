package com.sunrun.controller;

import com.sunrun.common.DataDictionary;
import com.sunrun.common.notice.NoticeFactory;
import com.sunrun.common.notice.NoticeMessage;
import com.sunrun.common.notice.ReturnData;
import com.sunrun.entity.Task;
import com.sunrun.entity.TaskEvent;
import com.sunrun.entity.TaskFile;
import com.sunrun.exception.NotFindTaskEventException;
import com.sunrun.exception.NotFindTaskException;
import com.sunrun.service.TaskService;
import com.sunrun.vo.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.acl.NotOwnerException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping("create")
    public ReturnData save(@RequestParam(name = "lang", defaultValue = "zh") String lang,  @ModelAttribute TaskInfo taskInfo){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        Task save = null;
        try {
            Task taskData = taskInfo.getTaskData();
            TaskEvent eventData = taskInfo.getEventData();
            if (StringUtils.hasText(taskData.getTaskName())){
                if (taskData.getCreateTime() == null) {
                    taskInfo.getTaskData().setCreateTime(LocalDateTime.now());
                }
                if (taskData.getStatus() == null) {
                    taskInfo.getTaskData().setStatus(DataDictionary.DEFAULT_TASK_STATUS);
                }
                if (eventData.getTaskJID() != null) {
                    if (eventData.getCreateTime() == null) {
                        eventData.setCreateTime(taskInfo.getTaskData().getCreateTime());
                    }
                    if (eventData.getType() == null) {
                        eventData.setType(1);
                    }
                }
                save = taskService.save(taskInfo);
                if (save != null) {
                    noticeMessage = NoticeMessage.SUCCESS;
                }
            } else {
                noticeMessage = NoticeMessage.TASK_NAME_IS_EMPTY;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, save);
    }

    @RequestMapping(value = "delete/{taskJID}", method = RequestMethod.DELETE)
    public ReturnData delete(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                             @PathVariable(name = "taskJID") String  taskJid,
                             @RequestParam(name = "userName") String userName){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        if (StringUtils.hasText(taskJid)){
            if (StringUtils.hasText(userName)) {
                try {
                    taskService.deleteByTaskJid(userName,taskJid);
                    noticeMessage =NoticeMessage.SUCCESS;
                } catch (NotFindTaskException e) {
                    noticeMessage = NoticeMessage.TASK_NOT_EXIST;
                    e.printStackTrace();
                } catch (NotOwnerException e) {
                    noticeMessage = NoticeMessage.NOT_PERMISSION;
                    e.printStackTrace();
                }
            } else {
                noticeMessage = NoticeMessage.USERNAME_IS_NULL;
            }

        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }

    @RequestMapping(value = "update" , method = RequestMethod.PATCH)
    public ReturnData update(@RequestParam(name = "lang", defaultValue = "zh") String lang,  @ModelAttribute Task task){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        Task update = null;
        if (StringUtils.hasText(task.getTaskJID()) && StringUtils.hasText(task.getUserName())) {
            try {
                update = taskService.update(task);
                noticeMessage = NoticeMessage.SUCCESS;
            } catch (NotFindTaskException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.TASK_NOT_EXIST;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, update);
    }

    @PostMapping("event/add")
    public ReturnData addEvent(@RequestParam(name = "lang", defaultValue = "zh") String lang, @ModelAttribute TaskEvent taskEvent){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        TaskEvent event = null;
        if (StringUtils.hasText(taskEvent.getTaskJID()) && StringUtils.hasText(taskEvent.getUserName())){
            if (taskEvent.getCreateTime() == null) {
                taskEvent.setCreateTime(LocalDateTime.now());
            }
            if (taskEvent.getType() == null) {
                taskEvent.setType(DataDictionary.DEFAULT_TASK_EVENT_TYPE);
            }
            try {
                event = taskService.saveEvent(taskEvent);
                noticeMessage = NoticeMessage.SUCCESS;
            } catch (NotFindTaskException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.TASK_NOT_EXIST;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, event);
    }

    @RequestMapping(value = "event/{id}", method = RequestMethod.DELETE )
    public ReturnData deleteEvent(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                  @PathVariable(name = "id") Integer  id,
                                  @RequestParam(name = "taskJID",required = false) String taskJId,
                                  @RequestParam(name = "userName") String userName){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        TaskEvent event = null;
        if (id != null && StringUtils.hasText(userName)){
            try {
                TaskEvent taskEvent = new TaskEvent();
                taskEvent.setUserName(userName);
                taskEvent.setTaskJID(taskJId);
                taskEvent.setId(id);
                taskService.deleteEvent(taskEvent);
                noticeMessage = NoticeMessage.SUCCESS;
            } catch (NotFindTaskException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.TASK_NOT_EXIST;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, event);
    }
    @GetMapping("event/{taskJID}")
    public ReturnData findEvents(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                 @PathVariable(name = "taskJID") String  taskJID,
                                 @RequestParam(name = "page",defaultValue = "0",required = false)int page,
                                 @RequestParam(name = "size",defaultValue = "15",required = false)int size){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        Page<TaskEvent> taskEventList = null;
        if (StringUtils.hasText(taskJID)){
            Pageable pageable =  null;
            if (page > -1) {
                pageable = PageRequest.of (page,size);
            }
            taskEventList = taskService.findAllEvents(taskJID,pageable);
            noticeMessage = NoticeMessage.SUCCESS;
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, taskEventList);
    }

    @RequestMapping(value = "event/update", method = RequestMethod.PATCH )
    public ReturnData updateEvent(@RequestParam(name = "lang", defaultValue = "zh") String lang, @ModelAttribute TaskEvent taskEvent ){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        if (taskEvent.getId() != null && StringUtils.hasText(taskEvent.getUserName())){
            try {
                taskService.updateEvent(taskEvent);
                noticeMessage = NoticeMessage.SUCCESS;
            } catch (NotFindTaskException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.TASK_NOT_EXIST;
            } catch (NotFindTaskEventException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.TASK_EVENT_NOT_EXIST;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }

    @PostMapping("file/add")
    public ReturnData saveFiles(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                @ModelAttribute TaskInfo taskInfo,
                                @RequestParam(name = "userName") String userName,
                                @RequestParam(name = "taskJID") String taskJID) {
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        List<TaskFile> save = null;
        if (StringUtils.hasText(userName) && StringUtils.hasText(taskJID)) {
            LocalDateTime createTime = LocalDateTime.now();
            boolean flag = true;
            for (TaskFile taskFile : taskInfo.getFileData()) {
                if (StringUtils.hasText(taskFile.getFileName())) {
                    if (taskFile.getCreateTime() == null) {
                        taskFile.setCreateTime(createTime);
                    }
                    taskFile.setTaskJID(taskJID);
                    taskFile.setUserName(userName);
                    if (taskFile.getFileType() == null && taskFile.getFileName().contains(".")) {
                        taskFile.setFileType(taskFile.getFileName().substring(taskFile.getFileName().lastIndexOf(".") + 1).toLowerCase());
                    }
                } else {
                    noticeMessage = NoticeMessage.FILE_NAME_IS_EMPTY;
                    flag = false;
                    break;
                }
            }
            if (flag) {
                try {
                    save = taskService.saveFile(userName, taskJID, taskInfo.getFileData());
                    noticeMessage = NoticeMessage.SUCCESS;
                } catch (NotFindTaskException e) {
                    e.printStackTrace();
                    noticeMessage = NoticeMessage.TASK_NOT_EXIST;
                }
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, save);
    }

    @RequestMapping(value = "file",method = RequestMethod.DELETE )
    public ReturnData deleteFiles(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                @RequestParam(name = "userName") String userName,
                                @RequestParam(name = "taskJID",required = false) String taskJID,
                                @RequestParam(name ="ids")List<Integer> ids) {
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        if (StringUtils.hasText(userName) && !ids.isEmpty()) {
            try {
                taskService.deleteFilesByIds(ids,userName,taskJID);
                noticeMessage = NoticeMessage.SUCCESS;
            } catch (NotFindTaskException e) {
                e.printStackTrace();
                noticeMessage = NoticeMessage.TASK_NOT_EXIST;
            }
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, null);
    }

    @GetMapping("file/{taskJID}")
    public ReturnData findFiles(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                @PathVariable(name = "taskJID") String  taskJID,
                                @RequestParam(name = "page",defaultValue = "0",required = false)int page,
                                @RequestParam(name = "size",defaultValue = "15",required = false)int size){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        Page<TaskFile> taskFileList = null;
        if (StringUtils.hasText(taskJID)){
            Pageable pageable =  null;
            if (page > -1) {
                pageable = PageRequest.of (page,size);
            }
            taskFileList = taskService.findAllFiles(taskJID,pageable);
            noticeMessage = NoticeMessage.SUCCESS;
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, taskFileList);
    }

    @GetMapping("room/{roomJID}")
    public ReturnData findTasks(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                @PathVariable(name = "roomJID") String  roomJID,
                                @RequestParam(name = "page",defaultValue = "0",required = false)int page,
                                @RequestParam(name = "size",defaultValue = "15",required = false)int size){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        Page<Task> taskList = null;
        if (StringUtils.hasText(roomJID)){
            Pageable pageable =  null;
            if (page > -1) {
                pageable = PageRequest.of (page,size);
            }
            taskList =  taskService.findAllByRoomJID(roomJID,pageable);
            noticeMessage = NoticeMessage.SUCCESS;
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, taskList);
    }
}
