package com.sunrun.utils;


import com.sunrun.common.config.RestApiConfig;
import com.sunrun.entity.Property;
import com.sunrun.exception.DomainInvalidException;
import com.sunrun.utils.helper.*;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Component
public class RestApiUtil {

    @Resource
    private RestApiConfig restApiConfig;
    @Resource
    private RestTemplate restTemplate;
    private static final String OCCUPANTS ="occupants";
    private static final String PARTICIPANTS ="participants";
    public void setRestApiConfig(RestApiConfig restApiConfig) {
        this.restApiConfig = restApiConfig;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public ResponseEntity<String> createChatRoom(ChatRoom chatRoom, String serviceName) {
        HttpEntity<ChatRoom> requestEntity = new HttpEntity<>(chatRoom, getHttpHeaders(MediaType.APPLICATION_JSON_UTF8));
        return restTemplate.postForEntity(getUrlWithServiceName("room",serviceName), requestEntity, String.class);
    }

    public ChatRoom getChatRoom(String roomName,String serviceName) {
        HttpEntity<String> requestEntity = new HttpEntity<>(null, getHttpHeaders());
        try {
            return restTemplate.exchange(getUrlWithParams("room", serviceName, roomName) , HttpMethod.GET, requestEntity, ChatRoom.class).getBody();
        } catch (RestClientException e) {
            if (e.getMessage().contains("404 Not Found")){
                return null;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean updateChatRoom(ChatRoom chatRoom, String serviceName) {
        HttpEntity<ChatRoom> requestEntity = new HttpEntity<>(chatRoom, getHttpHeaders());
        ResponseEntity<String> result = restTemplate.exchange(getUrlWithParams("room", serviceName, chatRoom.getRoomName()), HttpMethod.PUT, requestEntity, String.class);
        return result.getStatusCode() == HttpStatus.OK;
    }


    public String getRoomOccupants(String roomName,String serviceName) {
        HttpEntity<String> requestEntity = new HttpEntity<>(null, getHttpHeaders());
        ResponseEntity<String> room = restTemplate.exchange(getUrlWithParams("room", serviceName, roomName, OCCUPANTS), HttpMethod.GET, requestEntity, String.class);
        return room.getBody();
    }

    public String getRoomParticipants(String roomName,String serviceName){
        HttpEntity<String> requestEntity = new HttpEntity<>(null, getHttpHeaders());
        ResponseEntity<String> room = restTemplate.exchange(getUrlWithParams("room", serviceName, roomName, PARTICIPANTS), HttpMethod.GET, requestEntity, String.class);
        return room.getBody();
    }

    public HttpHeaders getHttpHeaders(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, Base64.getEncoder().encodeToString(restApiConfig.getAuthorizationHeader().getBytes()));
        return httpHeaders;
    }

    public HttpHeaders getHttpHeaders(MediaType mediaType,List<MediaType> acceptableMediaTypes){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, Base64.getEncoder().encodeToString(restApiConfig.getAuthorizationHeader().getBytes()));
        httpHeaders.setContentType(mediaType);
        httpHeaders.setAccept(acceptableMediaTypes);
        return httpHeaders;
    }
    public HttpHeaders getHttpHeaders(MediaType mediaType){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, Base64.getEncoder().encodeToString(restApiConfig.getAuthorizationHeader().getBytes()));
        httpHeaders.setContentType(mediaType);
        return httpHeaders;
    }

    public String getUrl(String index) {
        return restApiConfig.getProtocol() + "://" + restApiConfig.getServer() + restApiConfig.getUrls().get(index);
    }

    public String getUrlWithServiceName(String index, String serviceName) {
        return serviceName == null ? getUrl(index) : getUrl(index) + "?servicename=" + serviceName;
    }
    public String getUrlWithParams(String index, String serviceName, String... pathParams) {
        StringBuilder url = new StringBuilder(getUrl(index));
        for (int i= 0 ; i < pathParams.length; i++) {
            url.append("/" + pathParams[i]);
        }
        if (serviceName != null) {
            url.append("?servicename=" +serviceName);
        }
        return url.toString();
    }

    public String getUserUrlWithParams(String index, String... pathParams) {
        StringBuilder url = new StringBuilder(getUrl(index));
        for (int i= 0 ; i < pathParams.length; i++) {
            url.append("/" + pathParams[i]);
        }
        return url.toString();
    }


    public boolean addMember(String roomName, String serviceName, Role roles, String jid) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, getHttpHeaders(MediaType.APPLICATION_XML,Arrays.asList(MediaType.APPLICATION_XML)));
        ResponseEntity<String> result = restTemplate.postForEntity(getUrlWithParams("room", serviceName, roomName, roles.name(), jid), requestEntity, String.class);
        return result.getStatusCode() == HttpStatus.CREATED;
    }

    public boolean removeMember(String roomName, String serviceName, Role role, String jid) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, getHttpHeaders(MediaType.APPLICATION_XML,Arrays.asList(MediaType.APPLICATION_XML)));
        ResponseEntity<String> result = restTemplate.exchange(getUrlWithParams("room", serviceName, roomName, role.name(), jid), HttpMethod.DELETE, requestEntity, String.class);
        return HttpStatus.OK == result.getStatusCode();
    }

    public boolean deleteChatRoom(String roomName, String serviceName) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, getHttpHeaders());
        ResponseEntity<String> result = restTemplate.exchange(getUrlWithParams("room", serviceName, roomName), HttpMethod.DELETE, requestEntity, String.class);
        return HttpStatus.OK == result.getStatusCode();
    }

    public List<ChatRoom> getAllChatRooms(String serviceName, String type, String search) throws DomainInvalidException {
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, getHttpHeaders(MediaType.APPLICATION_JSON_UTF8,Arrays.asList(MediaType.APPLICATION_JSON_UTF8)));
        StringBuilder url = new StringBuilder(getUrlWithServiceName("room", serviceName));
        if (type != null) {
            url.append(url.toString().contains("?")? "&type=" + type: "?type="+type);
        }
        if (search != null) {
            url.append(url.toString().contains("?")? "&search=" + search: "?search="+search);
        }
        if (serviceName != null && serviceName.contains("_")){
            throw new DomainInvalidException();
        }
        ResponseEntity<RoomData> result = restTemplate.exchange(url.toString(), HttpMethod.GET, requestEntity, RoomData.class);
        return result.getBody().getChatRooms();
    }

    public boolean addGroupRoleToChatRoom(String roomName, String serviceName, String groupName, Role role){
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, getHttpHeaders(MediaType.APPLICATION_XML));
        ResponseEntity<String> result = restTemplate.postForEntity(getUrlWithParams("room", serviceName, roomName, role.name(), "group", groupName), requestEntity, String.class);
        return result.getStatusCode() == HttpStatus.CREATED;
    }

    public UserData getUser(String userName) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, getHttpHeaders(MediaType.APPLICATION_JSON_UTF8));
        try {
            ResponseEntity<UserData> users = restTemplate.exchange(getUserUrlWithParams("users", userName), HttpMethod.GET, requestEntity, UserData.class);
            return users.getBody();
        } catch (RestClientException e) {
            if (e.getMessage().contains("404 Not Found")){
                return null;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean creatUser(UserData userData) {
        HttpEntity<UserData> requestEntity = new HttpEntity<>(userData, getHttpHeaders(MediaType.APPLICATION_JSON,Arrays.asList(MediaType.APPLICATION_JSON)));
        ResponseEntity<String> result = restTemplate.postForEntity(getUrl("users"), requestEntity, String.class);
        return result.getStatusCode() == HttpStatus.CREATED;
    }

    public boolean updateUser(UserData userData) {
        HttpEntity<UserData> requestEntity = new HttpEntity<>(userData, getHttpHeaders(MediaType.APPLICATION_JSON));
        ResponseEntity<String> result = restTemplate.exchange(getUserUrlWithParams("users",userData.getUsername()), HttpMethod.PUT, requestEntity, String.class);
        return result.getStatusCode() == HttpStatus.OK;
    }

    public boolean deleteUser(String userName) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, getHttpHeaders());
        ResponseEntity<String> result = restTemplate.exchange(getUserUrlWithParams("users",userName), HttpMethod.DELETE, requestEntity, String.class);
        return  result.getStatusCode() == HttpStatus.OK;
    }

    public Property getSystemProperty(String propertyName) {
        HttpEntity<String> requestEntity = new HttpEntity<>(propertyName, getHttpHeaders());
        try {
            return restTemplate.exchange(getUrl("properties") + "/{0}", HttpMethod.GET, requestEntity, Property.class, propertyName).getBody();
        } catch (RestClientException e) {
            if (e.getMessage().contains("404 Not Found")){
                return null;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Property> getAllSystemProperties() {
        HttpHeaders httpHeaders = getHttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, httpHeaders);
        return restTemplate.exchange(getUrl("properties"), HttpMethod.GET, requestEntity,PropertyEntity.class).getBody().getProperty();
    }

    public boolean updateSystemProperty(Property property) {
        HttpEntity<Property> requestEntity = new HttpEntity<>(property, getHttpHeaders(MediaType.APPLICATION_JSON_UTF8));
        return restTemplate.exchange(getUrl("properties") +"/{0}", HttpMethod.PUT, requestEntity,PropertyEntity.class,property.getKey()).getStatusCode() == HttpStatus.OK;
    }

    public boolean createSystemProperty(Property property) {
        HttpEntity<Property> requestEntity = new HttpEntity<>(property, getHttpHeaders(MediaType.APPLICATION_JSON_UTF8));
        return restTemplate.exchange(getUrl("properties"),HttpMethod.POST,requestEntity,String.class).getStatusCode() == HttpStatus.CREATED;
    }

    public boolean deleteSystemProperty(String propertyName) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, getHttpHeaders());
        return restTemplate.exchange(getUrl("properties")+"/{0}", HttpMethod.DELETE, requestEntity,PropertyEntity.class,propertyName).getStatusCode() == HttpStatus.OK;
    }
}
