package com.sunrun.common.config;

import com.sunrun.common.OpenfireSystemProperties;
import com.sunrun.exception.NotFindPropertyException;
import com.sunrun.service.SystemPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "openfire")
public class OfConfigInit {
    private String host;
    private int port;
    @Autowired private SystemPropertyService systemPropertyService;
    @Bean
    public OfConfig ofConfig() throws NotFindPropertyException {
        OfConfig ofConfig = new OfConfig();
        ofConfig.setDomainName(systemPropertyService.findPropertyByPropertyName(OpenfireSystemProperties.XMPP_DOMAIN).getValue());
        ofConfig.setHost(host);
        ofConfig.setPort(port);
        return ofConfig;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
