package com.sunrun.service.impl;

import com.sunrun.dao.*;
import com.sunrun.entity.*;
import com.sunrun.entity.model.MucRoomMemberKey;
import com.sunrun.exception.NotFindTaskEventException;
import com.sunrun.exception.NotFindTaskException;
import com.sunrun.listener.TaskMessageEvent;
import com.sunrun.service.TaskService;
import com.sunrun.utils.XmppConnectionUtil;
import com.sunrun.vo.TaskInfo;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Domainpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.acl.NotOwnerException;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
public class TaskServiceImpl implements TaskService{
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskFileRepository taskFileRepository;
    @Autowired
    private TaskEventRepository taskEventRepository;
    @Autowired
    private MucRoomMemberRepository mucRoomMemberRepository;
    @Autowired
    private MucServiceRepository mucServiceRepository;
    private Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    @Override
    public Task save(TaskInfo taskinfo) {
        List<TaskFile> fileData = taskinfo.getFileData();
        if (fileData != null && !fileData.isEmpty()) {
            taskFileRepository.saveAll(fileData);
        }
        TaskEvent eventData = taskinfo.getEventData();
        if (eventData.getTaskJID() != null) {
            taskEventRepository.save(eventData);
            taskinfo.addListener(event -> {
                try {
                    MultiUserChat muc = joinMuc(event);
                    muc.sendMessage(String.format("创建任务(%s)，通知内容：%s", event.getTaskName(), event.getContent()));
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(String.format("Failed to send notification that %s save the task(%s)",event.getUserName(), event.getTaskName()));
                } finally {
                    XmppConnectionUtil.getInstance().disconnectAccout();
                }
            });
            taskinfo.operate();
        }
        return taskRepository.save(taskinfo.getTaskData());
    }

    @Override
    public void deleteByTaskJid(String userName, String taskJid) throws NotFindTaskException, NotOwnerException {
        Task task = taskRepository.findByTaskJID(taskJid);
        if (task == null) {
            throw new NotFindTaskException();
        }
        if (!userName.contains("@")) {
          userName = userName + "@" + XmppConnectionUtil.getInstance().getConnection().getXMPPServiceDomain().toString();
        }
        if (!task.getUserName().equals(userName)){
            throw new NotOwnerException();
        }
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setTaskData(task);
        taskInfo.addListener(event ->  {
            try {
                MultiUserChat muc = joinMuc(event);
                muc.sendMessage(String.format("删除任务(%s)", event.getTaskName()));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(String.format("Failed to send notification that %s delete the task(%s)",event.getUserName(), event.getTaskName()));
            } finally {
                XmppConnectionUtil.getInstance().disconnectAccout();
            }
        });
        taskEventRepository.deleteByTaskJID(taskJid);
        taskRepository.deleteByTaskJID(taskJid);
        taskFileRepository.deleteByTaskJID(taskJid);
        taskInfo.operate();
    }

    private MultiUserChat joinMuc(TaskMessageEvent event) throws XmppStringprepException, XMPPException.XMPPErrorException, MultiUserChatException.NotAMucServiceException, SmackException.NotConnectedException, InterruptedException, SmackException.NoResponseException {
        XmppConnectionUtil.getInstance().login(event.getUserName(), XmppConnectionUtil.defaultPassword);
        AbstractXMPPConnection connection = XmppConnectionUtil.getInstance().getConnection();
        MultiUserChat muc = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(JidCreate.entityBareFrom(event.getRoomJID()));
        Domainpart domain = muc.getRoom().getDomain();
        String subdomain = domain.toString().contains(".") ? domain.subSequence(0, domain.toString().lastIndexOf(".")).toString() : domain.toString();
        Optional<MucService> op1 = mucServiceRepository.findById(subdomain);
        MucRoomMemberKey key = new MucRoomMemberKey();
        key.setJid(event.getUserName());
        if (op1.isPresent()) {
            key.setRoomID(op1.get().getServiceID());
        }
        Optional<MucRoomMember> op2 = mucRoomMemberRepository.findById(key);
        String nickName;
        if (op2.isPresent() && op2.get().getNickname() != null) {
            nickName = op2.get().getNickname();
        } else if (event.getUserName().contains("@")) {
            nickName = event.getUserName().substring(0, event.getUserName().lastIndexOf("@"));
        } else {
            nickName = event.getUserName();
        }
        muc.join(Resourcepart.from(nickName));
        return muc;
    }

