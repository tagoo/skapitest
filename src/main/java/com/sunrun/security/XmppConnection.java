package com.sunrun.security;

import com.sunrun.common.config.OfConfig;
import com.sunrun.listener.openfire.MyConnectionListener;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class XmppConnection implements Serializable{
    private static final long serialVersionUID = 5704194033499157942L;
    private String host ;
    private int port ;
    private String domainName ;
    private AbstractXMPPConnection connection;
    private ConnectionListener connectionListener;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public final static String defaultPassword = "123456";

    public XmppConnection(OfConfig ofConfig) {
        this.host = ofConfig.getHost();
        this.port = ofConfig.getPort();
        this.domainName = ofConfig.getDomainName();
        this.getConnection();
    }

    public AbstractXMPPConnection getConnection() {
        if (connection == null) {
            synchronized (this) {
                if (connection == null) {
                    XMPPTCPConnectionConfiguration config = null;
                    try {
                        config = XMPPTCPConnectionConfiguration.builder()
                                .setXmppDomain(domainName)
                                .setHost(host)
                                .setPort(port)
                                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                                .setCompressionEnabled(true)
                                .setSendPresence(false)
                                .build();
                    } catch (XmppStringprepException e) {
                        e.printStackTrace();
                        return null;
                    }
                    connection = new XMPPTCPConnection(config);
                    try {
                        connection.connect();
                    } catch (SmackException | IOException | XMPPException | InterruptedException e) {
                        e.printStackTrace();
                        return null;
                    }
                    //设置重连管理器
                        /*ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
                        reconnectionManager.enableAutomaticReconnection();*/
                    //设置ping管理器
                    PingManager pingManager = PingManager.getInstanceFor(connection);
                    pingManager.setPingInterval(10);
                    /*pingManager.pingMyServer(true,1000 * 20);*/
                    pingManager.registerPingFailedListener(() -> {
                        logger.info("Failed to ping server and reconnect again");
                        reconnect();
                    });
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
