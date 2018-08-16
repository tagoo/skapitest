package com.sunrun.po;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface UserPo {
     Long getId();
     Long getSourceId() ;
     Long getOrgId() ;
     Long getDomainId() ;
     Integer getSortNumber() ;
     String getUserName() ;
     String getUserAddress();
     String getUserRealName() ;
     LocalDate getUserBirthday() ;
     LocalDateTime getRegisterDate() ;
     String getUserEmail() ;
     String getUserMobile() ;
     String getUserPhone() ;
     Integer getUserSex() ;
     Integer getUserState() ;
     String getOrgName() ;
}
