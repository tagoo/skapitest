/*
 Navicat Premium Data Transfer

 Source Server         : 10.17.148.61(sit management)
 Source Server Type    : MySQL
 Source Server Version : 100118
 Source Host           : 10.17.148.61:3306
 Source Schema         : management_db

 Target Server Type    : MySQL
 Target Server Version : 100118
 File Encoding         : 65001

 Date: 18/06/2019 08:34:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for eva_auth_role
-- ----------------------------
DROP TABLE IF EXISTS `eva_auth_role`;
CREATE TABLE `eva_auth_role`  (
  `fd_id` bigint(36) NOT NULL COMMENT '主键',
  `fd_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称',
  `fd_is_admin` tinyint(1) NULL DEFAULT NULL COMMENT '是否管理员：1-是，0-否',
  `fd_tenant_id` bigint(36) NULL DEFAULT NULL COMMENT '所属租户，超级管理员为-1',
  `fd_description` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色描述',
  `fd_is_delete` tinyint(1) NULL DEFAULT NULL COMMENT '是否删除：1-删除。0-否',
  `doc_creator_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `doc_create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `doc_last_update_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `doc_last_update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`fd_id`) USING BTREE,
  INDEX `idx_role_tenant_id`(`fd_tenant_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for eva_auth_role_user
-- ----------------------------
DROP TABLE IF EXISTS `eva_auth_role_user`;
CREATE TABLE `eva_auth_role_user`  (
  `fd_id` bigint(36) NOT NULL COMMENT '主键',
  `fd_role_id` bigint(36) NOT NULL COMMENT '角色ID',
  `fd_person_uid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'mip账号（是账号名称）',
  PRIMARY KEY (`fd_id`) USING BTREE,
  INDEX `idx_role_id`(`fd_role_id`) USING BTREE,
  INDEX `idx_person_uid`(`fd_person_uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色与用户的关联管理表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gateway_auth_app
-- ----------------------------
DROP TABLE IF EXISTS `gateway_auth_app`;
CREATE TABLE `gateway_auth_app`  (
  `fd_id` bigint(32) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `fd_gateway_id` bigint(32) NULL DEFAULT NULL COMMENT '关联网关id',
  `fd_app_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '授权应用appId',
  `fd_app_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '授权应用appKey',
  `fd_app_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '授权应用appName',
  PRIMARY KEY (`fd_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '记录网关上配置的授权外部应用' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_application
-- ----------------------------
DROP TABLE IF EXISTS `sys_application`;
CREATE TABLE `sys_application`  (
  `FD_ID` bigint(36) NOT NULL COMMENT '主键',
  `FD_APP_NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用名称',
  `FD_TENANT_ID` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户id',
  `FD_IS_DELETE` tinyint(1) NULL DEFAULT NULL COMMENT '标记是否删除',
  `FD_APP_DESC` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用描述',
  `FD_CREATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `DOC_CREATOR_ID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者mip账号',
  `DOC_LAST_UPDATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `DOC_LAST_UPDATE_ID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改者mip账号',
  PRIMARY KEY (`FD_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用列表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_application_instance
-- ----------------------------
DROP TABLE IF EXISTS `sys_application_instance`;
CREATE TABLE `sys_application_instance`  (
  `FD_ID` bigint(50) NOT NULL COMMENT '主键',
  `FD_IP` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实例IP',
  `FD_PORT` varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实例端口',
  `FD_SERVICE_ID` bigint(36) NULL DEFAULT NULL COMMENT '服务名称ID',
  `FD_APP_ID` bigint(36) NULL DEFAULT NULL COMMENT '应用名称ID',
  `FD_VERSIONS` varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本',
  `FD_RUNNING_STATUS` tinyint(1) NULL DEFAULT NULL COMMENT '运行状态',
  `STATUS_UPDATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '运行状态更新时间',
  `FD_IS_GREY` tinyint(1) NULL DEFAULT 0 COMMENT '是否灰度 0:不是,1:是',
  PRIMARY KEY (`FD_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '实例列表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_application_service
-- ----------------------------
DROP TABLE IF EXISTS `sys_application_service`;
CREATE TABLE `sys_application_service`  (
  `FD_ID` bigint(36) NOT NULL COMMENT '主键',
  `FD_SERVE_NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务名称',
  `FD_SERVE_DESC` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务的备注',
  `FD_APP_ID` bigint(36) NULL DEFAULT NULL COMMENT '所属应用Id',
  `FD_TENANT_ID` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户id',
  `FD_CREATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `FD_IS_DELETE` tinyint(1) NULL DEFAULT NULL COMMENT '是否删除',
  `DOC_CREATOR_ID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者mip账号',
  `DOC_LAST_UPDATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `DOC_LAST_UPDATE_ID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改者mip账号',
  `fd_context_path` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微服务请求地址：用于热刷新',
  PRIMARY KEY (`FD_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '微服务列表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_auth_default_tenant_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_auth_default_tenant_user`;
CREATE TABLE `sys_auth_default_tenant_user`  (
  `fd_id` bigint(36) NOT NULL COMMENT '主键id',
  `fd_tenant_id` bigint(36) NOT NULL COMMENT '默认租户id',
  `fd_person_uid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'mip账号（是账号名称）',
  PRIMARY KEY (`fd_id`) USING BTREE,
  INDEX `idx_tenant_id`(`fd_tenant_id`) USING BTREE,
  INDEX `idx_person_uid`(`fd_person_uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户与默认租户关联表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_auth_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_auth_menu`;
CREATE TABLE `sys_auth_menu`  (
  `fd_id` bigint(36) NOT NULL COMMENT '主键id',
  `fd_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模块名称',
  `fd_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标识',
  `fd_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型，M:模块，F:功能',
  `fd_front_icon` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `fd_front_link` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '前端超链接',
  `fd_order` int(11) NULL DEFAULT NULL COMMENT '排序',
  `fd_is_available` int(1) NULL DEFAULT NULL COMMENT '是否有效',
  `fd_hierarchy_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '层级ID',
  `fd_parentid` bigint(36) NULL DEFAULT NULL COMMENT '父节点Id',
  `fd_tenant_id` bigint(36) NULL DEFAULT NULL COMMENT '所属租户id,预留字段，平台菜单：-1',
  `fd_is_delete` int(1) NOT NULL DEFAULT 0 COMMENT '是否已删除 1--已删除；0--未删除',
  `fd_description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `doc_last_update_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改者mip账号',
  `doc_last_update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `doc_create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `doc_creator_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者mip账号',
  `fd_is_need_log` tinyint(1) NULL DEFAULT NULL COMMENT '标记是否需要统计操作日志',
  PRIMARY KEY (`fd_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单功能信息表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_auth_menu_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_auth_menu_role`;
CREATE TABLE `sys_auth_menu_role`  (
  `fd_id` bigint(36) NOT NULL COMMENT '主键id',
  `fd_menu_id` bigint(36) NULL DEFAULT NULL COMMENT '菜单ID',
  `fd_role_id` bigint(36) NULL DEFAULT NULL COMMENT '角色ID',
  `fd_tenant_id` bigint(36) NULL DEFAULT NULL COMMENT '租户ID，-1：标识平台菜单',
  PRIMARY KEY (`fd_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单角色关系表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_auth_tenant
-- ----------------------------
DROP TABLE IF EXISTS `sys_auth_tenant`;
CREATE TABLE `sys_auth_tenant`  (
  `fd_id` bigint(36) NOT NULL COMMENT '主键',
  `fd_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '租户名称',
  `fd_is_available` tinyint(1) NULL DEFAULT NULL COMMENT '是否有效（1-有效，0-无效）',
  `fd_description` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户的描述',
  `fd_is_delete` tinyint(1) NULL DEFAULT NULL COMMENT '是否删除（1为删，0未删）',
  `doc_creator_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人，保存mip账号',
  `doc_create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `doc_last_update_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `doc_last_update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `fd_svn_config_url` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'svn地址',
  `fd_svn_config_username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'svn账号',
  `fd_svn_config_password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'svn密码',
  PRIMARY KEY (`fd_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '租户表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_base_app_cluster_define
-- ----------------------------
DROP TABLE IF EXISTS `sys_base_app_cluster_define`;
CREATE TABLE `sys_base_app_cluster_define`  (
  `fd_id` bigint(32) NOT NULL COMMENT '主键',
  `fd_cluster_id` bigint(32) NULL DEFAULT NULL COMMENT '各中心集群ID',
  `fd_host` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务器IP',
  `fd_port` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用端口号',
  `fd_target_dir` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '安装路径',
  PRIMARY KEY (`fd_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用关系表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_base_app_cluster_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_base_app_cluster_info`;
CREATE TABLE `sys_base_app_cluster_info`  (
  `fd_id` bigint(36) NOT NULL COMMENT '主键',
  `fd_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用集群名称',
  `fd_cluster_id` bigint(36) NULL DEFAULT NULL COMMENT '应用集群所部署在机器集群的id',
  `fd_status` varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态，installed(已安装)/uninstalled(未安装)/failed(安装失败)',
  `fd_install_time` datetime(0) NULL DEFAULT NULL COMMENT '应用集群安装时间',
  `fd_desc` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用集群集群描述',
  `fd_creator_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `fd_create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `fd_last_update_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改者',
  `fd_last_update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `fd_application_type` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用集群类型:CONFIG_SERVER(配置中心)，EUREKA_SERVER(注册中心)，HYSTRIX_DASHBOARD(hystrix 监控服务)，ADMIN_SERVER(spring admin监控服务)',
  `fd_registry_cluster_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '需要注册到注册中心的集群id, sys_base_app_cluster_info的id',
  `fd_tenant_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户id',
  `fd_kafka_address` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'kafka集群地址（仅config配置中心需要）',
  `fd_zookeeper_address` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'zookeeper集群地址（仅config配置中心需要）',
  `fd_label` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标签',
  `fd_svn_config_url` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配置中心使用的SVN路径',
  `fd_svn_config_username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配置中心使用的SVN账号',
  `fd_svn_config_password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配置中心使用的SVN密码',
  `fd_install_type` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '安装类型',
  `fd_version` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本',
  `fd_docker_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'eva容器id',
  `fd_encrypt_key` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT ' 配置中心加解密秘钥',
  `fd_registry_account` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'security账号',
  `fd_registry_password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'security密码',
  PRIMARY KEY (`fd_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '基础应用集群信息（配置中心，注册中心，hystrix dashboard监控服务，spring admin监控服务）' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_base_app_install_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_base_app_install_info`;
CREATE TABLE `sys_base_app_install_info`  (
  `fd_id` bigint(36) NOT NULL COMMENT '主键',
  `fd_application_type` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用集群类型:CONFIG_SERVER(配置中心)，ERUEKA_SERVER(注册中心)，HYSTRIX_DASHBOARD(hystrix 监控服务)，ADMIN_SERVER(spring admin监控服务)',
  `fd_pkg_location` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '源包所在路径，包所在oss对象存储中的路径',
  `fd_install_cmd` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部署命令，多个命令以分隔',
  `fd_target_dir` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '源包上传到目标服务器的路径，若服务器上不存在则自动创建',
  `fd_file_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '源包上传到服务器上的文件名',
  `fd_version` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本',
  `fd_image_name` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '镜像名',
  `fd_port` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '默认端口号',
  PRIMARY KEY (`fd_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '基础应用安装信息，包括源包路径，安装命令' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_circuit_breaker_rule
-- ----------------------------
DROP TABLE IF EXISTS `sys_circuit_breaker_rule`;
CREATE TABLE `sys_circuit_breaker_rule`  (
  `fd_id` bigint(36) NOT NULL COMMENT '主键',
  `fd_name` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '熔断规则名称',
  `fd_app_id` bigint(36) NULL DEFAULT NULL COMMENT '熔断规则所属应用',
  `fd_tenant_id` bigint(36) NULL DEFAULT NULL COMMENT '熔断规则所属租户',
  `fd_target_name` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '熔断目标为空表示所有服务或者所属应用下的某个微服务的service_name',
  `fd_breaker_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '熔断方式1、manual:手动熔断；2、auto:自动熔断；3、cancel:取消熔断',
  `fd_desc` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '熔断规则备注、描述',
  `fd_error_percentage` int(3) NULL DEFAULT NULL COMMENT '请求失败率(%)请求的失败率，以%为单位0-100的整数，在熔断统计时间窗内，请求失败率大于此值，则断开熔断器',
  `fd_request_volumne` int(10) NULL DEFAULT NULL COMMENT '发起的请求数，1-10000的整数，若在熔断统计时间窗内，请求数大于此值，熔断统计生效',
  `fd_sleep_window` int(10) NULL DEFAULT NULL COMMENT '熔断时间窗，熔断的持续时间，以毫秒为单位，超过此时间窗则熔断状态变成半开状态',
  `fd_execution_timeout` int(10) NULL DEFAULT NULL COMMENT '执行超时时间（ms）',
  `fd_statistical_window` int(10) NULL DEFAULT NULL COMMENT '统计时间窗(ms)',
  `fd_creator_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `fd_create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `fd_last_update_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最后修改人',
  `fd_last_update_time` datetime(0) NULL DEFAULT NULL COMMENT '最后修改时间',
  `fd_service_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务Id,用于前端跳转详情',
  `fd_interface_names` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接口熔断方法名,多个名字用分号隔开',
  PRIMARY KEY (`fd_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '熔断规则表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `fd_id` bigint(36) NOT NULL COMMENT '主键',
  `fd_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `fd_apply_id` bigint(36) NULL DEFAULT NULL COMMENT '所属应用',
  `fd_service_id` bigint(36) NULL DEFAULT NULL COMMENT '所属微服务',
  `fd_tenant_id` bigint(36) NULL DEFAULT NULL COMMENT '所属租户',
  `fd_label` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标签：dev、sit、uat、ver、pro',
  `doc_creator_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `doc_create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `doc_last_update_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `doc_last_update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `fd_config_cluster_id` bigint(36) NULL DEFAULT NULL COMMENT '配置中心集群ID',
  PRIMARY KEY (`fd_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '配置中心' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_config_version
-- ----------------------------
DROP TABLE IF EXISTS `sys_config_version`;
CREATE TABLE `sys_config_version`  (
  `fd_id` bigint(36) NOT NULL COMMENT '主键',
  `fd_version` int(3) NOT NULL COMMENT '版本：默认100及1.0.0版本',
  `fd_file_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '配置文件名称（SVN）',
  `fd_content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '内容',
  `fd_config_id` bigint(36) NOT NULL COMMENT '所属配置',
  `fd_status` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '版本状态：草稿，锁定',
  `doc_creator_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `doc_create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `doc_last_update_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `doc_last_update_time` datetime(0) NULL DEFAULT NULL COMMENT '跟新时间',
  PRIMARY KEY (`fd_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '配置中心版本' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_config_version_instance
-- ----------------------------
DROP TABLE IF EXISTS `sys_config_version_instance`;
CREATE TABLE `sys_config_version_instance`  (
  `fd_id` bigint(36) NOT NULL COMMENT '主键',
  `fd_config_id` bigint(3) NULL DEFAULT NULL COMMENT '所属配置中心',
  `fd_using_version` bigint(36) NULL DEFAULT NULL COMMENT '正在使用的版本',
  `fd_instance_id` bigint(36) NULL DEFAULT NULL COMMENT '微服务实例ID',
  `fd_instance_ip` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微服务实例IP',
  `fd_instance_port` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微服务实例端口',
  `fd_rollback_version` bigint(36) NULL DEFAULT NULL COMMENT '上次使用的版本',
  `doc_creator_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `doc_create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `doc_last_update_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `doc_last_update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`fd_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '配置版本与微服务实例关系' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_elastic_job_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_elastic_job_config`;
CREATE TABLE `sys_elastic_job_config`  (
  `fd_id` bigint(36) NOT NULL COMMENT '主键id,运行作业id',
  `fd_app_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统id',
  `fd_module_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模块id',
  `fd_job_class` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '该job的实现类完整路径，如：com.midea.mframework.sdk.elasticJob.SimpleJobDemo',
  `fd_job_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作业名称',
  `fd_job_cron` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'cron表达式，用于控制作业触发时间',
  `fd_job_sharding_total_count` tinyint(2) NOT NULL COMMENT '作业分片总数',
  `fd_job_sharding_item_parameters` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分片序列号和参数:0=a,1=b,2=c',
  `fd_job_parameter` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '作业自定义参数',
  `fd_failover` tinyint(1) NULL DEFAULT NULL COMMENT '是否开启任务执行失效转移：0 否，1是',
  `fd_misfire` tinyint(1) NULL DEFAULT NULL COMMENT '是否开启错过任务重新执行：0 否，1是',
  `fd_description` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '作业描述信息',
  `fd_job_properties` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配置jobProperties定义的枚举控制Elastic-Job的实现细节',
  `fd_running_status` tinyint(1) NULL DEFAULT NULL COMMENT '运行状态 ：0 停用；1 启用',
  `DOC_CREATOR_ID` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `DOC_CREATOR_NAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人名',
  `DOC_CREATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `DOC_LAST_UPDATE_ID` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最后更新人',
  `DOC_LAST_UPDATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '最后更新时间',
  `fd_execute_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT 'job执行的类型,0:多次执行,1:单次执行,2:立即执行',
  PRIMARY KEY (`fd_id`) USING BTREE,
  INDEX `SYS_ELASTIC_JOB_CONFIG_01`(`fd_job_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'elastic-job配置表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_elastic_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_elastic_job_log`;
CREATE TABLE `sys_elastic_job_log`  (
  `fd_id` bigint(36) NOT NULL COMMENT '主键id,运行作业id',
  `job_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作业名称',
  `job_parameter` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '作业参数',
  `sharding_item_parameters` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分片序列号和参数',
  `running_start_time` datetime(0) NULL DEFAULT NULL COMMENT '运行起始时间',
  `running_end_time` datetime(0) NULL DEFAULT NULL COMMENT '运行结束时间',
  `running_status` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '运行状态',
  `error_msg` varchar(4000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '错误消息',
  `attribute1` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备用字段1',
  `attribute2` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备用字段2',
  `attribute3` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备用字段3',
  `attribute4` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备用字段4',
  `attribute5` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备用字段5',
  `fd_task_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务id',
  PRIMARY KEY (`fd_id`) USING BTREE,
  INDEX `SYS_ELASTIC_JOB_LOGKEY1`(`job_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'elastic-job运行日志记录' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_elasticjob_time
-- ----------------------------
DROP TABLE IF EXISTS `sys_elasticjob_time`;
CREATE TABLE `sys_elasticjob_time`  (
  `FD_ID` bigint(36) NOT NULL COMMENT '主键',
  `DOC_LAST_UPDATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '最后更新时间',
  `FD_SYNC_FLAG` varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '同步状态P：正在同步、N：没有在同步',
  PRIMARY KEY (`FD_ID`) USING BTREE,
  INDEX `SYS_ELASTICJOB_TIME_KEY1`(`DOC_LAST_UPDATE_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '定时任务时间' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_org_custom_relation
-- ----------------------------
DROP TABLE IF EXISTS `sys_org_custom_relation`;
CREATE TABLE `sys_org_custom_relation`  (
  `FD_ID` bigint(36) NOT NULL COMMENT '主键',
  `FD_DEPARTMENT_ID` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织机构id，组织机构表主键',
  `FD_PERSON_ID` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '人员id，人员表主键',
  `FD_PERSON_UID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '人员账号，用户账号',
  `FD_DEPARTMENT_NUMBER` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织机构编码',
  PRIMARY KEY (`FD_ID`) USING BTREE,
  INDEX `idx_sys_org_custom_relation_001`(`FD_DEPARTMENT_ID`) USING BTREE,
  INDEX `idx_sys_org_custom_relation_002`(`FD_PERSON_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户自定义组织架构-用户关系表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_org_department
-- ----------------------------
DROP TABLE IF EXISTS `sys_org_department`;
CREATE TABLE `sys_org_department`  (
  `FD_ID` bigint(36) NOT NULL COMMENT '主键ID',
  `FD_NAME` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `DEPARTMENT_NUMBER` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织机构编号',
  `DISPLAY_NAME` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门显示名称',
  `FD_ORDER` decimal(10, 0) NULL DEFAULT NULL COMMENT '排序号',
  `FD_FAX_NUMBER` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '传真号码',
  `FD_MAIL` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门群发地址',
  `FD_ACI` decimal(10, 0) NULL DEFAULT NULL COMMENT '部门管理员授权',
  `FD_ADLOCATION` varchar(1500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'AD域',
  `FD_ADPARENT_ID` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'AD域父ID',
  `FD_CREATOR` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `FD_DEFAULT_DOMAIN` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '事业部默认邮箱后缀',
  `FD_MAIL_DOMAIN` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮件域名',
  `FD_DEPARTMENT_NAME` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门全称',
  `FD_IS_ROOT` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否根节点',
  `FD_LABEL_CODE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织标签编码',
  `FD_MODIFIER` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改者',
  `FD_ORG_AREA` varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属地区',
  `FD_ORG_TYPE` varchar(3) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织类型\r\n            (组织类型\r\n            0-内部\r\n        \r\n\r\n    1-临时内部\r\n            2-测试\r\n            3-职能\r\n            4-应用\r\n            5-外部)',
  `FD_PERSONIN_CHARGE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门负责人',
  `FD_PARENT_NAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上级部门全称',
  `FD_PARENTID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'default' COMMENT '上级部门编号',
  `LDAP_PARENTID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ldap倒过来的父部门id',
  `FD_LAST_UPDATE_TIME` timestamp(0) NULL DEFAULT NULL COMMENT '最后更新时间',
  `FD_IS_CHARGE` decimal(10, 0) NULL DEFAULT NULL COMMENT '是否主管（岗位使用） 0:否1：是',
  `FD_ORGANIZATION_ID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织ID',
  `FD_POSTAL_ADDRESS` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门地址',
  `FD_POSTAL_CODE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮编',
  `FD_TELEPHONE_NUMBER` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '办公电话',
  `FD_RETURN_ID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '返回ID',
  `LDAP_SYNC_FLAG` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'N' COMMENT 'Ldap同步标识(N为未同步,Y为已同步)',
  `FD_ENGLISH_NAME` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '英文显示名',
  `FD_DEPARTMENT_ENGLISH_NAME` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门英文全称',
  `FD_ISROOT` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否根节点',
  `FD_ORGAREA` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属地区',
  `FD_OTYPE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织单元类型',
  `FD_LDAP_PARENTID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上级部门编码',
  `FD_PARENTNAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上级部门名称',
  `FD_OSTYLE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织机构类型',
  `FD_LABLECODE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织标签编码',
  `FD_REPORTCODE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织汇报关系',
  `FD_STYLE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织类型',
  `FD_STATUS` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织单元状态',
  `FD_LEVEL` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织单元层次',
  `FD_HISTORYID` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '历史组织单元编号',
  `FD_OID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织单元系统编号',
  `FD_SOURCE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据来源(LDAP,CUSTOM)',
  `FD_APP_ID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统id',
  PRIMARY KEY (`FD_ID`) USING BTREE,
  INDEX `IDX_SYS_ORG_DEPARTMENT_02`(`FD_PARENTID`) USING BTREE,
  INDEX `IDX_SYS_ORG_DEPARTMENT_01`(`DEPARTMENT_NUMBER`) USING BTREE,
  INDEX `IDX_SYS_ORG_DEPARTMENT_03`(`FD_OID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '部门表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_org_department_category
-- ----------------------------
DROP TABLE IF EXISTS `sys_org_department_category`;
CREATE TABLE `sys_org_department_category`  (
  `FD_ID` bigint(36) NOT NULL COMMENT '主键ID',
  `FD_CATEGORY_ID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分类ID',
  `FD_DEPARTMENT_ID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '机构ID',
  `FD_NAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '场所名称',
  PRIMARY KEY (`FD_ID`) USING BTREE,
  INDEX `SYS_ORG_DEPARTMENT_CATEGORY_KEY1`(`FD_CATEGORY_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '场所配置中间表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_org_department_leader
-- ----------------------------
DROP TABLE IF EXISTS `sys_org_department_leader`;
CREATE TABLE `sys_org_department_leader`  (
  `FD_ID` bigint(36) NOT NULL COMMENT '主键ID',
  `DEPARTMENT_NUMBER` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '组织机构编号',
  `DEPARTMENT_LEADER_ID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '部门负责人员工编码',
  PRIMARY KEY (`FD_ID`) USING BTREE,
  INDEX `SYS_ORG_DEPARTMENT_LEADER_KEY1`(`DEPARTMENT_NUMBER`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '部门负责人表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_org_ldap_sync_record
-- ----------------------------
DROP TABLE IF EXISTS `sys_org_ldap_sync_record`;
CREATE TABLE `sys_org_ldap_sync_record`  (
  `FD_ID` bigint(36) NOT NULL COMMENT '主键ID',
  `FD_LDAP_ID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'LDAP唯一编码',
  `FD_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '中文名',
  `FD_UID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户ID',
  `FD_DEPT_PERSON_FLAG` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门或人员标志位(D-部门,P-内部人员z账号,PE-供应商账号)',
  `FD_SYNC_STATUS` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '同步状态[S/N/E]: S-同步成功、N-未同步、E-同步失败、P-正在同步',
  `FD_SYNC_RESULT` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '同步结果',
  `FD_FAIL_TIMES` tinyint(4) NULL DEFAULT NULL COMMENT '失败次数',
  `FD_SYNC_TIME` datetime(0) NULL DEFAULT NULL COMMENT '同步时间',
  `FD_LAST_UPDATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '修改或创建时间',
  PRIMARY KEY (`FD_ID`) USING BTREE,
  INDEX `IDX_SYS_ORG_LDAP_SYNC_LOG_01`(`FD_LDAP_ID`) USING BTREE,
  INDEX `IDX_SYS_ORG_LDAP_SYNC_LOG_02`(`FD_UID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '同步中间表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_org_person
-- ----------------------------
DROP TABLE IF EXISTS `sys_org_person`;
CREATE TABLE `sys_org_person`  (
  `FD_ID` bigint(36) NOT NULL COMMENT '主键ID',
  `FD_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '中文名',
  `FD_PINYIN` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '拼音',
  `FD_UID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户账号',
  `FD_SHORT_NAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '简称',
  `FD_EMAIL` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电子邮件',
  `FD_USER_PASSWORD` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户密码',
  `FD_DEPARTMENT_NAME` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门全路径',
  `FD_EMERGENCY_CONTACT` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标识用户账号的紧急联系姓名和联系方式',
  `FD_USER_TYPE` int(3) NULL DEFAULT NULL COMMENT '用户类型：\r\n0-内部用户\r\n1-内部临时\r\n2-测试账户\r\n3-职能账号\r\r\n\r\n\n4-应用账户\r\n5-外部用户\r\n6-公共通讯录\r\n7-供应商\r\n8-TC门店\r\n9-代理商\r\n10-产业链\r\n11-国外供应商',
  `FD_EMPLOYEE_NUMBER` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '员工编号，跟ldap同步',
  `FD_MOBILE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话',
  `FD_TELEPHONE_NUMBER` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '座机',
  `FD_HOME_ADDRESS` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '家庭住址',
  `FD_POSTAL_ADDRESS` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '通讯地址',
  `FD_SECURE_MOBILE` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '存储密码找回绑定的手机号码',
  `FD_ORDER` decimal(10, 0) NULL DEFAULT NULL COMMENT '排序号',
  `FD_NO` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户唯一标识',
  `FD_UID_EMPLOYEE_NUMBER` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一标识登录名+员工编号',
  `FD_KEYWORD` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关键字',
  `FD_IS_AVAILABLE` decimal(10, 0) NULL DEFAULT 1 COMMENT '是否生效',
  `FD_IS_BUSINESS` decimal(10, 0) NULL DEFAULT NULL COMMENT '是否公司',
  `FD_MEMO` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `FD_HIERARCHY_ID` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '层级id',
  `FD_CREATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `FD_ALTER_TIME` varchar(400) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改时间',
  `FD_PORTAL_LINK` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '门户链接',
  `FD_PORTAL_NAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '门户名字',
  `FD_THIS_LEADERID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '本级领导',
  `FD_SUPER_LEADERID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上级领导',
  `FD_PARENTORGID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机构id',
  `FD_PARENTID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属部门',
  `FD_ENGLISH_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '英文名',
  `FD_LAST_UPDATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '最后更新时间',
  `FD_SIGN` decimal(10, 0) NULL DEFAULT NULL COMMENT '更新标识 0:表示正在同步1：已经同步完',
  `FD_IS_CHARGE` decimal(10, 0) NULL DEFAULT NULL COMMENT '是否主管（岗位使用） 0:否1：是',
  `FD_CATEID` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '群组类别id',
  `FD_IN_DUSTRY` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职群ID',
  `FD_PART_TIME_DEPARTMENT` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '多部门兼容',
  `FD_HR_PART_DEPARTMENT` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'HR兼职职位 多值逗号分隔',
  `FD_EMPSTATUS` int(3) NULL DEFAULT NULL COMMENT '员工当前状态:下游应用只需要同步1，2，8，9，10其它状态（3-7）的帐户将\r\n\r\n被禁用',
  `FD_POSITION_NAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职位名称',
  `FD_MAIL_SYSTEM` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮件系统标识',
  `FD_POSITION` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职位编码',
  `FD_DEPARTMENT_NUMBER` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机构编码(与Ldap一致)',
  `FD_RETURN_ID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '返回ID',
  `FD_RANK` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职级',
  `LDAP_SYNC_FLAG` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'N' COMMENT 'Ldap是否同步标志(N未同步,Y已同步)',
  `FD_POSITION_ENGLISH_NAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职位英文名称',
  `FD_BUSINESS_MAIL` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司邮箱',
  `FD_PERSONAL_MAIL` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '个人邮箱',
  `FD_ANOTHER_NAME_MAIL` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱别名',
  `FD_DEPARTMENT_ENGLISH_NAME` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门英文路径',
  `FD_EMPLOYEETYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '员工类型',
  `FD_HIREDATE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '入司时间',
  `FD_GENDER` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `FD_POSITIONTYPE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职类',
  `FD_POSITIONGRADE` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职等',
  `FD_IDCARDTYPE` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '证件类型',
  `FD_NATION` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '国籍',
  `FD_IDCARDNO` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证号',
  `FD_COMPANYNUMBER` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司代码',
  `FD_COMPANYNAME` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司名称',
  `FD_BIRTHDAY` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '出生日期',
  `FD_ISPARTTIME` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否兼职',
  `FD_ISTRANSFER` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否借调',
  `FD_VIRTUALDEP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '兼职部门',
  `FD_VIRTUALPOS` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '兼职岗位',
  `FD_SOURCE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据来源(LDAP,CUSTOM)',
  `FD_APP_ID` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统id',
  PRIMARY KEY (`FD_ID`) USING BTREE,
  INDEX `IDX_SYS_ORG_PERSON_02`(`FD_UID`) USING BTREE,
  INDEX `IDX_SYS_ORG_PERSON_03`(`FD_PARENTID`) USING BTREE,
  INDEX `IDX_SYS_ORG_PERSON_04`(`FD_NAME`) USING BTREE,
  INDEX `IDX_SYS_ORG_PERSON_01`(`FD_UID_EMPLOYEE_NUMBER`) USING BTREE,
  INDEX `IDX_SYS_ORG_PERSON_06`(`FD_EMPLOYEE_NUMBER`) USING BTREE,
  FULLTEXT INDEX `FD_HIERARCHY_ID_1`(`FD_HIERARCHY_ID`)
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '人员表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_org_person_collection
-- ----------------------------
DROP TABLE IF EXISTS `sys_org_person_collection`;
CREATE TABLE `sys_org_person_collection`  (
  `FD_ID` bigint(36) NOT NULL COMMENT '主键ID',
  `FD_UID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户UID',
  `FD_COLLECTION_UID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收藏人员UID',
  `FD_CATALOG_ID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所属目录',
  `FD_COLLECTION_DEPARTMENT_ID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收藏部门ID',
  `FD_COLLECTION_FLAG` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '收藏标志(1为人员2为机构)',
  PRIMARY KEY (`FD_ID`) USING BTREE,
  INDEX `SYS_ORG_PERSON_COLLECTION_KEY1`(`FD_UID`) USING BTREE,
  INDEX `SYS_ORG_PERSON_COLLECTION_KEY2`(`FD_COLLECTION_FLAG`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '常用人员收藏表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_org_person_collection_catalog
-- ----------------------------
DROP TABLE IF EXISTS `sys_org_person_collection_catalog`;
CREATE TABLE `sys_org_person_collection_catalog`  (
  `FD_ID` bigint(36) NOT NULL COMMENT '主键ID',
  `FD_NAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '目录名称',
  `DOC_CREATER_ID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建人ID',
  PRIMARY KEY (`FD_ID`) USING BTREE,
  INDEX `SYS_ORG_PERSON_COLLECTION_CATALOG_KEY1`(`FD_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '常用人员收藏目录表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_org_person_conf
-- ----------------------------
DROP TABLE IF EXISTS `sys_org_person_conf`;
CREATE TABLE `sys_org_person_conf`  (
  `FD_ID` bigint(36) NOT NULL COMMENT '用户ID',
  `FD_UID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户UID',
  `FD_LANG_TYPE` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '默认语言类型',
  `FD_TIME_ZONE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '时区编码',
  PRIMARY KEY (`FD_ID`) USING BTREE,
  INDEX `SYS_ORG_PERSON_CONF_KEY1`(`FD_UID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '人员配置表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_org_person_conf_init
-- ----------------------------
DROP TABLE IF EXISTS `sys_org_person_conf_init`;
CREATE TABLE `sys_org_person_conf_init`  (
  `FD_ID` bigint(32) NOT NULL COMMENT '主键ID',
  `FD_PARENT_ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属部门ID',
  `FD_TIMEZONE_ID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '时区ID',
  `FD_LANG_TYPE` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '语言类型',
  `FD_ISVALID` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否生效',
  `DOC_CREAT_TIME` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `DOC_LAST_UPDATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '最后更新时间',
  `DOC_LAST_UPDATE_UID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最后修改人登录名',
  `DOC_CREATER_UID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人登录名',
  PRIMARY KEY (`FD_ID`) USING BTREE,
  INDEX `IDX_SYS_ORG_PERSON_CONF_INIT_KEY_01`(`FD_ISVALID`) USING BTREE,
  INDEX `IDX_SYS_ORG_PERSON_CONF_INIT_KEY_02`(`FD_PARENT_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '初始化人员个性化配置表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_org_person_login
-- ----------------------------
DROP TABLE IF EXISTS `sys_org_person_login`;
CREATE TABLE `sys_org_person_login`  (
  `FD_ID` bigint(36) NOT NULL COMMENT '主键ID',
  `FD_UID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录账号',
  `FD_NAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '人名',
  `FD_DEPARTMENT_ID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门ID',
  `FD_DEPARTMENT_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门名称',
  `FD_LOGIN_DATE` datetime(0) NULL DEFAULT NULL COMMENT '登录时间',
  `FD_HIERARCHY_ID` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门层级ID',
  `ALTERNATE1` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '备用字段1',
  `ALTERNATE2` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '备用字段2',
  `ALTERNATE3` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '备用字段3',
  `ALTERNATE4` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '备用字段4',
  `ALTERNATE5` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '备用字段5',
  PRIMARY KEY (`FD_ID`) USING BTREE,
  INDEX `IDX_SYS_ORG_PERSON_LOGIN_KEY_01`(`FD_UID`) USING BTREE,
  INDEX `IDX_SYS_ORG_PERSON_LOGIN_KEY_02`(`FD_DEPARTMENT_ID`) USING BTREE,
  INDEX `IDX_SYS_ORG_PERSON_LOGIN_KEY_03`(`FD_LOGIN_DATE`) USING BTREE,
  INDEX `IDX_SYS_ORG_PERSON_LOGIN_KEY_04`(`FD_HIERARCHY_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '登录日志' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_org_person_position
-- ----------------------------
DROP TABLE IF EXISTS `sys_org_person_position`;
CREATE TABLE `sys_org_person_position`  (
  `FD_ID` bigint(36) NOT NULL COMMENT '主键ID',
  `FD_POSITION` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '职位编码',
  `FD_POSITION_PARENT` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上级职位编码',
  `FD_FAMILYGROUPID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职群ID',
  `FD_LDAP_UPDATE_FLAG` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ldap更新标志',
  `DOC_UPDATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `DOC_CREATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `FD_DEPARTMENTNUMBER` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门代码',
  `FD_ISCHARGE` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否主管',
  `FD_PARENTNAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上级职位名称',
  `FD_POSITIONGRADE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职等',
  `FD_RANK` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职级',
  `FD_POSITIONTYPE` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职类类型',
  `FD_DEPARTMENTNAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门名称',
  `FD_JOBCLASS` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职群职种',
  `FD_ISROOT` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否根节点',
  `FD_STATUS` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '岗位状态',
  `FD_OPERATETIME` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作时间',
  `FD_OID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统编号',
  `FD_JOBID` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职位系统编号',
  PRIMARY KEY (`FD_ID`) USING BTREE,
  INDEX `SYS_ORG_PERSON_POSITION_KEY1`(`FD_POSITION`) USING BTREE,
  INDEX `SYS_ORG_PERSON_POSITION_KEY2`(`FD_LDAP_UPDATE_FLAG`) USING BTREE,
  INDEX `SYS_ORG_PERSON_POSITION_KEY3`(`FD_DEPARTMENTNUMBER`) USING BTREE,
  INDEX `SYS_ORG_PERSON_POSITION_KEY4`(`FD_ISCHARGE`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '人员上级岗位表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_org_person_timezone
-- ----------------------------
DROP TABLE IF EXISTS `sys_org_person_timezone`;
CREATE TABLE `sys_org_person_timezone`  (
  `fd_id` bigint(36) NOT NULL COMMENT '主键ID',
  `zone_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '时区ID',
  `fd_lang_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '语言类型',
  `zone_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '时区名称',
  `fd_offset` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '偏移量',
  `is_effective` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否有效',
  `order_num` varchar(19) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '排序号码',
  `attribute1` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注1',
  `attribute2` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注2',
  `attribute3` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注3',
  `attribute4` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注4',
  `attribute5` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注5',
  `doc_creator_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `doc_create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `doc_last_update_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最后更新人ID',
  `doc_last_update_time` datetime(0) NULL DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`fd_id`) USING BTREE,
  INDEX `IDX_SYS_TIMEZONE_01`(`zone_id`, `fd_lang_type`) USING BTREE,
  INDEX `IDX_SYS_TIMEZONE_02`(`fd_lang_type`, `is_effective`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '时区表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_read_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_read_log`;
CREATE TABLE `sys_read_log`  (
  `FD_ID` bigint(36) NOT NULL COMMENT 'ID',
  `FD_READ_TIME` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '读时间',
  `FD_READER_CLIENT_IP` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'IP',
  `FD_IS_NEW_VERSION` tinyint(4) NULL DEFAULT NULL COMMENT '是否最新版本',
  `FD_KEY` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关键字',
  `FD_MODEL_NAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模块名',
  `FD_MODEL_ID` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模块ID',
  `FD_READER_ID` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '阅读者ID',
  `FD_READ_TYPE` int(10) NULL DEFAULT NULL COMMENT '阅读类型',
  PRIMARY KEY (`FD_ID`) USING BTREE,
  INDEX `IDX_SYS_READ_LOG_01`(`FD_MODEL_ID`) USING BTREE,
  INDEX `IDX_SYS_READ_LOG_02`(`FD_READER_ID`, `FD_MODEL_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '阅读日志' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_sdk_versions
-- ----------------------------
DROP TABLE IF EXISTS `sys_sdk_versions`;
CREATE TABLE `sys_sdk_versions`  (
  `FD_ID` bigint(36) NOT NULL COMMENT '主键',
  `SDk_NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'SDK名称',
  `SDK_VERSIONS` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'sdk版本号',
  `UPDATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`FD_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'SDK版本' ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
