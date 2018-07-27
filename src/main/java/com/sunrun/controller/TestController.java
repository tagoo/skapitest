package com.sunrun.controller;

import com.sunrun.dao.UserRepository;
import com.sunrun.entity.User;
import com.sunrun.service.TestService;
import com.sunrun.utils.XmppConnectionUtil;
import org.aspectj.weaver.ast.Test;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequestMapping("test")
@RestController
public class TestController {
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    private TestService testService;
    @RequestMapping()
    public void testDemo(){
        testService.login();


       /* XmppConnectionUtil instance = XmppConnectionUtil.getInstance();
        instance.login("demo1","123456");
        List<RosterEntry> allFriends = instance.getAllFriends();
        List<HostedRoom> allHostedRooms = instance.getAllHostedRooms();
        System.out.println(allHostedRooms);
        Presence presence = new Presence(Presence.Type.available);
        instance.closeConnection();*/
    }
}