    @Override
    public Task update(Task task) throws NotFindTaskException {
        Task data = taskRepository.findByTaskJID(task.getTaskJID());
        String oldTaskName = data.getTaskName();
        if (data == null) {
            throw new NotFindTaskException();
        }
        if (task.getStatus() != null) {
            data.setStatus(task.getStatus());
        }
        if (task.getTaskName() != null) {
            data.setTaskName(task.getTaskName());
        }
        if (task.getTaskDescription() != null) {
            data.setTaskDescription(task.getTaskDescription());
        }
        TaskInfo taskInfo = new TaskInfo();
        task.setRoomJID(data.getRoomJID());
        task.setTaskName(oldTaskName);
        taskInfo.setTaskData(task);
        taskInfo.addListener(event ->  {
            try {
                MultiUserChat muc = joinMuc(event);
                muc.sendMessage(String.format("更新任务(%s)", event.getTaskName()));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(String.format("Failed to send notification that %s update the task(%s)",event.getUserName(),event.getTaskName()));
            } finally {
                XmppConnectionUtil.getInstance().disconnectAccout();
            }
        });
        taskInfo.operate();
        return data;
    }

    @Override
    public TaskEvent saveEvent(TaskEvent taskEvent) throws NotFindTaskException {
        Task task = taskRepository.findByTaskJID(taskEvent.getTaskJID());
        if (task == null) {
            throw new NotFindTaskException();
        }
        TaskEvent result = taskEventRepository.save(taskEvent);
        TaskInfo taskInfo = new TaskInfo();
        Task task1 = new Task();
        task1.setTaskName(task.getTaskName());
        task1.setRoomJID(task.getRoomJID());
        taskInfo.setTaskData(task1);
        taskInfo.setEventData(taskEvent);
        taskInfo.addListener(event -> {
            try {
                MultiUserChat muc = joinMuc(event);
                muc.sendMessage(String.format("在任务(%s)中添加事件(%s)", event.getTaskName(), event.getContent()));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(String.format("Failed to send notification that %s add the event(%s) to the task(%s)",event.getUserName(), event.getContent(), event.getTaskName()));
            } finally {
                XmppConnectionUtil.getInstance().disconnectAccout();
            }
        });
        taskInfo.operate();
        return result;
    }

