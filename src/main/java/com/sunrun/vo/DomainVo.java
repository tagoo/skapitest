package com.sunrun.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DomainVo {
    private Integer id;
    private String name;
    private Integer sort_number;
    private String update_time;
    private String add_time;
}
