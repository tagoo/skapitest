/*
 Navicat Premium Data Transfer

 Source Server         : 10.17.148.61(gateway)
 Source Server Type    : MySQL
 Source Server Version : 100118
 Source Host           : 10.17.148.61:3306
 Source Schema         : mgateway_db

 Target Server Type    : MySQL
 Target Server Version : 100118
 File Encoding         : 65001

 Date: 18/06/2019 08:34:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for api
-- ----------------------------
DROP TABLE IF EXISTS `api`;
CREATE TABLE `api`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `created` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `gateway_id` bigint(20) NOT NULL COMMENT '发布到的网关id',
  `group_id` bigint(20) NOT NULL COMMENT '所属分组id',
  `in_gateway` int(11) NULL DEFAULT NULL COMMENT '已发布到网关，空及0为未发布，1为已发布',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'api名称',
  `request_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '前端请求的路径',
  `service_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '微服务名',
  `service_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '后端对应微服务的路径',
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `api_user_id_index`(`user_id`) USING BTREE,
  INDEX `api_gateway_id_index`(`gateway_id`) USING BTREE,
  INDEX `api_group_id_index`(`group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 280 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'api，前端请求与后端服务的对应关系' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for api_filter_predicate
-- ----------------------------
DROP TABLE IF EXISTS `api_filter_predicate`;
CREATE TABLE `api_filter_predicate`  (
  `fd_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `fd_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型filter/predicate 两个可选值',
  `fd_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `fd_args` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数，可以是json或者逗号分割的字符串',
  `fd_api_id` bigint(20) NULL DEFAULT NULL COMMENT '关联Api表主键',
  PRIMARY KEY (`fd_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1411 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'Api Filter、Prediscate数据表，记录与Api关联的Filter和Predicate数据' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for api_group
-- ----------------------------
DROP TABLE IF EXISTS `api_group`;
CREATE TABLE `api_group`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `created` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '名称',
  `login_name` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人登录名',
  `login_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `api_group_name_index`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 263 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'api分组' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for container_label
-- ----------------------------
DROP TABLE IF EXISTS `container_label`;
CREATE TABLE `container_label`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `created` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '描述',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '集群名称',
  `env_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境名称',
  `operator_time` datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
  `operator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `label_name_index`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 321 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '集群' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for environment
-- ----------------------------
DROP TABLE IF EXISTS `environment`;
CREATE TABLE `environment`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环境名称',
  `tenant_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `description` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '描述',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `operator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人：账号',
  `operate_time` datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 157 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '环境' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for executable_jar
-- ----------------------------
DROP TABLE IF EXISTS `executable_jar`;
CREATE TABLE `executable_jar`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '描述',
  `jar_location` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'jar包存放位置，如oss的url',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'jar包名称',
  `src_location` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '源码路径，如svn',
  `version` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '版本',
  `extra_jvm_param` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '启动网关额外的jvm参数',
  `fd_image_name` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '镜像名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_as0wq9e20l7ob8sm90a92q75k`(`version`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '可执行jar包' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for filter_predicate_info
-- ----------------------------
DROP TABLE IF EXISTS `filter_predicate_info`;
CREATE TABLE `filter_predicate_info`  (
  `fd_id` bigint(20) UNSIGNED NOT NULL COMMENT '主键',
  `fd_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型 可选值：filter/predicate',
  `fd_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `fd_args_demo` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数配置实例',
  PRIMARY KEY (`fd_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'Api Filter、Predicate静态数据表，记录当前支持的所有Filter和Predicate' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gateway
-- ----------------------------
DROP TABLE IF EXISTS `gateway`;
CREATE TABLE `gateway`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `container_label_id` bigint(20) NOT NULL COMMENT '集群id',
  `created` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
  `eureka_cluster_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'eureka集群id，微服务管理提供',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `executable_jar_id` bigint(20) NOT NULL COMMENT '客户网关jar包id',
  `executable_jar_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'jar包在集群服务器的存放位置，绝对路径',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '名称',
  `port` int(11) NOT NULL COMMENT '网关运行绑定的端口号',
  `publish` int(11) NULL DEFAULT NULL COMMENT '是否发布到集群，0和空-未发布，1-已发布',
  `security_enable` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '网关是否启用登录校验，0表示不启用，1表示启用',
  `fd_install_type` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '安装类型：cluster、docker',
  `fd_docker_id` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '容器id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `gateway_name_index`(`name`) USING BTREE,
  INDEX `gateway_label_id_index`(`container_label_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 224 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '网关' ROW_FORMAT = Compact;

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
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '记录网关上配置的授权外部应用' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gateway_redis
-- ----------------------------
DROP TABLE IF EXISTS `gateway_redis`;
CREATE TABLE `gateway_redis`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `host` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主机ip',
  `port` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主机端口号',
  `username` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `use_database` int(11) NULL DEFAULT 0 COMMENT '当前使用Redis第几号库',
  `gateway_id` bigint(20) NULL DEFAULT NULL COMMENT '网关id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 91 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '网关部署使用的redis配置' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for grey_strategy
-- ----------------------------
DROP TABLE IF EXISTS `grey_strategy`;
CREATE TABLE `grey_strategy`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '名称',
  `effect` tinyint(4) NULL DEFAULT 0 COMMENT '是否在api上生效',
  `created` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `strategy_name_index`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '灰度策略' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for grey_strategy_api
-- ----------------------------
DROP TABLE IF EXISTS `grey_strategy_api`;
CREATE TABLE `grey_strategy_api`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `api_id` bigint(20) NOT NULL COMMENT 'api的主键id',
  `strategy_id` bigint(20) NOT NULL COMMENT '灰度策略id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `strategy_api_rel_api_idx`(`api_id`) USING BTREE,
  INDEX `strategy_api_rel_str_idx`(`strategy_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 169 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'api与灰度策略的关系表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for grey_strategy_content
-- ----------------------------
DROP TABLE IF EXISTS `grey_strategy_content`;
CREATE TABLE `grey_strategy_content`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `skey` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '键值对的key',
  `strategy_id` bigint(20) NOT NULL COMMENT '策略id',
  `svalue` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '键值对的value',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `strategy_cont_sta_id_idx`(`strategy_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 277 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '灰度策略的内容' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for label_vmserver
-- ----------------------------
DROP TABLE IF EXISTS `label_vmserver`;
CREATE TABLE `label_vmserver`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `label_id` bigint(20) NOT NULL COMMENT '集群id',
  `server_id` bigint(20) NOT NULL COMMENT '服务器id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `lab_server_rel_lab_id_idx`(`label_id`) USING BTREE,
  INDEX `lab_server_rel_ser_id_idx`(`server_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 794 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '集群-服务器关系' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for security_anon_intf
-- ----------------------------
DROP TABLE IF EXISTS `security_anon_intf`;
CREATE TABLE `security_anon_intf`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gateway_id` bigint(20) NOT NULL COMMENT '网关id',
  `intf_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '接口地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 191 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '网关匿名接口配置表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for traffic_strategy
-- ----------------------------
DROP TABLE IF EXISTS `traffic_strategy`;
CREATE TABLE `traffic_strategy`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '策略名称',
  `time_window` int(11) NULL DEFAULT NULL COMMENT '时间窗',
  `time_unit` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '时间单位',
  `api_limit` int(11) NULL DEFAULT NULL COMMENT 'api限流次数',
  `user_limit` int(11) NULL DEFAULT NULL COMMENT '用户登录限流次数',
  `app_limit` int(11) NULL DEFAULT NULL COMMENT '应用限流次数',
  `ip_limit` int(11) NULL DEFAULT NULL COMMENT 'ip限流次数',
  `tenant_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户id',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '流控策略信息表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for traffic_strategy_api
-- ----------------------------
DROP TABLE IF EXISTS `traffic_strategy_api`;
CREATE TABLE `traffic_strategy_api`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `api_id` bigint(20) NULL DEFAULT NULL COMMENT 'api id',
  `traffic_strategy_id` bigint(20) NULL DEFAULT NULL COMMENT '限流id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 109 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '流控策略绑定api' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for user_api_group
-- ----------------------------
DROP TABLE IF EXISTS `user_api_group`;
CREATE TABLE `user_api_group`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `api_group_id` bigint(20) NOT NULL COMMENT 'api分组id',
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_group_user_id_idx`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 264 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '租户api分组关系' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for user_gateway
-- ----------------------------
DROP TABLE IF EXISTS `user_gateway`;
CREATE TABLE `user_gateway`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `gateway_id` bigint(20) NOT NULL COMMENT '网关id',
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_gateway_user_id_idx`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 224 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '租户-网关关系' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for user_grey_strategy
-- ----------------------------
DROP TABLE IF EXISTS `user_grey_strategy`;
CREATE TABLE `user_grey_strategy`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `strategy_id` bigint(20) NOT NULL COMMENT '灰度策略id',
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_strategy_user_id_idx`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '租户-灰度策略关系' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for user_label
-- ----------------------------
DROP TABLE IF EXISTS `user_label`;
CREATE TABLE `user_label`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `label_id` bigint(20) NOT NULL COMMENT '集群id',
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_label_user_id_idx`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 322 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '租户-集群关系' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for user_vmserver
-- ----------------------------
DROP TABLE IF EXISTS `user_vmserver`;
CREATE TABLE `user_vmserver`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `host` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'host',
  `server_id` bigint(20) NOT NULL COMMENT '虚拟机id',
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_server_user_id_idx`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 293 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '租户-虚拟机关系' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for vmserver
-- ----------------------------
DROP TABLE IF EXISTS `vmserver`;
CREATE TABLE `vmserver`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `host` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'host',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
  `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务器密码',
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ssh用户名',
  `created_time` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
  `auth_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '验证类型,PASSWORD  或 PUBLIC_KEY',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `vmserver_name_idx`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 355 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '虚拟机' ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
