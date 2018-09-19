package com.sunrun.po;

import com.sunrun.entity.Group;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;
@Setter
@Getter
public class GroupData extends Group {
    @Transient
    private Integer domainId;
}
