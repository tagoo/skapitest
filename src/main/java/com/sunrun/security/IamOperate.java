package com.sunrun.security;

import com.sunrun.common.config.IamConfig;
import com.sunrun.dao.DomainRepository;
import com.sunrun.dao.OrgRepository;
import com.sunrun.dao.UserRepository;
import com.sunrun.entity.Domain;
import com.sunrun.entity.Org;
import com.sunrun.entity.User;
import com.sunrun.exception.IamConnectionException;
import com.sunrun.exception.SycnOrgException;
import com.sunrun.exception.SyncUserException;
import com.sunrun.utils.IDGenerator;
import com.sunrun.utils.IamUtil;
import com.sunrun.utils.JidGenerator;
import com.sunrun.vo.IamValidateRespData;
import com.sunrun.vo.OrgVo;
import com.sunrun.vo.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class IamOperate implements Operate,EnvironmentAware{
    private Logger logger = LoggerFactory.getLogger(IamOperate.class);
    private Environment environment;
    @Resource
    private IamConfig iamConfig;
    @Resource
    private RestTemplate restTemplate;
    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private OrgRepository orgRepository;
    @Autowired
    private UserRepository userRepository;
    private final String VALIDATE_URL = "validate";
    @Override
    public boolean accessLogin(User user, String serviceTicket) throws IamConnectionException {
        IamValidateRespData body = null;
        try {
            StringBuffer url = new StringBuffer(iamConfig.getProtocol()+"://");
            url.append(iamConfig.getServer()).append(iamConfig.getUrls().get(VALIDATE_URL));
            body = restTemplate.getForEntity(url.toString() + "?st={0}&service={1}", IamValidateRespData.class, serviceTicket, iamConfig.getService()).getBody();

        } catch (RestClientException e) {
            logger.error("Failed to connect to the Iam,server: " + iamConfig.getServer());
            throw new IamConnectionException(e);
        }
        return body.getUser_id() != null;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public boolean synchronizeData() throws IamConnectionException {
        IamUtil iamUtil = new IamUtil(iamConfig, restTemplate);
        List<Domain> domainList = domainRepository.findAll();
        Map<Integer, Domain> localDomains = null;
        if (!domainList.isEmpty()) {
            localDomains = domainList.stream().collect(Collectors.toMap(u -> u.getDomainId(), u -> u));
        }
        for (Domain domain : iamUtil.getDomainList()) {
            if(null != localDomains && localDomains.containsKey(domain.getDomainId())) {
                if (domain.isNeedUpdate(localDomains.get(domain.getDomainId()).getUpdateTime())) {
                    logger.info(String.format("Starting update the domain that ID is %d,its name is %s",domain.getDomainId(),domain.getName()));
                    iamUtil.addDetails(localDomains.get(domain.getDomainId()));
                    domainRepository.saveAndFlush(domain);
                }
            } else {
                iamUtil.addDetails(domain);
                Domain save = domainRepository.save(domain);
                Map<Long,Org> orgDictionary = new HashMap<>();
                try {
                    List<OrgVo> sources = iamUtil.getOrganizationaList(domain.getDomainId());
                    if (sources!= null && !sources.isEmpty()) {
                        List<Org> orgList = new ArrayList<>();
                        sources.forEach(u -> packOrg(orgList, orgDictionary, u, save.getId()));
                        if (!orgList.isEmpty()) orgRepository.saveAll(orgList);
                    }
                } catch (IamConnectionException e) {
                    String message = String.format("同步域(%s)中的部门出现异常",save.getName());
                    logger.error(message);
                    throw new SycnOrgException(message,e);
                }
                try {
                    List<UserVo> userSources = iamUtil.getUserList(save.getDomainId());
                    if (userSources != null && !userSources.isEmpty()) {
                        List<User> userList = new ArrayList<>();
                        userSources.forEach(u -> {
                            packUser(userList,orgDictionary,u,save);
                        });
                        if (!userList.isEmpty()) userRepository.saveAll(userList);
                    }
                } catch (IamConnectionException e) {
                    String message = String.format("同步域(%s)中的用户出现异常",save.getName());
                    logger.error(message);
                   throw new SyncUserException(message,e);
                }
            }
        }
        return false;
    }

    private void packUser(List<User> userList, Map<Long, Org> orgDictionary, UserVo u, Domain domain) {
        User user = new User();
        user.setUserName(JidGenerator.generate(u.getName(),domain.getName()));
        user.setDomainId(domain.getId());
        user.setOrgId(orgDictionary.get(u.getOrg_id()).getOrgId());
        user.setUpdateTime(u.getUpdate_time());
        user.setRegisterDate(u.getAdd_time());
        user.setSortNumber(u.getSort_number());
        user.setUserBirthday(u.getBirthday());
        user.setUserRealName(u.getReal_name());
        user.setUserEmail(u.getEmail());
        user.setUserPhone(u.getTelephone());
        user.setUserMobile(u.getMobile());
        user.setUserSex(u.getSex());
        user.setUserState(u.getIs_enabled());
        user.setSourceId(u.getId());
        user.setIamUserHead(u.getHead());
        userList.add(user);
    }


    private void packOrg(List<Org> orgList, Map<Long,Org> orgDictionary,  OrgVo u, Long domainId) {
        Org org = new Org();
        org.setOrgId(IDGenerator.getLongId());
        org.setSourceId(u.getId());
        org.setDomainId(domainId);
        org.setName(u.getName());
        org.setSortNumber(u.getSort_number());
        org.setUpdateTime(u.getUpdate_time());
        org.setParentId(u.getParent_id() == 0 ? 0 : orgDictionary.get(u.getParent_id()).getOrgId());
        orgList.add(org);
        orgDictionary.put(org.getSourceId(),org);
        if (u.getChildren() != null &&  !u.getChildren().isEmpty()) {
            u.getChildren().forEach(v -> packOrg(orgList, orgDictionary, v, domainId));
        }
    }


}
