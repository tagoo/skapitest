package com.sunrun.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tbgroup")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GroupProp {
    @Id
    private String groupName;
    private Integer domainId;
}
