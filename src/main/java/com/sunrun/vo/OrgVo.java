package com.sunrun.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class OrgVo {
    private Long id;
    private Long parent_id;
    private String name;
    private Integer sort_number;
    private Long org_id;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime update_time;
    private String type;
    private List<OrgVo> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort_number() {
        return sort_number;
    }

    public void setSort_number(Integer sort_number) {
        this.sort_number = sort_number;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public LocalDateTime  getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(LocalDateTime  update_time) {
        this.update_time = update_time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<OrgVo> getChildren() {
        return children;
    }

    public void setChildren(List<OrgVo> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "OrgVo{" +
                "id=" + id +
                ", parent_id=" + parent_id +
                ", name='" + name + '\'' +
                ", sort_number=" + sort_number +
                ", org_id=" + org_id +
                ", update_time='" + update_time + '\'' +
                ", type='" + type + '\'' +
                ", children=" + children +
                '}';
    }
}
