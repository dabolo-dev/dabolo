

-- ----------------------------
-- Table structure for `Activity`
-- ----------------------------
DROP TABLE IF EXISTS `Activity`;
CREATE TABLE `Activity` (
  `activity_title` varchar(255) NOT NULL COMMENT '活动标题',
  `activity_desc` text NOT NULL COMMENT '活动描述',
  `activity_status` enum('cancel','finish','processing','publish','draft') NOT NULL DEFAULT 'draft' COMMENT '活动状态',
  `activity_id` char(32) NOT NULL COMMENT '活动id',
  `activity_effect` bit(1) NOT NULL DEFAULT b'1' COMMENT '活动是否有效',
  `activity_index` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '活动显示优先级',
  `activity_creator` char(32) NOT NULL COMMENT '活动组织者',
  `activity_sign_up_start` datetime DEFAULT NULL COMMENT '活动报名开始时间',
  `activity_sign_up_end` datetime NOT NULL COMMENT '活动报名结束时间',
  `activity_start` datetime NOT NULL COMMENT '活动开始时间',
  `activity_end` datetime NOT NULL COMMENT '活动结束',
  `activity_allow_persion` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '活动人数限制，默认0为不限制',
  `activity_charge` enum('AA','charge','free') NOT NULL DEFAULT 'free' COMMENT '收费情况',
  `activity_location` varchar(255) NOT NULL COMMENT '活动地点',
  `activity_note` varchar(255) DEFAULT NULL COMMENT '活动备注',
  `activity_is_public` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否是公开活动',
  `activity_location_latitude` double NOT NULL COMMENT '活动地点经度',
  `activity_location_longitude` double NOT NULL COMMENT '活动地点维度',
  `activity_location_geohash` varchar(255) NOT NULL COMMENT '活动地点的附件算法值',
  PRIMARY KEY (`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- ----------------------------
-- Records of Activity
-- ----------------------------

-- ----------------------------
-- Table structure for `ActivityAndLabel`
-- ----------------------------
DROP TABLE IF EXISTS `ActivityAndLabel`;
CREATE TABLE `ActivityAndLabel` (
  `activity_and_lable_activity_id` char(32) NOT NULL,
  `activity_and_label_label_name` char(32) NOT NULL,
  PRIMARY KEY (`activity_and_lable_activity_id`,`activity_and_label_label_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ActivityAndLabel
-- ----------------------------

-- ----------------------------
-- Table structure for `ActivityAndType`
-- ----------------------------
DROP TABLE IF EXISTS `ActivityAndType`;
CREATE TABLE `ActivityAndType` (
  `activity_and_type_type_id` char(32) NOT NULL,
  `activity_and_type_activity_id` char(32) NOT NULL,
  PRIMARY KEY (`activity_and_type_activity_id`,`activity_and_type_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ActivityAndType
-- ----------------------------

-- ----------------------------
-- Table structure for `ActivityAndUser`
-- ----------------------------
DROP TABLE IF EXISTS `ActivityAndUser`;
CREATE TABLE `ActivityAndUser` (
  `activity_and_user_user_id` char(32) NOT NULL,
  `activity_and_user_activity_id` char(32) NOT NULL,
  `activity_and_user_time` datetime NOT NULL,
  `activity_and_user_participate` bit(1) NOT NULL DEFAULT b'0',
  `activity_and_user_praise` bit(1) NOT NULL DEFAULT b'0',
  `activity_and_user_attention` bit(1) NOT NULL DEFAULT b'0',
  `activity_and_user_signin` bit(1) NOT NULL DEFAULT b'0',
  `activity_and_user_persion_count` int(10) unsigned NOT NULL DEFAULT '0',
  `activity_and_user_note` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ActivityAndUser
-- ----------------------------

-- ----------------------------
-- Table structure for `ActivityType`
-- ----------------------------
DROP TABLE IF EXISTS `ActivityType`;
CREATE TABLE `ActivityType` (
  `activity_type_id` char(32) NOT NULL,
  `activity_type_name` varchar(255) NOT NULL,
  PRIMARY KEY (`activity_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ActivityType
-- ----------------------------

-- ----------------------------
-- Table structure for `Comment`
-- ----------------------------
DROP TABLE IF EXISTS `Comment`;
CREATE TABLE `Comment` (
  `comment_id` char(32) NOT NULL,
  `comment_desc` varchar(255) NOT NULL,
  `comment_creator` char(32) NOT NULL,
  `comment_time` datetime NOT NULL,
  `comment_object` char(32) NOT NULL,
  PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Records of Comment
-- ----------------------------
DROP TABLE IF EXISTS `CommentAndPic`;
CREATE TABLE `CommentAndPic` (
  `comment_and_pic_pic_id` char(32) NOT NULL,
  `comment_and_pic_comment_id` char(32) NOT NULL,
  PRIMARY KEY (`comment_and_pic_comment_id`,`comment_and_pic_pic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- ----------------------------
-- Table structure for `Label`
-- ----------------------------
DROP TABLE IF EXISTS `Label`;
CREATE TABLE `Label` (
  `label_id` char(32) NOT NULL,
  `label_name` varchar(255) NOT NULL,
  PRIMARY KEY (`label_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of Label
-- ----------------------------

-- ----------------------------
-- Table structure for `Picture`
-- ----------------------------
DROP TABLE IF EXISTS `Picture`;
CREATE TABLE `Picture` (
  `pic_id` char(32) NOT NULL,
  `pic_name` varchar(255) NOT NULL,
  `pic_creator` char(32) NOT NULL,
  `pic_activity_id` char(32) NOT NULL default '-1',
  `pic_face` bit(1) NOT NULL DEFAULT b'0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of Picture
-- ----------------------------

-- ----------------------------
-- Table structure for `Third`
-- ----------------------------
DROP TABLE IF EXISTS `Third`;
CREATE TABLE `Third` (
  `third_type` enum('wechat') NOT NULL COMMENT '平台类型',
  `third_id` varchar(255) NOT NULL COMMENT '平台用户id',
  `user_id` char(32) NOT NULL COMMENT '用户id',
  PRIMARY KEY (`third_type`,`third_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of Third
-- ----------------------------

-- ----------------------------
-- Table structure for `User`
-- ----------------------------
DROP TABLE IF EXISTS `User`;
CREATE TABLE `User` (
  `user_id` char(32) NOT NULL COMMENT '用户id',
  `user_pub` text NOT NULL COMMENT '用户公钥',
  `user_pri_en` text NOT NULL COMMENT '用户加密私钥',
  `user_active` bit(1) NOT NULL DEFAULT b'1' COMMENT '用户是否激活',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `UserInfo`;
CREATE TABLE `UserInfo` (
  `user_info_login_name` varchar(255) NOT NULL,
  `user_info_nick_name` varchar(255) DEFAULT NULL,
  `user_info_online` bit(1) NOT NULL DEFAULT b'0',
  `user_info_user_id` char(32) NOT NULL,
  PRIMARY KEY (`user_info_user_id`,`user_info_login_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