    @Override
    public void deleteEvent(TaskEvent taskEvent) throws NotFindTaskException {
        Task task = null;
        if (taskEvent != null) {
            task = taskRepository.findByTaskJID(taskEvent.getTaskJID());
        }
        if (task == null) {
            Optional<TaskEvent> op = taskEventRepository.findById(taskEvent.getId());
            if (op.isPresent()) {
                taskEvent.setTaskJID(op.get().getTaskJID());
                task = taskRepository.findByTaskJID(taskEvent.getTaskJID());
            }
        }
        if (task == null) {
            throw new NotFindTaskException();
        }
        TaskEvent eve = taskEventRepository.findByIdAndTaskJID(taskEvent.getId(), taskEvent.getTaskJID());
        if (eve != null) {
            taskEventRepository.deleteById(taskEvent.getId());
            TaskInfo taskInfo = new TaskInfo();
            Task task1 = new Task();
            task1.setTaskName(task.getTaskName());
            task1.setRoomJID(task.getRoomJID());
            taskInfo.setTaskData(task1);
            taskInfo.setEventData(eve);
            taskInfo.addListener(event -> {
                try {
                    MultiUserChat muc = joinMuc(event);
                    muc.sendMessage(String.format("在任务(%s)中删除事件(%s)", event.getTaskName(), event.getContent()));
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(String.format("Failed to send notification that %s delete the event(%s) to the task(%s)",event.getUserName(), event.getContent(), event.getTaskName()));
                } finally {
                    XmppConnectionUtil.getInstance().disconnectAccout();
                }
            });
            taskInfo.operate();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskEvent> findAllEvents(String taskJID, Pageable pageable) {
        return taskEventRepository.findAllByTaskJID(taskJID,pageable);
    }

    @Override
    public void updateEvent(TaskEvent taskEvent) throws NotFindTaskException, NotFindTaskEventException {
        TaskEvent source = taskEventRepository.getOne(taskEvent.getId());
        if (source != null) {
            Task task = taskRepository.findByTaskJID(source.getTaskJID());
            if (task == null) {
                throw new NotFindTaskException();
            }
            if (taskEvent.getType() != null) {
                source.setType(taskEvent.getType());
            }
            if (taskEvent.getContent() != null) {
                source.setContent(taskEvent.getContent());
            }
            if (taskEvent.getLatitude() != null) {
                source.setLatitude(taskEvent.getLatitude());
            }
            if (taskEvent.getLongitude() != null) {
                source.setLongitude(taskEvent.getLongitude());
            }
            if (taskEvent.getLocation() != null) {
                source.setLocation(taskEvent.getLocation());
            }
            TaskInfo taskInfo = new TaskInfo();
            Task task1 = new Task();
            task1.setTaskName(task.getTaskName());
            task1.setRoomJID(task.getRoomJID());
            taskInfo.setTaskData(task1);
            taskInfo.setEventData(source);
            taskInfo.addListener(event -> {
                try {
                    MultiUserChat muc = joinMuc(event);
                    muc.sendMessage(String.format("在任务(%s)中更新事件(%s)", event.getTaskName(), event.getContent()));
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(String.format("Failed to send notification that %s update the event(%s) to the task(%s)",event.getUserName(), event.getContent(), event.getTaskName()));
                } finally {
                    XmppConnectionUtil.getInstance().disconnectAccout();
                }
            });
            taskInfo.operate();
        } else {
            throw new NotFindTaskEventException();
        }
    }

    @Override
    public List<TaskFile> saveFile(String userName, String taskJID, List<TaskFile> taskFiles) throws NotFindTaskException {
        Task task = taskRepository.findByTaskJID(taskJID);
        if (task == null) {
            throw new NotFindTaskException();
        }
        List<TaskFile> taskFileList = taskFileRepository.saveAll(taskFiles);
        TaskInfo taskInfo = new TaskInfo();
        Task task1 = new Task();
        task1.setTaskName(task.getTaskName());
        task1.setRoomJID(task.getRoomJID());
        task1.setUserName(userName);
        taskInfo.setTaskData(task1);
        taskInfo.setFileData(taskFileList);
        taskInfo.addListener(event -> {
            try {
                MultiUserChat muc = joinMuc(event);
                muc.sendMessage(String.format("在任务(%s)中添加文件(%s)", event.getTaskName(), event.getContent()));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(String.format("Failed to send notification that %s add the files(%s) to the task(%s)",event.getUserName(), event.getContent(), event.getTaskName()));
            } finally {
                XmppConnectionUtil.getInstance().disconnectAccout();
            }
        });
        taskInfo.operate();
        return taskFileList;
    }

    @Override
    public void deleteFilesByIds(List<Integer> ids, String userName, String taskJID) throws NotFindTaskException {
        Task task = null;
        if (StringUtils.hasText(taskJID)){
            task = taskRepository.findByTaskJID(taskJID);
            if (task == null) {
                throw new NotFindTaskException();
            }
        }
        if (task == null) {
            TaskFile one = taskFileRepository.getOne(ids.get(0));
            task = taskRepository.findByTaskJID(one.getTaskJID());
        }
        if (task == null) {
            throw new NotFindTaskException();
        }
        List<TaskFile> taskFiles = taskFileRepository.findAllById(ids);
        taskFileRepository.deleteByIds(ids);
        TaskInfo taskInfo = new TaskInfo();
        Task task1 = new Task();
        task1.setTaskName(task.getTaskName());
        task1.setRoomJID(task.getRoomJID());
        task1.setUserName(userName);
        taskInfo.setTaskData(task1);
        taskInfo.setFileData(taskFiles);
        taskInfo.addListener(event -> {
            try {
                MultiUserChat muc = joinMuc(event);
                muc.sendMessage(String.format("在任务(%s)中删除文件(%s)", event.getTaskName(), event.getContent()));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(String.format("Failed to send notification that %s delete the files(%s) to the task(%s)",event.getUserName(), event.getContent(), event.getTaskName()));
            } finally {
                XmppConnectionUtil.getInstance().disconnectAccout();
            }
        });
        taskInfo.operate();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskFile> findAllFiles(String taskJID,Pageable pageable) {
        return taskFileRepository.findAllByTaskJID(taskJID,pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Task> findAllByRoomJID(String roomJID, Pageable pageable) {
        return taskRepository.findAllByRoomJID(roomJID, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskEvent findEventById(Integer id) {
        return taskEventRepository.getOne(id);
    }
}
