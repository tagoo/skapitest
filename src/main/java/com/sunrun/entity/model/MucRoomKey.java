package com.sunrun.entity.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/*  1、该类为MucRoom的复合主键
    2、实现Serializable接口(否则会报错，错误会直接显示);
    3、在复合主键的类上，使用注解@Embeddable
    4、有默认的public无参数的构造方法
    5、重写hashCode()和equal()*/

public class MucRoomKey implements Serializable {
    private static final long serialVersionUID = -1392421455501428337L;
    private String name;
    private Long serviceID;

    public MucRoomKey() {
    }

    public MucRoomKey(String name, Long serviceID) {
        this.name = name;
        this.serviceID = serviceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getServiceID() {
        return serviceID;
    }

    public void setServiceID(Long serviceID) {
        this.serviceID = serviceID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MucRoomKey that = (MucRoomKey) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(serviceID, that.serviceID);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, serviceID);
    }

    @Override
    public String toString() {
        return "MucRoomKey{" +
                "name='" + name + '\'' +
                ", serviceID=" + serviceID +
                '}';
    }
/*  @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((serviceID == null) ? 0 : serviceID.hashCode());
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(obj == null){
            return false;
        }
        if(getClass() != obj.getClass()){
            return false;
        }

        final MucRoomKey other = (MucRoomKey)obj;
        if(serviceID == null){
            if(other.serviceID != null){
                return false;
            }
        }else if(!serviceID.equals(other.serviceID)){
            return false;
        }
        if(name == null){
            if(other.name != null){
                return false;
            }
        }else if(!name.equals(other.name)){
            return false;
        }
        return true;
    }*/
}
