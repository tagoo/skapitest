package com.sunrun.utils.helper;

import com.sunrun.entity.Property;

import java.io.Serializable;
import java.util.List;

public class UserData implements Serializable{

    private static final long serialVersionUID = 6270797158737863554L;
    private String username;
    private String password;
    private String email;
    private String name;
    private List<Property> properties;

    public String getUsername(){
        if (username != null) {
            int index = username.indexOf("_");
            if (index > -1) {
                return username.substring(0,index);
            }
        }
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", properties=" + properties +
                '}';
    }
}
