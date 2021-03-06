package com.sunrun.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "ofmucservice")
public class MucService {
    @Column(name = "serviceID")
    @GeneratedValue(generator = "snowflakeId")
    @GenericGenerator(name = "snowflakeId",strategy = "com.sunrun.utils.IDGenerator")
    private Long serviceID;
    @Id
    private String subdomain;
    private String description;
    @Column(name = "isHidden", length = 4)
    private Boolean isHidden;

    public MucService() {
    }

    public Long getServiceID() {
        return serviceID;
    }

    public void setServiceID(Long serviceID) {
        this.serviceID = serviceID;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Boolean isHidden) {
        this.isHidden = isHidden;
    }

    @Override
    public String toString() {
        return "MucService{" +
                "serviceID=" + serviceID +
                ", subdomain='" + subdomain + '\'' +
                ", description='" + description + '\'' +
                ", isHidden=" + isHidden +
                '}';
    }
}
