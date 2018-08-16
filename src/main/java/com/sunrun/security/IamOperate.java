package com.sunrun.security;

import com.sunrun.common.config.IamConfig;
import com.sunrun.dao.DomainRepository;
import com.sunrun.dao.MucServiceRepository;
import com.sunrun.dao.OrgRepository;
import com.sunrun.dao.UserRepository;
import com.sunrun.entity.Domain;
import com.sunrun.entity.MucService;
import com.sunrun.entity.Org;
import com.sunrun.entity.User;
import com.sunrun.exception.*;
import com.sunrun.service.SystemPropertyService;
import com.sunrun.support.iam.DomainSyncInfo;
import com.sunrun.support.iam.SystemPropertyInfo;
import com.sunrun.utils.IDGenerator;
import com.sunrun.utils.IamUtil;
import com.sunrun.entity.Property;
import com.sunrun.utils.XmppConnectionUtil;
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
    @Autowired
    private MucServiceRepository mucServiceRepository;
    @Resource
    private SystemPropertyService systemPropertyService;
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
    @Transactional(rollbackFor = Exception.class)
    public boolean synchronizeData() throws IamConnectionException, NotFindMucServiceException, SyncOrgException, CannotFindDomain, GetUserException {
        List<Domain> domainList = domainRepository.findAll();
        final Map<Integer, Domain> localDomains = Collections.synchronizedMap(new HashMap<>());
        if (!domainList.isEmpty()) {
            domainList.forEach(u -> localDomains.put(u.getId(), u));
            DomainSyncInfo.getNeedDeleteDomains().addAll(domainList);
        }
        List<Domain> remoteDomains = IamUtil.getInstance().getDomainList();
        Property systemProperty = SystemPropertyInfo.getProperties().get(SystemPropertyInfo.IAM_FIRST_SYNCHRONIZATION_INIT);
        boolean flag = true;
        //首次同步采取多线程提升效率，但是无法开启事务，需要程序级别过滤
        if (systemProperty == null || SystemPropertyInfo.Status.start == (SystemPropertyInfo.Status.fromString(systemProperty.getValue()))) {
            systemProperty = new Property(SystemPropertyInfo.IAM_FIRST_SYNCHRONIZATION_INIT,SystemPropertyInfo.Status.running.name());
            SystemPropertyInfo.getProperties().put(systemProperty.getKey(),systemProperty);
            /*ExecutorService executorService = Executors.newFixedThreadPool(remoteDomains.size());*/
            ForkJoinPool executorService = new ForkJoinPool(remoteDomains.size());
            Map<Domain,Future<Boolean>> futureList = new HashMap<>(remoteDomains.size());
            try {
                for (Domain domain : remoteDomains) {
                    if (IamUtil.getInstance().isNeedUpdate(domain,localDomains)) {
                        Future<Boolean> submit = executorService.submit(() -> {
                            logger.info(String.format("Starting update the domain that ID is %d,its name is %s", domain.getId(), domain.getName()));
                            if (localDomains.containsKey(domain.getId())) {
                                IamUtil.getInstance().addDetails(domain);
                                domainRepository.save(domain);
                                doNameUpdate(domain, localDomains);
                                doSynchronizeInOrg(domain);
                                return false;
                            } else {
                                try {
                                    IamUtil.getInstance().addDetails(domain);
                                    domainRepository.save(domain);
                                    doSaveInDomain(domain);
                                    createMucService(domain);
                                    return true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return false;
                                }
                            }
                        });
                        futureList.put(domain,submit);
                    }
                    DomainSyncInfo.getNeedDeleteDomains().remove(localDomains.get(domain.getId()));
                }
                for (Map.Entry<Domain, Future<Boolean>> entry :futureList.entrySet()) {
                    Domain domainKey = entry.getKey();
                    try {
                        if (entry.getValue().get()) {
                            DomainSyncInfo.putSuccessDomain(domainKey);
                            logger.info(String.format("Complete synchronization the domain(name:%s,id:%d)", domainKey.getName(), domainKey.getId()));
                        } else {
                            DomainSyncInfo.putFailedDomain(domainKey);
                            flag = false;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        DomainSyncInfo.putFailedDomain(domainKey);
                        flag = false;
                    }
                }
                if (!DomainSyncInfo.getNeedDeleteDomains().isEmpty()) {
                    deleteDomainResource(DomainSyncInfo.getNeedDeleteDomains());
                }
            }catch (Exception e) {
                flag = false;
                throw new RuntimeException(e);
            } finally {
                executorService.shutdown();
                if (flag) {
                    DomainSyncInfo.clearDomains();
                }
                if (SystemPropertyInfo.Status.running == SystemPropertyInfo.Status.fromString(systemProperty.getValue())){
                    Property property = entityManager.find(Property.class, systemProperty.getKey());
                    if (flag) {
                        systemProperty.setValue(SystemPropertyInfo.Status.success.toString());
                    } else {
                        systemProperty.setValue(SystemPropertyInfo.Status.failed.toString());
                    }
                    property.setValue(systemProperty.getValue());
                } else {
                    try {
                        if (flag) {
                            systemProperty.setValue(SystemPropertyInfo.Status.success.toString());
                        } else {
                            systemProperty.setValue(SystemPropertyInfo.Status.failed.toString());
                        }
                        systemPropertyService.update(systemProperty);
                    } catch (PropertyNameEmptyException | NotFindPropertyException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } else {
            for (Domain domain : remoteDomains) {
                if (IamUtil.getInstance().isNeedUpdate(domain,localDomains)) {
                    doSynchronizeInDomain(domain, localDomains);
                }
                DomainSyncInfo.getNeedDeleteDomains().remove(localDomains.get(domain.getId()));
            }
            if (!DomainSyncInfo.getNeedDeleteDomains().isEmpty()) {
                deleteDomainResource(DomainSyncInfo.getNeedDeleteDomains());
                DomainSyncInfo.clearDomains();
            }
        }
        return flag;
    }

    private void doSynchronizeInDomain(Domain domain, Map<Integer, Domain> localDomains) throws IamConnectionException, NotFindMucServiceException, SyncOrgException, GetUserException {
        if (localDomains.containsKey(domain.getId())) {
            logger.info(String.format("Starting update the domain that ID is %d,its name is %s", domain.getId(), domain.getName()));
            IamUtil.getInstance().addDetails(domain);
            Domain save = domainRepository.save(domain);
            doNameUpdate(save, localDomains);
            doSynchronizeInOrg(save);
        }  else {
            IamUtil.getInstance().addDetails(domain);
            Domain save = domainRepository.save(domain);
            doSaveInDomain(save);
            createMucService(save);
        }
    }

    private void doSaveInDomain(Domain domain) throws IamConnectionException, SyncOrgException {
        Map<Long, Org> orgDictionary = new HashMap<>();
        List<OrgVo> sources = getAllOrgVoList(IamUtil.getInstance().getOrganizationaList(domain.getId())).stream().sorted(Comparator.comparing(OrgVo::getId)).collect(Collectors.toList());
        List<Org> orgList = new ArrayList<>();
        if (sources != null && !sources.isEmpty()) {
            sources.forEach((u)->packOrg(orgList, orgDictionary, u, domain.getId()));
        }
        if (!orgList.isEmpty()) {
            List<Org> orgs = orgRepository.saveAll(orgList);
            for (Org r : orgs) {
                try {
                    saveUserInSingleOrg(r, orgDictionary, domain);
                    logger.info(String.format("Complete the users data of the org(name:%s,id:%d) insertion", r.getName(), r.getSourceId()));
                } catch (GetUserException e) {
                    logger.warn(String.format("Failed to Sync org(name:%s,id:%d) data of the domain(name:%s,id:%d).", r.getName(), r.getSourceId(), domain.getName(), domain.getId()));
                    String message = String.format("同步域(%s)中的部门出现异常", domain.getName());
                    throw new SyncOrgException(message, e);
                }
            }
        }
    }

    private void doSynchronizeInOrg(Domain domain) throws IamConnectionException, GetUserException {
        List<OrgVo> orgVoList = getAllOrgVoList(IamUtil.getInstance().getOrganizationaList(domain.getId())).stream().sorted(Comparator.comparing(OrgVo::getId)).collect(Collectors.toList());
        List<Org> localOrgList = orgRepository.findByDomainId(domain.getId());
        List<Long> needDeleteOrgList = localOrgList.stream().map(org -> org.getSourceId()).collect(Collectors.toList());
        Map<Long, Org> localOrgDictionary = localOrgList.stream().collect(Collectors.toMap(r -> r.getSourceId(), r -> r));
        Map<Long, Org> copy = new HashMap<>();
        copy.putAll(localOrgDictionary);
        List<Org> needUpdateOrgList = new ArrayList<>();
        if (orgVoList != null && !orgVoList.isEmpty()) {
            for (OrgVo vo : orgVoList) {
                if (IamUtil.getInstance().isNeedUpdate(vo, localOrgDictionary)) {
                    packOrg(needUpdateOrgList, localOrgDictionary, vo, domain.getId());
                }
                needDeleteOrgList.remove(vo.getId());
            }
        }
        if (!needUpdateOrgList.isEmpty()) {
            for (Org org : needUpdateOrgList) {
                if (copy.containsKey(org.getSourceId())) {
                    Org save = orgRepository.save(org);
                    updateUserInSingleOrg(save);
                } else {
                    Org save = orgRepository.save(org);
                    saveUserInSingleOrg(save, localOrgDictionary, domain);
                }
            }
        }
        if (!needDeleteOrgList.isEmpty()) {
            orgRepository.deleteInBatch(needDeleteOrgList.stream().map(id -> localOrgDictionary.get(id)).collect(Collectors.toList()));
            for (Long sourceId : needDeleteOrgList) {
                Org org = localOrgDictionary.get(sourceId);
                logger.info(String.format("Delete local users of the org(name:%s,id:%d)", org.getName(), org.getSourceId()));
                userRepository.deleteByOrgId(org.getOrgId());
            }
        }
    }

    private List<OrgVo> getAllOrgVoList(List<OrgVo> sources) {
        List<OrgVo> list = new ArrayList<>();
        if (null != sources && !sources.isEmpty()) {
            sources.forEach(o -> {
                list.add(o);
                if (o.getChildren() != null &&  !o.getChildren().isEmpty()) {
                    list.addAll(getAllOrgVoList(o.getChildren()));
                }
            });
        }
        return list;
    }

    private void doNameUpdate(Domain domain, Map<Integer, Domain> localDomains) throws NotFindMucServiceException {
        if (IamUtil.getInstance().isNameUpdate(domain.getName(), localDomains.get(domain.getId()).getName())) {
            Optional<MucService> mucService = mucServiceRepository.findById(domain.getName());
            if (mucService.isPresent()) {
                createMucService(mucService.get(), domain);
            } else {
                throw new NotFindMucServiceException("不存在与域（" + domain.getName()+")对应的MUC服务");
            }
        }
    }


    private void updateUserInSingleOrg(Org save) throws IamConnectionException {
        List<User> localUsers = userRepository.findByOrgId(save.getOrgId());
        List<Long> needDeleteUsers = localUsers.stream().map(u -> u.getSourceId()).collect(Collectors.toList());
        Map<Long, User> collect = localUsers.stream().collect(Collectors.toMap(u -> u.getSourceId(), u -> u));
        List<UserVo> userList = IamUtil.getInstance().getUserList(save.getSourceId());
        if (userList != null && !userList.isEmpty()) {
            for (UserVo vo : userList) {
                if (collect.containsKey(vo.getId())) {
                    if (IamUtil.getInstance().isNeedTimeUpdate(vo.getUpdate_time(), collect.get(vo.getId()).getUpdateTime())) {
                        User user = packUser(vo.getId(), save);
                        user.setId(collect.get(vo.getId()).getId());
                        userRepository.save(user);
                    }
                } else {
                    User user = packUser(vo.getId(), save);
                    userRepository.save(user);
                }
                needDeleteUsers.remove(vo.getId());
            }
        }
        if (!needDeleteUsers.isEmpty()) {
            userRepository.deleteInBatch(needDeleteUsers.stream().map(u ->  collect.get(u)).collect(Collectors.toList()));
        }
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
        DomainSyncInfo.clearDomains();
        return true;
    }

    private void saveUserInSingleOrg(Org org, Map<Long, Org> orgDictionary, Domain domain) throws GetUserException  {

        List<UserVo> userSources = null;
        try {
            userSources = IamUtil.getInstance().getUserList(org.getSourceId());
        } catch (Exception e) {
            logger.error(String.format("Failed to Get user data of the org(name:%s,id:%d) from IAM server",org.getName(),org.getSourceId()));
            throw new GetUserException(e);
        }
        if (userSources != null && !userSources.isEmpty()) {
            List<User> userList = new ArrayList<>();
            userSources.forEach(u -> userList.add(packUser(u.getId(),org)));
            if (!userList.isEmpty()) userRepository.saveAll(userList);
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
    private void createMucService(MucService source, Domain domain) {
        try {
            MucService mucService = new MucService();
            mucService.setSubdomain(domain.getName());
            mucService.setServiceID(source.getServiceID());
            mucService.setIsHidden(false);
            mucServiceRepository.save(mucService);
            mucServiceRepository.delete(source);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Create mucService(%s) exception",domain.getName()));
        }
    }
    private User packUser(Long userId, Org org) {
        User user = new User();
        /*user.setUserName(JidGenerator.generate(u.getName(),domain.getName()));*/
        UserVo u = null;
        try {
            u = IamUtil.getInstance().getUserDetails(userId);
        } catch (IamConnectionException e) {
            logger.error(String.format("Failed to get the user(id:%d) details of the org(name:%s)"),userId,org.getName());
            throw new RuntimeException(e);
        }
        user.setUserName(u.getName());
        user.setDomainId(org.getDomainId());
        /*user.setOrgId(org.getOrgId());*/
        user.setOrg(org);
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
        user.setUserPassword(DigestUtils.sha1Hex(XmppConnectionUtil.defaultPassword.getBytes()));
        return user;
    }


   /* private User packUser(Map<Long, Org> orgDictionary, Long userId, Domain domain) {
        User user = new User();
        *//*user.setUserName(JidGenerator.generate(u.getName(),domain.getName()));*//*
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
    }*/


    private void packOrg(List<Org> orgList, Map<Long,Org> orgDictionary,  OrgVo u, Integer domainId) {
        Org org = new Org();
        if (orgDictionary.containsKey(u.getId())) {
            org.setOrgId(orgDictionary.get(u.getId()).getOrgId());
        } else {
            org.setOrgId(IDGenerator.getLongId());
        }
        org.setSourceId(u.getId());
        org.setDomainId(domainId);
        org.setName(u.getName());
        org.setSortNumber(u.getSort_number());
        org.setUpdateTime(u.getUpdate_time());
        org.setParentId(u.getParent_id() == 0 ? 0 : orgDictionary.get(u.getParent_id()).getOrgId());
        orgList.add(org);
        if (!orgDictionary.containsKey(u.getId())) {
            orgDictionary.put(org.getSourceId(),org);
        }
        /*if (u.getChildren() != null &&  !u.getChildren().isEmpty()) {
            u.getChildren().forEach(v -> packOrg(orgList, orgDictionary, v, domainId));
        }*/
    }

    private void packOrg(OrgVo vo, List<Long> needDeleteOrgs) {
        needDeleteOrgs.remove(vo.getId());
        if (vo.getChildren() != null &&  !vo.getChildren().isEmpty()) {
            vo.getChildren().forEach(v -> packOrg(v, needDeleteOrgs));
        }
    }


}
