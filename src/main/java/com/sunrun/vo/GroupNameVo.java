package com.sunrun.vo;

import com.sunrun.entity.Group;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class GroupNameVo {
    private List<String> groupname;
    private List<GroupName> groups;
}
