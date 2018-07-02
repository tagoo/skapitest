package com.sunrun.utils;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.sunrun.common.config.RestApiConfig;
import com.sunrun.entity.User;
import com.sunrun.utils.helper.*;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<String> creatChatRoom(ChatRoom chatRoom, String serviceName) {
        HttpHeaders headers = getHttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<ChatRoom> requestEntity = new HttpEntity<>(chatRoom, headers);
        return restTemplate.postForEntity(getUrlWithServiceName("room",serviceName), requestEntity, String.class);
    }

    public ChatRoom getChatRoom(String roomName,String serviceName) {
        HttpHeaders httpHeaders = getHttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(null, httpHeaders);
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
        HttpHeaders httpHeaders = getHttpHeaders();
        HttpEntity<ChatRoom> requestEntity = new HttpEntity<>(chatRoom, httpHeaders);
        ResponseEntity<String> result = restTemplate.exchange(getUrlWithParams("room", serviceName, chatRoom.getRoomName()), HttpMethod.PUT, requestEntity, String.class);
        return result.getStatusCode() == HttpStatus.OK;
    }


    public String getRoomOccupants(String roomName,String serviceName) {
        HttpHeaders httpHeaders = getHttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> room = restTemplate.exchange(getUrlWithParams("room", serviceName, roomName, OCCUPANTS), HttpMethod.GET, requestEntity, String.class);
        return room.getBody();
    }

    public String getRoomParticipants(String roomName,String serviceName){
        HttpHeaders httpHeaders = getHttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> room = restTemplate.exchange(getUrlWithParams("room", serviceName, roomName, PARTICIPANTS), HttpMethod.GET, requestEntity, String.class);
        return room.getBody();
    }

    public HttpHeaders getHttpHeaders(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, Base64.getEncoder().encodeToString(restApiConfig.getAuthorizationHeader().getBytes()));
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
        HttpHeaders httpHeaders = getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_XML);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> result = restTemplate.postForEntity(getUrlWithParams("room", serviceName, roomName, roles.name(), jid), requestEntity, String.class);
        return result.getStatusCode() == HttpStatus.CREATED;
    }

    public boolean removeMember(String roomName, String serviceName, Role role, String jid) {
        HttpHeaders httpHeaders = getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_XML);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> result = restTemplate.exchange(getUrlWithParams("room", serviceName, roomName, role.name(), jid), HttpMethod.DELETE, requestEntity, String.class);
        return HttpStatus.OK == result.getStatusCode();
    }

    public boolean deleteChatRoom(String roomName, String serviceName) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, getHttpHeaders());
        ResponseEntity<String> result = restTemplate.exchange(getUrlWithParams("room", serviceName, roomName), HttpMethod.DELETE, requestEntity, String.class);
        return HttpStatus.OK == result.getStatusCode();
    }

    public List<ChatRoom> getAllChatRooms(String serviceName, String type, String search) {
        HttpHeaders httpHeaders = getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, httpHeaders);
        StringBuilder url = new StringBuilder(getUrlWithServiceName("room", serviceName));
        if (type != null) {
            url.append(url.toString().contains("?")? "&type=" + type: "?type="+type);
        }
        if (search != null) {
            url.append(url.toString().contains("?")? "&search=" + search: "?search="+search);
        }
        ResponseEntity<RoomData> result = restTemplate.exchange(url.toString(), HttpMethod.GET, requestEntity, RoomData.class);
        return result.getBody().getChatRooms();
    }

    public boolean addGroupRoleToChatRoom(String roomName, String serviceName, String groupName, Role role){
        HttpHeaders httpHeaders = getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> result = restTemplate.postForEntity(getUrlWithParams("room", serviceName, roomName, role.name(), "group", groupName), requestEntity, String.class);
        return result.getStatusCode() == HttpStatus.CREATED;
    }

    public UserData getUser(String userName) {
        HttpHeaders httpHeaders = getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, httpHeaders);
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
        HttpHeaders httpHeaders = getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<UserData> requestEntity = new HttpEntity<>(userData, httpHeaders);
        ResponseEntity<String> result = restTemplate.postForEntity(getUrl("users"), requestEntity, String.class);
        return result.getStatusCode() == HttpStatus.CREATED;
    }

    public boolean updateUser(UserData userData) {
        HttpHeaders httpHeaders = getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserData> requestEntity = new HttpEntity<>(userData, httpHeaders);
        ResponseEntity<String> result = restTemplate.exchange(getUserUrlWithParams("users",userData.getUsername()), HttpMethod.PUT, requestEntity, String.class);
        return result.getStatusCode() == HttpStatus.OK;
    }

    public boolean deleteUser(String userName) {
        HttpHeaders httpHeaders = getHttpHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> result = restTemplate.exchange(getUserUrlWithParams("users",userName), HttpMethod.DELETE, requestEntity, String.class);
        return  result.getStatusCode() == HttpStatus.OK;
    }
}
