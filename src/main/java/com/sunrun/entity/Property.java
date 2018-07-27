package com.sunrun.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ofproperty")
public class Property {
    @Id
    @Column(name = "name")
    private String key;
    @Column(name = "propValue")
    private String value;
    @Column(name = "encrypted")
    private Integer encrypted;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(Integer encrypted) {
        this.encrypted = encrypted;
    }

    public Property(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Property() {
    }

    @Override
    public String toString() {
        return "Property{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", encrypted=" + encrypted +
                '}';
    }
}
