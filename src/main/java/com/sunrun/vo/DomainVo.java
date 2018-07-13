package com.sunrun.vo;

public class DomainVo {
    private Long id;
    private String name;
    private Integer sort_number;
    private String update_time;
    private String add_time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    @Override
    public String toString() {
        return "DomainVo [id=" + id + ", name=" + name + ", sort_number=" + sort_number + ", update_time=" + update_time
                + ", add_time=" + add_time + "]";
    }

}
