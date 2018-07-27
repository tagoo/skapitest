package com.sunrun.common.task;

import com.sunrun.common.config.IamConfig;
import com.sunrun.common.config.RestApiConfig;
import com.sunrun.dao.SystemPropertyRepository;
import com.sunrun.dao.UserRepository;
import com.sunrun.entity.User;
import com.sunrun.exception.*;
import com.sunrun.security.Operate;
import com.sunrun.service.SystemPropertyService;
import com.sunrun.support.iam.DomainSyncInfo;
import com.sunrun.support.iam.SystemPropertyInfo;
import com.sunrun.utils.IamUtil;
import com.sunrun.utils.RestApiUtil;
import com.sunrun.entity.Property;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(value = 1)
public class SkapiApplicationRunner implements ApplicationRunner {
    @Resource
    private Operate operate;
    @Resource
    private IamConfig iamConfig;
    @Resource
    private RestTemplate restTemplate;
    @Autowired
    private SystemPropertyService systemPropertyService;
    @Autowired
    private SystemPropertyRepository systemPropertyRepository;
    @Autowired
    private UserRepository userRepository;
    @Resource
    private RestApiConfig restApiConfig;
    private static final Logger logger = LoggerFactory.getLogger(SkapiApplicationRunner.class);
    @Override
    public void run(ApplicationArguments args) throws Exception {
        iamInit();
        loadAllProperties();
        openfireInit();
        synchronizeInit();
    }

    private void synchronizeInit() throws SyncOrgException, CannotFindDomain, IamConnectionException, GetUserException, NotFindMucServiceException {
        logger.info("Start synchronizing user data of the IAM server");
        Property property = SystemPropertyInfo.getProperties().get(SystemPropertyInfo.IAM_FIRST_SYNCHRONIZATION_INIT);
        if (property == null) {
            property = new Property(SystemPropertyInfo.IAM_FIRST_SYNCHRONIZATION_INIT,SystemPropertyInfo.Status.start.name());
            SystemPropertyInfo.getProperties().put(property.getKey(),property);
            systemPropertyRepository.save(property);
            operate.synchronizeData();
            if (!Boolean.parseBoolean(SystemPropertyInfo.getProperties().get(SystemPropertyInfo.IAM_FIRST_SYNCHRONIZATION_INIT).getValue())){
                operate.deleteDomainResource(DomainSyncInfo.getFailedDomains());
            }
        }
        logger.info("Successfully complete synchronizing");
    }

    private void iamInit() throws IamConnectionException {
        logger.info(String.format("Start connecting to the IAM server,host:%s,port:%d",iamConfig.getHost(),iamConfig.getPort()));
        IamUtil.getInstance().setDefaultProp(iamConfig,restTemplate);
        logger.info("Successfully connect to the IAM server," + IamUtil.getInstance().getIamServer());
    }

    private void loadAllProperties() {
        List<Property> allProperties = systemPropertyService.getAllProperties();
        allProperties.forEach(p -> SystemPropertyInfo.getProperties().put(p.getKey(),p));
    }

    private void openfireInit() throws PropertyNameEmptyException, NotFindPropertyException {
        logger.info("Start initializing the openfire's params");
        Property property = SystemPropertyInfo.getProperties().get(SystemPropertyInfo.OPENFIRE_FIRST_INIT);
        if (property == null) {
            List<Property> properties = new ArrayList<>();

            properties.add(initParams("plugin.restapi.enabled","true"));
            properties.add(initParams("plugin.restapi.httpAuth","basic"));
            properties.add(initParams("plugin.restapi.secret","o2p5J9R9Hq0rCJot"));
            properties.add(initParams("plugin.restapi.allowedIPs",""));

            properties.add(initParams("jdbcAuthProvider.useConnectionProvider","true"));
            properties.add(initParams("admin.authorizedJIDs","admin@"+ SystemPropertyInfo.getProperties().get("xmpp.domain").getValue()));
            properties.add(initParams("provider.auth.className","org.jivesoftware.openfire.auth.JDBCAuthProvider"));
            properties.add(initParams("jdbcProvider.driver",SystemPropertyInfo.getProperties().get("database.defaultProvider.driver").getValue()));
            properties.add(initParams("jdbcProvider.connectionString",getConnectionString()));
            properties.add(initParams("jdbcAuthProvider.passwordSQL","SELECT userPassword FROM tbuser WHERE userName=?"));
            properties.add(initParams("jdbcAuthProvider.passwordType","sha1"));
            properties.add(initParams("jdbcAuthProvider.setPasswordSQL","UPDATE tbuser SET userPassword=? WHERE userName=?"));
            properties.add(initParams("jdbcAuthProvider.allowUpdate","true"));

            properties.add(initParams("provider.user.className","org.jivesoftware.openfire.user.JDBCUserProvider"));
            properties.add(initParams("jdbcUserProvider.allUsersSQL","SELECT userName FROM tbuser"));
            properties.add(initParams("jdbcUserProvider.loadUserSQL","SELECT userRealName,userEmail FROM tbuser WHERE userName= ?"));
            properties.add(initParams("jdbcUserProvider.searchSQL","SELECT userName FROM tbuser WHERE"));
            properties.add(initParams("jdbcUserProvider.userCountSQL","SELECT COUNT(*) FROM tbuser"));
            properties.add(initParams("jdbcUserProvider.usernameField","userName"));
            properties.add(initParams("jdbcUserProvider.nameField","userRealName"));
            properties.add(initParams("jdbcUserProvider.emailField","userEmail"));
            properties.add(initParams("jdbcUserProvider.useConnectionProvider","true"));
            systemPropertyService.saveAll(properties);
            setDefaultAdmin();

            property = new Property(SystemPropertyInfo.OPENFIRE_FIRST_INIT,"true");
            systemPropertyService.save(property);
            SystemPropertyInfo.getProperties().put(property.getKey(),property);
            logger.info("Complete initializing the openfire's params");
        }
    }

    private void setDefaultAdmin() throws NotFindPropertyException {
        User user = new User();
        String authorizationHeader = restApiConfig.getAuthorizationHeader();
        if (authorizationHeader == null) {
            throw new NotFindPropertyException("Not found restapi.authorizationHeader in the configuration file : application.properties");
        }
        String[] split = authorizationHeader.split(":");
        user.setUserName(split[0]);
        user.setUserRealName("Administrator");
        user.setUserPassword(DigestUtils.sha1Hex(split[1].getBytes()));
        user.setDomainId(-1L);
        user.setOrgId(-1L);
        userRepository.save(user);
    }

    private String getConnectionString() {
        String serverURL = SystemPropertyInfo.getProperties().get("database.defaultProvider.serverURL").getValue();
        StringBuilder sb = new StringBuilder(serverURL.substring(0, serverURL.lastIndexOf("?") + 1));
        sb.append("user=" + SystemPropertyInfo.getProperties().get("database.defaultProvider.username").getValue())
                .append("&amp;")
                .append("password=" + SystemPropertyInfo.getProperties().get("database.defaultProvider.password").getValue());
        return sb.toString();
    }

    private Property initParams(String key, String value) {
        Property property = new Property(key, value);
        SystemPropertyInfo.getProperties().put(key,property);
        return property;
    }
}
