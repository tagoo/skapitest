package com.sunrun.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "tbuser")
@Table
@Getter
@Setter
@ToString
public class User implements Serializable{
    private static final long serialVersionUID = -6628203903947347958L;
    @Id
    @GeneratedValue(generator = "snowflakeId")
    @GenericGenerator(name = "snowflakeId",strategy = "com.sunrun.utils.IDGenerator")
    private Long id;
    private Long sourceId;
    private Integer domainId;
    private Integer sortNumber;
    @Column(name = "userName")
    private String userName;
    @Column(name = "userPassword",updatable = false)
    private String userPassword;
    private String userRealName;
    private String userDescription;
    private String userHead;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate userBirthday;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerDate;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "role",length = 1)
    private Role role;
    private String longitude;
    private String latitude;
    private String userEmail;
    private String userMobile;
    private String userMobile2;
    private String rank;
    private String userPhone;
    private String userAddress;
    private String qq;
    private Integer userAge;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "userSex",length = 5)
    private Gender userSex;
    private String apiKey;
    private Integer userState;
    private Integer spaceView ;
    private String remarkName ;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime ;
    private String iamUserHead ;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="orgId",foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))
    private Org org;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public enum Role {
        user,admin
    }

    public enum Gender {
        none,man,women
    }

}
