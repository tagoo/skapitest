package com.sunrun.security;

import com.sunrun.common.config.IamConfig;
import com.sunrun.common.notice.ReturnData;
import com.sunrun.dao.DomainRepository;
import com.sunrun.dao.MucServiceRepository;
import com.sunrun.dao.OrgRepository;
import com.sunrun.dao.UserRepository;
import com.sunrun.entity.Domain;
import com.sunrun.entity.MucService;
import com.sunrun.entity.Org;
import com.sunrun.entity.User;
import com.sunrun.exception.CannotFindDomain;
import com.sunrun.exception.GetUserException;
import com.sunrun.exception.IamConnectionException;
import com.sunrun.exception.SycnOrgException;
import com.sunrun.utils.IDGenerator;
import com.sunrun.utils.IamUtil;
import com.sunrun.vo.IamValidateRespData;
import com.sunrun.vo.OrgVo;
import com.sunrun.vo.UserVo;
import org.apache.commons.codec.digest.DigestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import javax.annotation.Resource;
import javax.persistence.*;
import java.util.*;
import java.util.concurrent.*;

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
    @Autowired
    private MucServiceRepository mucServiceRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private final String VALIDATE_URL = "validate";
    @Override
    public boolean accessLogin(User user, String serviceTicket) throws IamConnectionException {
        IamValidateRespData body = null;
        try {
            StringBuffer url = new StringBuffer(IamUtil.getInstance().getIamServer());
            url.append(iamConfig.getUrls().get(VALIDATE_URL));
            body = restTemplate.getForEntity(url.toString() + "?st={0}&service={1}", IamValidateRespData.class, serviceTicket, iamConfig.getService()).getBody();

        } catch (RestClientException e) {
            logger.error("Failed to connect to the Iam,server: " + iamConfig.getHost());
            throw new IamConnectionException(e);
        }
        return body.getUser_id() != null;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public ReturnData synchronizeData() throws IamConnectionException {
        ReturnData returnData = new ReturnData();
        Map<String,List<Domain>> map = Collections.synchronizedMap(new HashMap<String,List<Domain>>());
        List<Domain> domainList = domainRepository.findAll();
        final Map<Long, Domain> localDomains = Collections.synchronizedMap(new HashMap<>());
        if (!domainList.isEmpty()) {
            domainList.forEach(u -> localDomains.put(u.getId(), u));
            /*localDomains = domainList.stream().collect(Collectors.toMap(u -> u.getDomainId(), u -> u));*/
        }
        List<Domain> remoteDomains = IamUtil.getInstance().getDomainList();
        ExecutorService executorService = Executors.newFixedThreadPool(remoteDomains.size());
        Map<Domain,Future<Boolean>> futureList = new HashMap<>(remoteDomains.size());
        boolean flag = true;
        try {
            for (Domain domain : remoteDomains) {
                Future<Boolean> submit = executorService.submit(() -> {
                    if (localDomains.containsKey(domain.getId())) {
                        if (domain.isNeedUpdate(localDomains.get(domain.getId()).getUpdateTime())) {
                            logger.info(String.format("Starting update the domain that ID is %d,its name is %s", domain.getId(), domain.getName()));
                            IamUtil.getInstance().addDetails(localDomains.get(domain.getId()));
                            domainRepository.saveAndFlush(domain);
                        }
                        return false;
                    } else {
                        try {
                            IamUtil.getInstance().addDetails(domain);
                            domainRepository.saveAndFlush(domain);
                            Map<Long, Org> orgDictionary = new HashMap<>();
                            List<OrgVo> sources = IamUtil.getInstance().getOrganizationaList(domain.getId());
                            if (sources != null && !sources.isEmpty()) {
                                List<Org> orgList = new ArrayList<>();
                                for (OrgVo u : sources) {
                                    try {
                                        packOrg(orgList, orgDictionary, u, domain.getId());
                                        if (!orgList.isEmpty()) {
                                            orgRepository.saveAll(orgList);
                                            for (Org r : orgList) {
                                                saveUserInSingleOrg(r, orgDictionary, domain);
                                            }
                                            orgList.clear();
                                        }
                                        logger.info(String.format("Complete the users data of the org(name:%s,id:%d) insertion", u.getName(), u.getId()));
                                    } catch (Exception e) {
                                        logger.warn(String.format("Failed to Sync org(name:%s,id:%d) data of the domain(name:%s,id:%d).", u.getName(), u.getId(), domain.getName(), domain.getName()));
                                        String message = String.format("同步域(%s)中的部门出现异常", domain.getName());
                                        throw new SycnOrgException(message, e);
                                    }
                                }
                            }
                            createMucService(domain);
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    }
                });
                futureList.put(domain,submit);
            }
            List<Domain> failedDomains = Collections.synchronizedList(new ArrayList<Domain>());
            List<Domain> successDomains = Collections.synchronizedList(new ArrayList<Domain>());
            for (Map.Entry<Domain, Future<Boolean>> entry :futureList.entrySet()) {
                Domain domainKey = entry.getKey();
                try {
                    if (entry.getValue().get()) {
                        successDomains.add(domainKey);
                        logger.info("Complete synchronization the domain(name:%s,id:%d)", domainKey.getName(), domainKey.getId());
                    } else {
                        failedDomains.add(domainKey);
                        flag = false;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    failedDomains.add(domainKey);
                    flag = false;
                }
            }
            map.put("successDomains",successDomains);
            map.put("failedDomains",failedDomains);
            returnData.setData(map);
        }catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
        returnData.setSuccess(flag);
        return returnData;
    }


    private boolean checkDomainResourceExist(Domain save) {
        boolean flag = false;
        flag = userRepository.selectCountByDomainId(save.getId()) > 0;
        if (!flag) {
            flag = orgRepository.selectCountByDomainId(save.getId()) > 0;
        }
        if (!flag) {
            flag = domainRepository.existsById(save.getId());
        }
        return flag;
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean deleteDomainResource(List<Domain> domains) throws CannotFindDomain {
        if (domains == null || domains.isEmpty()) {
            throw new CannotFindDomain();
        }
        for (Domain save: domains) {
            if (checkDomainResourceExist(save)) {
                Domain domain = entityManager.find(Domain.class, save.getId());
                if (domain != null) {
                    entityManager.remove(domain);
                }
                userRepository.deleteByDomainId(save.getId());
                orgRepository.deleteByDomainId(save.getId());
            }
            if (checkDomainResourceExist(save)){
                logger.error(String.format("Failed to delete the domain(name:%s,id:%d)",save.getName(),save.getId()));
                return false;
            }
        }
        return true;
    }

    private void saveUserInSingleOrg(Org org, Map<Long, Org> orgDictionary, Domain domain) throws GetUserException  {
        /*userRepository.findByOrgId(org.getOrgId());*/
        Optional<Org> local = orgRepository.findById(org.getOrgId());
        if (local.isPresent()) {
            List<UserVo> userSources = null;
            try {
                userSources = IamUtil.getInstance().getUserList(local.get().getSourceId());
            } catch (Exception e) {
                logger.error(String.format("Failed to Get user data of the org(name:%s,id:%d) from IAM server",org.getName(),org.getSourceId()));
                throw new GetUserException(e);
            }
            if (userSources != null && !userSources.isEmpty()) {
                List<User> userList = new ArrayList<>();
                userSources.forEach(u -> {
                    userList.add(packUser(orgDictionary,u.getId(),domain));
                });
                if (!userList.isEmpty()) userRepository.saveAll(userList);
            }
        }
    }

    private void createMucService(Domain domain) {
        try {
            MucService mucService = new MucService();
            mucService.setSubdomain(domain.getName());
            mucService.setIsHidden(false);
            mucService.setServiceID(IDGenerator.getLongId());
            mucServiceRepository.save(mucService);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Create mucService(%s) exception",domain.getName()));
        }
    }

    private User packUser(Map<Long, Org> orgDictionary, Long userId, Domain domain) {
        User user = new User();
        /*user.setUserName(JidGenerator.generate(u.getName(),domain.getName()));*/
        UserVo u = null;
        try {
            u = IamUtil.getInstance().getUserDetails(userId);
        } catch (IamConnectionException e) {
            logger.error(String.format("Failed to get the user(id:%d) details of the domain(domainName:%s)"),userId,domain.getName());
           throw new RuntimeException(e);
        }
        user.setUserName(u.getName());
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
        user.setUserPassword(DigestUtils.sha1Hex("123456".getBytes()));
        return user;
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
