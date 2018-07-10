package com.sunrun.controller;

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
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@RequestMapping("test")
@RestController
public class TestController {
    @RequestMapping()
    public void testDemo(){
        /*try {
            XMPPTCPConnectionConfiguration configuration = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword("demo1", "123456")
                    .setXmppDomain("sunrun")
                    .setHost("localhost")
                    .setPort(5222)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
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
                    System.out.println("New message from " + from + ": " + message.getBody());
                }
            });
            EntityBareJid jid = JidCreate.entityBareFrom("demo2@sunrun");
            Chat chat = chatManager.chatWith(jid);
            chat.send("Howdy!");
            for (int i= 0; i< 100 ; i++) {
                chat.send(i + "");
            }
            Roster roster = Roster.getInstanceFor(connection);
            Collection<RosterEntry> entries = roster.getEntries();
            for (RosterEntry entry : entries) {
                System.out.println(entry);
            }
            Thread.sleep(60000);
            *//*connection.disconnect();*//*
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        XmppConnectionUtil instance = XmppConnectionUtil.getInstance();
        instance.login("demo1","123456");
        List<RosterEntry> allFriends = instance.getAllFriends();
        List<HostedRoom> allHostedRooms = instance.getAllHostedRooms();
        System.out.println(allHostedRooms);
        instance.closeConnection();
    }
}
