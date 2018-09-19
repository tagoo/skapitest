package com.sunrun.support.iam;

import com.sunrun.entity.Property;
import org.jivesoftware.smack.packet.Presence;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class SystemPropertyInfo {
    public static final String IAM_FIRST_SYNCHRONIZATION_INIT="iam.first.synchronization";
    public static final String OPENFIRE_FIRST_INIT = "openfire.first.init";
    public static final String ADMIN_AUTHORIZED_JIDS = "admin.authorizedJIDs";
    private static volatile ConcurrentHashMap<String,Property> properties = new ConcurrentHashMap();

    public static ConcurrentHashMap<String, Property> getProperties() {
        return properties;
    }

    public static enum Status{
        start,running,failed,success;
        public static SystemPropertyInfo.Status fromString(String string) {
            return valueOf(string.toLowerCase(Locale.US));
        }
    }
}
