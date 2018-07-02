package com.sunrun.common.notice;

public enum NoticeMessage {

        SUCCESS(200, "成功", "Success"),

        FAILED(400, "操作失败", "Failed"),

        POST_PARAMS_ERROR(420,"请求的参数异常","Bad request" ),

        UNKNOWN_EXCEPTION(500, "未知异常，清联系系统管理员", "Unknown error, please contact administrator"),

        ENTER_USERNAME_AND_PASSWORD(1000, "请填写用户名与密码", "Please input user name and password"),

        EMAIL_OR_PHONE_REPEAT(1001, "邮箱/手机号重复", "Duplicated phone number"),

        SYSTEM_EXCEPTION(1002, "系统异常，清联系系统管理员", "Unknown error, please contact administrator"),

        USERNAME_OR_PASSWORD_IS_NULL(1003, "用户名或密码为空", "User name or password is empty"),

        USERNAME_OR_PASSWORD_ERROR(1005, "用户或密码错误", "User name or password error"),

        NO_PERMISSION(1006, "很抱歉，您没有权限进行此操作", "Access Denied"),

        GROUD_ID_IS_NULL(1007, "群ID不可为空", "Empty group ID"),

        ID_FORMAT_ERROR(1008, "ID格式错误", "Error ID format"),

        PARAMS_INVALID(1009, "参数不正确", "Wrong parameter"),

        GROUND_ID_OR_USERNAME_IS_NULL(1010, "群ID或用户名不可为空", "Empty group ID or user name"),

        NOT_BELONG_TO_ROOM(1011, "用户不属于此房间", "Error, not a group member"),

        USER_REPEAT_ROOM_MEMBER(1012, "用户已经是房间成员", "Error, already a group member"),

        LONGITUDES_OR_LATITUDES_IS_NULL(1013, "用户名,经度或纬度为空", "Empty user name, longitude or latitude"),

        USER_NOT_EXIST(1014, "用户不存在", "Error, User Not exist"),

        USERNAME_ILLEGAL_FORMAT(1015, "用户名,联系人为空或格式不正确", "Empty user name or illegal format"),

        PHONE_OR_EMAIL_INVALID(1016, "手机号或邮箱格式不正确", "Wrong phone number, wrong email or format error"),

        VCODE_IS_NULL(1017, "验证码为空", "Empty verification code"),

        VCODE_NOT_EXIST(1018, "验证码不存在", "Verification code not exist"),

        VCODE_INVALID(1019, "验证码已失效", "Invalid verification code"),

        ACCOUNT_NOT_VERIFIED(1020, "账号没有验证", "Failed to verify accout"),

        USERNAME_OR_DEVICE_IS_NULL(1022, "用户名,设备号或设备类型为空", "Empty user name, device number, or device type"),

        DEVICE_INFO_ERROR(1023, "设备信息错误", "Error device info"),

        UPDATA_DATA_IS_NULL(1024, "更新数据不可为空", "Empty data content"),

        AVATAR_UPLOAD_FAILED(1025, "头像上传失败", "Failed to upload portrait"),

        AVATAR_DOWNLOAD_FAILED(1026, "头像下载失败", "Faild to download portrait"),

        EMPTY_DATA(1027, "数据不可为空", "Empty data content"),

        USERNAME_IS_NULL(1028, "用户名不可为空", "Empty user name"),

        PARAMS_IS_NULL(1029, "参数不可为空", "Empty parameter"),

        SIGNATURE_FAILED(1030, "签名失败", "Signature failure"),

        EVENT_NOT_EXIST(1031, "事件不存在", "Event not exist"),

        PASSWORD_ERROR(1032, "密码错误", "Password error"),

        ACCOUNT_AUTHENTICATE_FAILED(1033,"账号验证失败" ,"Failed to authenticate user" ),

        ROOM_NAME_IS_EMPTY(1034,"群名不可为空","Empty room name or natural name"),

        ROOM_NAME_ALREADY_EXIST(1035,"该群名已经存在" ,"The room name already exists" ),

        NOT_FIND_ROOM(1036,"该群不存在","The room does not exist"),

        GROUP_NAME_IS_EMPTY(1037,"组名不可为空" , "The group name is empty."),

        USERNAME_ALREADY_EXIST(1038, "该用户名已经存在", "The username already exists.");
        private int code;
        private String cnMessage;
        private String enMessage;

        public int getCode() {
                return code;
        }

        public void setCode(int code) {
                this.code = code;
        }

        public String getCnMessage() {
                return cnMessage;
        }

        public void setCnMessage(String cnMessage) {
                this.cnMessage = cnMessage;
        }

        public String getEnMessage() {
                return enMessage;
        }

        public void setEnMessage(String enMessage) {
                this.enMessage = enMessage;
        }

        NoticeMessage(int code, String cnMessage, String enMessage) {
                this.code = code;
                this.cnMessage = cnMessage;
                this.enMessage = enMessage;
        }
}