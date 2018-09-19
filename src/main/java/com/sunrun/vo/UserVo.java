package com.sunrun.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Setter
@Getter
@NoArgsConstructor
public class UserVo {

    private Long id;
    private Long org_id;
    private Integer domain_id;
    private Integer grade;
    private Integer is_enabled;
    private Integer sort_number;
    private Integer sex;
    private String real_name;
    private String head;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime update_time;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthday;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime add_time;
    private String mobile;
    private String mobile2;
    private String telephone;
    private String qq;
    private String email;
    private String address;
    private String rank;
    private String name;
    private String type;

}
