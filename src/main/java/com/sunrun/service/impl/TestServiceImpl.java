package com.sunrun.service.impl;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.sunrun.controller.TestController;
import com.sunrun.dao.UserRepository;
import com.sunrun.entity.User;
import com.sunrun.service.TestService;
import com.sunrun.utils.XmppConnectionUtil;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(TestServiceImpl.class);

    @Override
    public void login() {
        List<User> byDomainId = userRepository.findByDomainId(1);
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        for (User u : byDomainId) {
                executorService.submit(() -> {
                    try {
                        XMPPTCPConnectionConfiguration configuration = XMPPTCPConnectionConfiguration.builder()
                                .setUsernameAndPassword(u.getUserName(), "123456")
                                .setXmppDomain("sunrun")
                                .setHost("localhost")
                                .setPort(5222)
                                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                                .setResource("smack")
                                .build();
                        AbstractXMPPConnection connection = new XMPPTCPConnection(configuration);
                        connection.connect().login();
                        Presence presence = new Presence(Presence.Type.available);
                        presence.setStatus("在线");
                        connection.sendStanza(presence);
                        ChatManager chatManager = ChatManager.getInstanceFor(connection);
                        chatManager.addListener(new IncomingChatMessageListener() {
                            @Override
                            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {

                                System.out.println(u.getUserName() + "收到 New message from " + from + ": " + message.getBody() + ",消息类型：" + chat.toString());

                            }
                        });
                        MultiUserChat muc = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(JidCreate.entityBareFrom("test@sunrun.sunrun"));
                        muc.addMessageListener((message) -> {
                            logger.info(u.getUserName() + "在" + LocalDateTime.now().toString() + "收到消息：" + message.getBody());
                        });
                        muc.join(Resourcepart.from(u.getUserName()));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        }
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
