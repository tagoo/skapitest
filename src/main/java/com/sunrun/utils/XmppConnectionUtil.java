package com.sunrun.utils;

import com.sunrun.openfire.MyConnectionListener;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.*;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class XmppConnectionUtil {
    private final static String host = "localhost";
    private final static int port = 5222;
    private final static String domainName = "sunrun";
    private volatile static AbstractXMPPConnection connection;
    private ConnectionListener connectionListener;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private XmppConnectionUtil() {}

    private static class InstanceFactory{
        public static XmppConnectionUtil instance = new XmppConnectionUtil();
    }
    public static  XmppConnectionUtil getInstance() {
        return InstanceFactory.instance;
    }

    public AbstractXMPPConnection getConnection() {
        if (connection == null) {
            synchronized (XmppConnectionUtil.class) {
                if (connection == null) {
                    try {
                        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                                .setXmppDomain(domainName)
                                .setHost(host)
                                .setPort(port)
                                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                                .setCompressionEnabled(true)
                                .setSendPresence(false)
                                .setDebuggerEnabled(true)
                                .build();
                        //设置不需要同意才就可以添加好友
                        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
                        connection = new XMPPTCPConnection(config);
                        connection.connect();
                        //设置重连管理器
                        /*ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
                        reconnectionManager.enableAutomaticReconnection();*/
                        //设置ping管理器
                        PingManager pingManager = PingManager.getInstanceFor(connection);
                        pingManager.setPingInterval(10);
                        /*pingManager.pingMyServer(true,1000 * 20);*/
                        pingManager.registerPingFailedListener(new PingFailedListener() {
                            @Override
                            public void pingFailed() {
                                logger.info("Failed to ping server and reconnect again");
                                //reconnect();
                            }
                        });

                    } catch (SmackException | IOException | XMPPException | InterruptedException e) {
                        e.printStackTrace();
                        connection = null;
                    }
                    /*new Thread(() -> {
                        try {
                            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                                    .setXmppDomain(openfireConfig.getDomainName())
                                    .setHost(openfireConfig.getHost())
                                    .setPort(openfireConfig.getPort())
                                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                                    .setCompressionEnabled(true)
                                    .setSendPresence(false)
                                    .setDebuggerEnabled(true)
                                    .build();
                            //设置不需要同意才就可以添加好友
                            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
                            connection = new XMPPTCPConnection(config);
                            connection.connect();
                        } catch (SmackException | IOException | XMPPException | InterruptedException e) {
                            e.printStackTrace();
                            connection = null;
                        }
                    }).start();*/
                }
            }
        }
        return connection;
    }

    public void reconnect() {
        new Thread(() -> {
            try {
                connection.disconnect();
                connection.connect();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void closeConnection(){
        if (connection != null) {
            connection.removeConnectionListener(connectionListener);
            if (connection.isConnected()) {
                connection.disconnect();
            }
            connection = null;
            logger.info("close connection");
        }

    }

    public boolean isAuthenticated(){
        return connection != null &&connection.isConnected() && connection.isAuthenticated();
    }

    public boolean login(String username, String password) {
        AbstractXMPPConnection connection = getConnection();
        if (connection == null) {
            return false;
        }
        try {
            connection.login(username, password);
            connectionListener = new MyConnectionListener(connection,username,password);
            connection.addConnectionListener(connectionListener);
            setPresence(0);
            return true;
        }catch (SmackException | IOException | XMPPException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setPresence(int code) {
        XMPPConnection connection = getConnection();
        if (connection == null) {
            return;
        }
        Presence presence;
        try {
            switch (code) {
                case 0:
                    presence = new Presence(Presence.Type.available);
                    connection.sendStanza(presence);
                    logger.info("set presence online");
                    break;
                case 1:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.chat);
                    logger.info("set presence chat");
                    connection.sendStanza(presence);
                    break;
                case 2:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.dnd);
                    logger.info("set presence busy");
                    connection.sendStanza(presence);
                    break;
                case 3:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.away);
                    logger.info("set presence away");
                    connection.sendStanza(presence);
                case 4:
                    presence = new Presence(Presence.Type.unavailable);
                    logger.info("set presence offline");
                    connection.sendStanza(presence);
                    break;
                default:
                    break;

            }
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<RosterEntry> getAllFriends() {
        if (getConnection() == null) {
            return null;
        }
        Set<RosterEntry> entries = Roster.getInstanceFor(connection).getEntries();
        return new ArrayList<RosterEntry>(entries);
    }

    public List<HostedRoom> getAllHostedRooms(){
        if (getConnection() == null) {
            return null;
        }
        try {
            List<DomainBareJid> xmppServiceDomains = MultiUserChatManager.getInstanceFor(getConnection()).getXMPPServiceDomains();
            List<HostedRoom> hostedRooms = new ArrayList<>();
            for (DomainBareJid jid:xmppServiceDomains) {
                hostedRooms.addAll(MultiUserChatManager.getInstanceFor(getConnection()).getHostedRooms(JidCreate.domainBareFrom(jid)));
            }
            return hostedRooms;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean joinChatRoom(String username, String roomName, String domainName){
        MultiUserChatManager instanceFor = MultiUserChatManager.getInstanceFor(connection);
        StringBuilder sb = new StringBuilder(roomName);
        sb.append("@");
        if (StringUtils.hasText(domainName)) {
            sb.append(domainName);
        } else {
            sb.append("conference");
        }
        sb.append(".").append(getInstance().getConnection().getServiceName().toString());
        try {
            MultiUserChat multiUserChat = instanceFor.getMultiUserChat(JidCreate.entityBareFrom(sb.toString()));
            multiUserChat.join(Resourcepart.from(username));
            return true;
        } catch (XmppStringprepException |SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException | InterruptedException | MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean disconnectAccout(){
        AbstractXMPPConnection connection = getConnection();
        if (connection != null && connection.isConnected()) {
            try {
                connection.disconnect(new Presence(Presence.Type.unavailable));
                return true;
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();

            }
        }
        return false;
    }
}
