package com.sunrun.entity;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "tborg")
public class Org implements Serializable {
    private static final long serialVersionUID = 7611086524358991266L;
    @Id
    private Long orgId;
    private Long parentId;
    private LocalDateTime updateTime;
    @Column(nullable = false,unique = true)
    private Long sourceId;
    @Column(nullable = false)
    private Long domainId;
    private String name;
    private Integer sortNumber;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(Integer sortNumber) {
        this.sortNumber = sortNumber;
    }

    @Override
    public String toString() {
        return "Org{" +
                "orgId=" + orgId +
                ", parentId=" + parentId +
                ", updateTime=" + updateTime +
                ", sourceId=" + sourceId +
                ", domainId=" + domainId +
                ", name='" + name + '\'' +
                ", sortNumber=" + sortNumber +
                '}';
    }
}
