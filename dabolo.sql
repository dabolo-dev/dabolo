DROP TABLE IF EXISTS `User`;
CREATE TABLE `User` (
  `user_id` char(32) NOT NULL COMMENT  '用户id',
  `user_pub` text NOT NULL COMMENT '用户公钥',
  `user_pri_en` text NOT NULL COMMENT '用户加密私钥',
  `user_active` bit(1) NOT NULL DEFAULT b'1' COMMENT '用户是否激活',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Third`;
CREATE TABLE `Third` (
  `third_type` enum('wechat') NOT NULL COMMENT '平台类型',
  `third_id` varchar(255) NOT NULL COMMENT '平台用户id',
  `user_id` char(32) NOT NULL COMMENT '用户id',
  PRIMARY KEY (`third_type`,`third_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;