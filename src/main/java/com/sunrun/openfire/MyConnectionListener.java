package com.sunrun.openfire;


import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyConnectionListener extends AbstractConnectionListener {

    private XMPPConnection connection;
    private String username;
    private String password;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public MyConnectionListener(XMPPConnection connection,String username, String password) {
        this.connection = connection;
        this.username = username;
        this.password = password;
    }

    @Override
    public void connected(XMPPConnection connection) {
        logger.info(String.format("%s connected",username));
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        logger.info(String.format("%s authenticated",username));
    }

    @Override
    public void connectionClosed() {
        logger.info(String.format("%s connectionClosed",username));
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        logger.info(String.format("%s connectionClosedOnError",username));
    }
}
