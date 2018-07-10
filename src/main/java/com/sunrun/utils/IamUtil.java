package com.sunrun.utils;

import com.sunrun.common.ImDictionary;
import com.sunrun.common.config.IamConfig;
import com.sunrun.entity.Domain;
import com.sunrun.exception.IamConnectionException;
import com.sunrun.vo.*;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
                .map((u) -> {
                    Domain domain = new Domain();
                    domain.setDomainId(u.getId());
                    domain.setName(u.getName());
                    domain.setUpdateTime(Date.from(LocalDateTime.parse(u.getUpdate_time(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toInstant()));
                    return domain; })
                .collect(Collectors.toList());
    }

    public DomainVo getDomainDetails(int domainId) throws IamConnectionException{
       return restTemplate.getForObject(iamServer + iamConfig.getUrls().get("domain") + "?access_token={0}&domain_id={1}", DomainVo.class, getAccessToken(), domainId);
    }

    public void addDetails(Domain domain) throws IamConnectionException {
        DomainVo vo = getDomainDetails(domain.getDomainId());
        domain.setUpdateTime(Date.from(LocalDateTime.parse(vo.getUpdate_time(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toInstant()));
        domain.setSortNumber(vo.getSort_number());
        domain.setName(vo.getName());
        domain.setSource(ImDictionary.DOMAIN_SOURCE_IAM);
    }

    public List<UserVo> getUserList(Integer domainId) throws IamConnectionException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(ACCESS_TOKEN, accessToken);
        params.add(DOMAIN_ID, domainId.toString());
        params.add("type", "2");
        params.add("user_infos", "*");
        System.out.println( "domainId =" + domainId  +",data:" + restTemplate.postForObject(iamServer + iamConfig.getUrls().get("orgList"), params, String.class));
        return restTemplate.postForObject(iamServer + iamConfig.getUrls().get("orgList"), params, UserResultVo.class).getOrgs();
    }
    public static Date dateFormat(String dateStr) {
        return Date.from(LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toInstant());
    }

    public List<OrgVo> getOrganizationaList(Integer domainId) throws IamConnectionException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(ACCESS_TOKEN, accessToken);
        params.add(DOMAIN_ID, domainId.toString());
        params.add("type", "1");
        params.add("org_infos", "*");
        return restTemplate.postForObject(iamServer + iamConfig.getUrls().get("orgList"), params, OrganizationVo.class).getOrgs();
    }

}
