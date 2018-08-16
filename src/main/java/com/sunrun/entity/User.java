package com.sunrun.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "tbuser")
public class User implements Serializable{
    private static final long serialVersionUID = -6628203903947347958L;
    @Id
    @GeneratedValue(generator = "snowflakeId")
    @GenericGenerator(name = "snowflakeId",strategy = "com.sunrun.utils.IDGenerator")
    private Long id;
    private Long sourceId;
    private Integer domainId;
    /*private Long orgId;*/
    private Integer sortNumber;
    @Column(name = "userName")
    private String userName;
    @Column(name = "userPassword",updatable = false)
    private String userPassword;
    private String userRealName;
    private String userDescription;
    private String userHead;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate userBirthday;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerDate;
    private String longitude;
    private String latitude;
    private String userEmail;
    private String userMobile;
    private String userPhone;
    private String userAddress;
    private Integer userAge;
    private Integer userSex;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getDomainId() {
        return domainId;
    }

    public void setDomainId(Integer domainId) {
        this.domainId = domainId;
    }

    public Integer getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(Integer sortNumber) {
        this.sortNumber = sortNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public LocalDate getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(LocalDate userBirthday) {
        this.userBirthday = userBirthday;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public Integer getUserAge() {
        return userAge;
    }

    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }

    public Integer getUserSex() {
        return userSex;
    }

    public void setUserSex(Integer userSex) {
        this.userSex = userSex;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Integer getUserState() {
        return userState;
    }

    public void setUserState(Integer userState) {
        this.userState = userState;
    }

    public Integer getSpaceView() {
        return spaceView;
    }

    public void setSpaceView(Integer spaceView) {
        this.spaceView = spaceView;
    }

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getIamUserHead() {
        return iamUserHead;
    }

    public void setIamUserHead(String iamUserHead) {
        this.iamUserHead = iamUserHead;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    /*public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }*/

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", sourceId=" + sourceId +
                ", domainId=" + domainId +
                ", orgId=" + org.getOrgId() +
                ", sortNumber=" + sortNumber +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userRealName='" + userRealName + '\'' +
                ", userDescription='" + userDescription + '\'' +
                ", userHead='" + userHead + '\'' +
                ", userBirthday=" + userBirthday +
                ", registerDate=" + registerDate +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userMobile='" + userMobile + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", userAddress='" + userAddress + '\'' +
                ", userAge=" + userAge +
                ", userSex=" + userSex +
                ", apiKey='" + apiKey + '\'' +
                ", userState=" + userState +
                ", spaceView=" + spaceView +
                ", remarkName='" + remarkName + '\'' +
                ", updateTime=" + updateTime +
                ", iamUserHead='" + iamUserHead + '\'' +
                '}';
    }
}
