package com.sunrun.utils;

import com.sunrun.common.ImDictionary;
import com.sunrun.common.config.IamConfig;
import com.sunrun.entity.Domain;
import com.sunrun.entity.Org;
import com.sunrun.entity.User;
import com.sunrun.exception.IamConnectionException;
import com.sunrun.vo.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class IamUtil {
    private IamConfig iamConfig;
    private RestTemplate restTemplate;
    private String key;
    private String secret;
    private String iamServer;
    private String accessToken;
    private String refreshAccessToken;
    private final String GRANT_TYPE = "grant_type";
    private final String ACCESS_SECRET = "access_secret";
    private final String ACCESS_KEY = "access_key";
    private final String ACCESS_SCOPE = "scope";
    private final String GRANT_TYPE_IS_CC = "client_credentials";
    private final String GRANT_TYPE_IS_RT = "refresh_token";
    private final String SCOPE_ALL = "iam-api:*:*:*";
    private final String ACCESS_TOKEN ="access_token";
    private final String REFRESH_TOKEN ="refresh_token";
    private final String DOMAIN_ID ="domain_id";
    private final String ORG_ID ="org_id";



    private static class InstanceFactory{
        private static IamUtil instance = new IamUtil();
    }
    public static IamUtil getInstance(){
        return InstanceFactory.instance;
    }
    private IamUtil(){}

    public void setDefaultProp(IamConfig iamConfig, RestTemplate restTemplate) throws IamConnectionException {
        this.iamConfig = iamConfig;
        this.key = iamConfig.getKey();
        this.iamServer = iamConfig.getProtocol() + "://" + iamConfig.getHost()+":"+iamConfig.getPort();
        this.secret = iamConfig.getSecret();
        this.restTemplate = restTemplate;
        this.accessToken = getAccessToken();
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getIamServer() {
        return iamServer;
    }

    public void setIamServer(String iamServer) {
        this.iamServer = iamServer;
    }

    public String  getAccessToken() throws IamConnectionException{
        if (accessToken == null) {
            synchronized (IamUtil.class) {
                if (accessToken == null) {
                    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                    params.add(GRANT_TYPE, GRANT_TYPE_IS_CC);
                    params.add(ACCESS_KEY, key);
                    params.add(ACCESS_SECRET, secret);
                    params.add(ACCESS_SCOPE, SCOPE_ALL);
                    try {
                        TokenResult result = restTemplate.postForObject(iamServer + iamConfig.getUrls().get("accessToken"), params, TokenResult.class);
                        this.accessToken = result.getAccess_token();
                        this.refreshAccessToken = result.getRefresh_token();
                        return accessToken;
                    } catch (RestClientException e) {
                        throw new IamConnectionException(e);
                    }
                }
            }
        }
        return accessToken;
    }

    public boolean validateAccessToken(String accessToken) throws IamConnectionException{
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(ACCESS_KEY, key);
        params.add(ACCESS_SECRET, secret);
        params.add(ACCESS_TOKEN, accessToken);
        try {
            HashMap<String,Object> map = restTemplate.postForObject(iamServer + iamConfig.getUrls().get("validateToken"), params, HashMap.class);
            return map.get("active") == null ? false : Boolean.parseBoolean(map.get("active").toString());
        } catch (RestClientException e) {
            throw new IamConnectionException(e);
        }
    }
    public boolean updateUser(User user) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("user_id", user.getSourceId().toString());
        params.add(ACCESS_TOKEN, accessToken);
        if (user.getUserRealName() != null) {
            params.add("real_name",user.getUserRealName());
        }
        if (user.getUserMobile() != null) {
            params.add("mobile",user.getUserMobile());
        }
        if (user.getUserPhone() != null) {
            params.add("telephone",user.getUserPhone());
        }
        if (user.getUserEmail() != null) {
            params.add("email",user.getUserEmail());
        }
        if (user.getUserAddress() != null) {
            params.add("address",user.getUserAddress());
        }
        if (user.getUserBirthday() != null) {
            params.add("birthday",user.getUserBirthday().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        if (user.getUserSex() != null) {
            params.add("sex",user.getUserSex().ordinal() +"");
        }
        if (user.getUserDescription() != null) {
            params.add("remark",user.getUserDescription());
        }
        if (user.getRank() != null) {
            params.add("rank",user.getRank());
        }
        if (user.getUserMobile2() != null) {
            params.add("mobile2",user.getUserMobile2());
        }
        if (user.getQq() != null) {
            params.add("qq",user.getQq());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params,headers);
        ResponseEntity<String> result = restTemplate.postForEntity(iamServer + iamConfig.getUrls().get("updateUser"), request, String.class);
        return result.getStatusCode() == HttpStatus.OK;
    }

    public String refreshAccessToken(String refreshToken) throws IamConnectionException{
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(GRANT_TYPE, GRANT_TYPE_IS_RT);
        params.add(ACCESS_KEY, key);
        params.add(ACCESS_SECRET, secret);
        params.add(REFRESH_TOKEN, refreshToken);
        try {
            HashMap<String,Object> map = restTemplate.postForObject(iamServer + iamConfig.getUrls().get("validateToken"), params, HashMap.class);
            return map.get(ACCESS_SECRET) == null ? null : map.get(ACCESS_SECRET).toString();
        } catch (RestClientException e) {
            throw new IamConnectionException(e);
        }
    }

    public List<Domain> getDomainList() throws IamConnectionException {
        DomainResult result = restTemplate.getForObject(iamServer + iamConfig.getUrls().get("domains") + "?access_token={1}", DomainResult.class, getAccessToken());
        List<DomainVo> domains = result.getDomains();
        return domains.stream()
                .map(u -> {
                    Domain domain = new Domain();
                    domain.setId(u.getId());
                    domain.setName(u.getName());
                    domain.setUpdateTime(TimeFormatUtil.toDate(u.getUpdate_time()));
                    return domain; })
                .collect(Collectors.toList());
    }

    public DomainVo getDomainDetails(Integer domainId) throws IamConnectionException{
        int count = 1;
        DomainVo domain = null;
        while (count <= 3 && domain == null) {
            try {
                domain = restTemplate.getForObject(iamServer + iamConfig.getUrls().get("domain") + "?access_token={0}&domain_id={1}", DomainVo.class, getAccessToken(), domainId);
            } catch (Exception e) {
                log.warn("The {} times to get the domain(id:{}) details failed.",count,domainId);
            }
            count ++;
        }
        if (domain == null) {
            throw new IamConnectionException();
        }
        return domain;
    }
    public void addDetails(Domain domain) throws IamConnectionException {
        DomainVo vo = getDomainDetails(domain.getId());
        domain.setUpdateTime(TimeFormatUtil.toDate(vo.getUpdate_time()));
        domain.setSortNumber(vo.getSort_number());
        domain.setName(vo.getName());
        domain.setSortNumber(vo.getSort_number());
        domain.setCreateTime(TimeFormatUtil.toDate(vo.getAdd_time()));
        domain.setSource(ImDictionary.DOMAIN_SOURCE_IAM);
    }

    public UserVo getUserDetails(Long userId) throws IamConnectionException{
        int count = 1;
        UserVo vo = null;
        while (count <= 3 && vo == null) {
            try {
                vo = restTemplate.getForObject(iamServer + iamConfig.getUrls().get("user") + "?access_token={0}&user_id={1}", UserVo.class, getAccessToken(), userId);
            } catch (Exception e) {
                log.warn("The {} times to get the user(id:{}) details failed.",count,userId);
            }
            count ++;
        }
        if (vo == null) {
            throw new IamConnectionException();
        }
        return vo;
    }

    public List<UserVo> getUserList(Integer domainId) throws IamConnectionException {
        return restTemplate.getForObject(iamServer + iamConfig.getUrls().get("orgList")+"?access_token={0}&domain_id={1}&type=2&depth=1", UserResultVo.class,getAccessToken(),domainId).getOrgs();
    }

    public List<UserVo> getUserList(Long orgId) {
        int count = 1;
        List<UserVo> list = null;
        while (count <= 3 && list == null) {
            try {
                list = restTemplate.getForObject(iamServer + iamConfig.getUrls().get("orgList") + "?access_token={0}&org_id={1}&type=2&depth=1", UserResultVo.class, getAccessToken(), orgId).getOrgs();
                break;
            }catch (Exception e) {
                log.warn("The {} times to get the user data in the org(id:{}) failed.",count,orgId);
            }
            count ++;
        }
        return list;
    }

    public List<OrgVo> getOrganizationaList(Integer domainId) throws IamConnectionException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(ACCESS_TOKEN, getAccessToken());
        params.add(DOMAIN_ID, domainId.toString());
        params.add("type", "1");
        params.add("org_infos", "*");
        int count = 1;
        List<OrgVo> list = null;
        while (count <= 3 && list == null) {
            try {
                list = restTemplate.postForObject(iamServer + iamConfig.getUrls().get("orgList"), params, OrganizationVo.class).getOrgs();
                break;
            }catch (Exception e) {
                log.warn("The {} times to get the org data in the domain(id:{}) failed.",count,domainId);
            }
            count ++;
        }
        return list;
    }


    public boolean isNeedUpdate(OrgVo vo, Map<Long, Org> orgDictionary) {
        if (vo != null) {
            if (orgDictionary.get(vo.getId()) == null) {
                return true;
            }
            return isNeedTimeUpdate(vo.getUpdate_time(),orgDictionary.get(vo.getId()).getUpdateTime());
        }
        return false;
    }


    public boolean isNeedUpdate(Domain domain, Map<Integer, Domain> localDomains) {
        if (domain != null) {
            if (localDomains.get(domain.getId()) == null) {
                return true;
            }
            return isNeedTimeUpdate(domain.getUpdateTime(),localDomains.get(domain.getId()).getUpdateTime());
        }
        return false;
    }

    public boolean isNeedTimeUpdate(Date source,Date dest) {
        if (source == null) {
            return dest != null;
        } else {
            return !source.equals(dest);
        }
    }

    public boolean isNeedTimeUpdate(LocalDateTime before, LocalDateTime after) {
        if (before == null) {
            return after != null;
        } else {
            return !before.isEqual(after);
        }
    }

    public boolean isNameUpdate(String beforeName, String afterName) {
        if (StringUtils.hasText(beforeName) && StringUtils.hasText(afterName)) {
            return !beforeName.equals(afterName);
        } else {
            throw new NullPointerException("the name of domain can not be null");
        }

    }
}
