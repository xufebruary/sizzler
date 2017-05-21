/*
Navicat MySQL Data Transfer

Source Server         : local-sizzler
Source Server Version : 50511
Source Host           : localhost:3306
Source Database       : sizzler

Target Server Type    : MYSQL
Target Server Version : 50511
File Encoding         : 65001

Date: 2017-05-21 11:27:52
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `ga_widget_info`
-- ----------------------------
DROP TABLE IF EXISTS `ga_widget_info`;
CREATE TABLE `ga_widget_info` (
  `Ga_Widget_Info_ID` bigint(32) NOT NULL AUTO_INCREMENT,
  `Variable_ID` varchar(255) NOT NULL,
  `Widget_ID` varchar(255) NOT NULL,
  `Account_ID` varchar(255) DEFAULT NULL,
  `Property_ID` varchar(255) DEFAULT NULL,
  `Profile_ID` varchar(255) DEFAULT NULL,
  `metrics` text,
  `Metrics_ID` text,
  `ignore_null_metrics` tinyint(4) NOT NULL DEFAULT '0',
  `dimensions` text,
  `Dimensions_ID` text,
  `ignore_null_dimension` tinyint(4) NOT NULL DEFAULT '0',
  `Sort` text,
  `Filters` text,
  `Segment` text,
  `Max_Result` int(20) DEFAULT NULL,
  `Account_Name` varchar(255) DEFAULT NULL,
  `connection_id` varchar(255) DEFAULT NULL,
  `Ds_Id` bigint(20) DEFAULT NULL,
  `Uid` bigint(20) DEFAULT NULL,
  `Panel_ID` varchar(255) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '1',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '依赖mysql自动更新的记录修改时间',
  `fields` text COMMENT 'widget所拖拽的指标，维度等字段',
  `time` text COMMENT 'widget时间选择',
  `ds_connection_id` varchar(255) DEFAULT NULL COMMENT '关联ptone_ds_connection_config的ds_connection_id字段',
  PRIMARY KEY (`Ga_Widget_Info_ID`),
  KEY `NewIndex1` (`Variable_ID`,`Widget_ID`),
  KEY `NewIndex2` (`Widget_ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=43933 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ga_widget_info
-- ----------------------------
INSERT INTO `ga_widget_info` VALUES ('43932', '07779c02-c9fe-4ff2-843d-254b35840cd1', '6b26bb8d-1884-42e1-87f6-755db382ed2a', null, null, '912281b9-1bbe-4d2b-8b87-8763ee092432', '[]', '', '0', '[{\"calculateType\":\"SUM\",\"code\":\"Pt_ID\",\"dataFormat\":\"\",\"dataType\":\"NUMBER\",\"datePeriod\":\"\",\"id\":\"0dc116ad-0c1c-4dd8-9ead-f82753576f81\",\"isShowOnTimeDropdowns\":0,\"name\":\"Pt_ID\",\"realName\":\"Pt_ID\",\"uuid\":\"025b47a5-56d6-4393-86e3-6b3cf6fb83a3\"},{\"calculateType\":\"COUNTA\",\"code\":\"User_Name\",\"dataFormat\":\"\",\"dataType\":\"STRING\",\"datePeriod\":\"\",\"id\":\"f5ec3402-243c-4293-80ec-10cdeff59fb1\",\"isShowOnTimeDropdowns\":0,\"name\":\"User_Name\",\"realName\":\"User_Name\",\"uuid\":\"8c6e358a-5f85-4359-8e26-c82bb3513e8a\"},{\"calculateType\":\"COUNTA\",\"code\":\"User_Email\",\"dataFormat\":\"\",\"dataType\":\"STRING\",\"datePeriod\":\"\",\"id\":\"90eab13f-6021-4649-add8-557375f16ffc\",\"isShowOnTimeDropdowns\":0,\"name\":\"User_Email\",\"realName\":\"User_Email\",\"uuid\":\"a536d591-69be-4807-8b23-e3635e1e0ec0\"}]', '0dc116ad-0c1c-4dd8-9ead-f82753576f81,f5ec3402-243c-4293-80ec-10cdeff59fb1,90eab13f-6021-4649-add8-557375f16ffc', '0', '', 'null', 'null', null, '127.0.0.1-sizzler', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '5', '1', '0e8d0c34-1adf-40e1-b5c8-791698529179', '1', '2017-05-21 10:52:33', null, null, null);

-- ----------------------------
-- Table structure for `panel_global_component`
-- ----------------------------
DROP TABLE IF EXISTS `panel_global_component`;
CREATE TABLE `panel_global_component` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `panel_id` varchar(255) NOT NULL,
  `item_id` bigint(20) NOT NULL,
  `code` varchar(1024) DEFAULT NULL,
  `name` varchar(1024) DEFAULT NULL,
  `value` varchar(1024) NOT NULL DEFAULT 'widgetTime',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1:有效 0：无效',
  `uid` int(12) DEFAULT NULL,
  `is_delete` bigint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `panel_id` (`panel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of panel_global_component
-- ----------------------------

-- ----------------------------
-- Table structure for `provider_info`
-- ----------------------------
DROP TABLE IF EXISTS `provider_info`;
CREATE TABLE `provider_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ds_id` bigint(20) DEFAULT NULL,
  `ds_code` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  `client_secret` varchar(255) DEFAULT NULL,
  `default_scope` varchar(255) DEFAULT NULL,
  `default_parameters` varchar(255) DEFAULT NULL,
  `default_callback` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`ds_code`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of provider_info
-- ----------------------------
INSERT INTO `provider_info` VALUES ('2', '1', 'googleanalysis', 'googleanalysis0', '1035821915519-9a559lo2aa3tpic4thinbuppbpeu1aqn.apps.googleusercontent.com', 'tX6XhF49clwR8dJpqlXa3-K1', 'https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/analytics.readonly', 'access_type=offline;approval_prompt=force', 'http://ptone.test.com/connect/oauth2Callback/googleanalysis', '-1');
INSERT INTO `provider_info` VALUES ('3', '3', 'googleadwords', 'googleadwords0', '1023490303349-dahr768hbl9u2r0ap750tgbidql0l2r0.apps.googleusercontent.com', 'gq3jTgcPyeOoT5aP9daA0Np3', 'https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/adwords', 'access_type=offline;approval_prompt=force', 'http://ptone.test.com/connect/oauth2Callback/googleadwords', '-1');
INSERT INTO `provider_info` VALUES ('4', '4', 'googlespreadsheet', 'googlespreadsheet0', '711504042988-o1hlape92i5darota058f4domog23737.apps.googleusercontent.com', 'yuIJwJ8W6TWaa6IjROy3sz7U', 'https://www.googleapis.com/auth/userinfo.email%20https://spreadsheets.google.com/feeds%20https://www.googleapis.com/auth/drive', 'access_type=offline;approval_prompt=force', 'http://ptone.test.com/connect/oauth2Callback/googlespreadsheet', '-1');
INSERT INTO `provider_info` VALUES ('5', '6', 'googledrive', 'googledrive1', '429305505425-4q9sakd4kmudgvsosh6a10jnkggjrrod.apps.googleusercontent.com', 'AEnNHYX061-Ev8gUSzgXC78-', 'https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/drive', 'access_type=offline;approval_prompt=force', 'http://ptone.test.com/connect/oauth2Callback/googledrive', '0');
INSERT INTO `provider_info` VALUES ('6', '6', 'googledrive', 'googledrive0', '749858407111-dsamak5irm2dtkfs4ma6tpd542spgc58.apps.googleusercontent.com', '1lDPQiRIlstNkiH-3lNWA10g', 'https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/drive', 'access_type=offline;approval_prompt=force', 'http://ptone.test.com/connect/oauth2Callback/googledrive', '-1');
INSERT INTO `provider_info` VALUES ('7', '12', 'facebookad', 'facebookad0', '800460813392881', 'c86e58c2be68644b446aea4f9db98139', 'ads_management%2Cads_read%2Cpublic_profile%2Cemail', 'display=popup', 'http://ptone.test.com/connect/oauth2Callback/facebookad', '-1');
INSERT INTO `provider_info` VALUES ('8', '14', 'bigquery', 'bigquery0', '711504042988-o1hlape92i5darota058f4domog23737.apps.googleusercontent.com', 'yuIJwJ8W6TWaa6IjROy3sz7U', 'https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/bigquery', 'access_type=offline;approval_prompt=force', 'http://ptone.test.com/connect/oauth2Callback/bigquery', '-1');
INSERT INTO `provider_info` VALUES ('9', '15', 'googlelogin', 'googlelogin0', '623984719874-2frtjmnggvq49p1vagls4fb4p0el0kal.apps.googleusercontent.com', 'Yjk4f9ctCi_VRmVvS8tNUVMI', 'https://www.googleapis.com/auth/userinfo.email', 'access_type=offline', 'http://ptone.test.com/connect/oauth2Callback/googlelogin', '-1');
INSERT INTO `provider_info` VALUES ('10', '16', 'linkedinlogin', 'linkedinlogin0', '755zfalgc6tqym', 'nESSZb8iOUdBOgfL', 'r_basicprofile%20r_emailaddress', '', 'http://ptone.test.com/connect/oauth2Callback/linkedinlogin', '-1');
INSERT INTO `provider_info` VALUES ('11', '17', 'facebooklogin', 'facebooklogin0', '233717053649257', 'e3a9f74da125bf73ade8ad4328f6e45f', 'public_profile%2Cemail', 'display=popup', 'http://ptone.test.com/connect/oauth2Callback/facebooklogin', '-1');
INSERT INTO `provider_info` VALUES ('12', '18', 'twitter', 'twitter0', 'RMi0B2jXTvPxYfVbWmof80rw4', 'q7jKDUPMYZAvGqnGMtwMA4ddSAj56pscaL6qn1m6KqQfbBsDSs', null, null, 'http://ptone.test.com/connect/oauth1Callback/twitter', '-1');
INSERT INTO `provider_info` VALUES ('13', '19', 'salesforce', 'salesforce0', '3MVG9ZL0ppGP5UrC_f76w4d9En7ikKKkDHVhaLabHoNMnlHaL55jnT_3V8oBRTuWCi44uvNfo67BmZv_XJ5f7', '6955264627882778992', null, null, 'https://ptone.test.com:80/connect/oauth2Callback/salesforce', '-1');
INSERT INTO `provider_info` VALUES ('14', '21', 'doubleclick', 'doubleclick0', '1046789044750-shtnkm4f656s60bmp0icoq2t27ah6icq.apps.googleusercontent.com', 'Ga_S7TRFyQl9OhppCkTqKSta', 'https://www.googleapis.com/auth/dfatrafficking%20https://www.googleapis.com/auth/dfareporting%20https://www.googleapis.com/auth/userinfo.email', 'access_type=offline;approval_prompt=force', 'http://ptone.test.com/connect/oauth2Callback/doubleclick', '-1');
INSERT INTO `provider_info` VALUES ('15', '22', 'doubleclickCompound', 'doubleclickCompound0', '1074419346696-3du97hffdkg5gnle6p5ito899doc9g91.apps.googleusercontent.com', '0aot9-xCvV1qZyc0Yz-eVTHq', 'https://www.googleapis.com/auth/dfatrafficking%20https://www.googleapis.com/auth/dfareporting%20https://www.googleapis.com/auth/userinfo.email', 'access_type=offline;approval_prompt=force', 'http://ptone.test.com/connect/oauth2Callback/doubleclickCompound', '-1');
INSERT INTO `provider_info` VALUES ('16', '23', 'paypal', 'paypal0', '{\"appId\":\"APP-53H78023WM6990527\",\"certKey\":\"Ptone2016\",\"certPath\":\"/root/ptone/certificate/paypal_cert.p12\",\"password\":\"SGSBL6EPALQ4JFBR\",\"userName\":\"payment_api1.miapex.com\",\"mode\":\"live\"}', 'EMOgbct6TvmLf9waqyE_3C4J2jU9jxuppBYvoE7_FP_h3EZ9V25f6xJh3d7gwEweY7K_ZPpIuDaopHM5', 'TRANSACTION_SEARCH,ACCESS_BASIC_PERSONAL_DATA,ACCESS_ADVANCED_PERSONAL_DATA', null, 'http://ptone.test.com/connect/callback/paypal', '-1');
INSERT INTO `provider_info` VALUES ('17', '24', 'paypal', 'paypalAuth0', '{\"appId\":\"APP-53H78023WM6990527\",\"certKey\":\"Ptone2016\",\"certPath\":\"D:\\\\paypal_cert.p12\",\"password\":\"SGSBL6EPALQ4JFBR\",\"userName\":\"payment_api1.miapex.com\",\"mode\":\"live\"}', null, 'TRANSACTION_SEARCH,ACCESS_BASIC_PERSONAL_DATA,ACCESS_ADVANCED_PERSONAL_DATA', null, 'http://ptone.test.com/connect/callback/paypal', '0');
INSERT INTO `provider_info` VALUES ('18', '25', 'stripe', 'stripe0', 'ca_8armtixIrI19J8VlF5NOQyQZWDVLSbS5', 'sk_live_OQVSuOu3YJwfziOK5A0cnbqa', 'read_only', 'response_type=code', 'https://ptone.test.com/connect/oauth2Callback/stripe', '-1');
INSERT INTO `provider_info` VALUES ('19', '27', 'mailchimp', 'mailchimp0', '966539466008', 'cfcc59a2c71028d0ac20e37fb8af17af', null, 'response_type=code', 'http://ptone.test.com/connect/oauth2Callback/mailchimp', '-1');
INSERT INTO `provider_info` VALUES ('20', '28', 'googleadsense', 'googleadsense0', '872351089545-von3o9u3jtqel4ifjput01jschbu62j0.apps.googleusercontent.com', '_L5VzZ37MWg3o6N2xTsb81Y9', 'https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/adsense.readonly', 'access_type=offline;approval_prompt=force', 'http://ptone.test.com/connect/oauth2Callback/googleadsense', '-1');
INSERT INTO `provider_info` VALUES ('21', '29', 'facebookPages', 'facebookPages0', '866566216782195', 'd6e2d20c8eeeae5c3ba65d43f60726c8', 'email,read_insights,manage_pages,publish_pages,pages_show_list,public_profile', 'display=popup', 'http://ptone.test.com/connect/oauth2Callback/facebookPages', '-1');
INSERT INTO `provider_info` VALUES ('22', '33', 'zendesk', 'zendesk0', 'testptone', '1c06b7bdf1ea3d05b65e9b753b1782d759cf1f2b3e2742bb9389bee5aa976761', 'read', 'response_type=code', 'https://ptone.test.com/connect/oauth2Callback/zendesk', '-1');
INSERT INTO `provider_info` VALUES ('23', '34', 'doubleclickBidManager', 'doubleclickBidManager0', '594356465236-us2oomi6vgq3nq8nmq0sgacs7tusogva.apps.googleusercontent.com', 'G2T5EwgMP4KcJlcs43iBrGCO', 'https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/doubleclickbidmanager', 'access_type=offline;approval_prompt=force', 'http://ptone.test.com/connect/oauth2Callback/doubleclickBidManager', '-1');
INSERT INTO `provider_info` VALUES ('24', '1', 'googleanalysis', 'googleanalysis0', '1035821915519-9a559lo2aa3tpic4thinbuppbpeu1aqn.apps.googleusercontent.com', 'tX6XhF49clwR8dJpqlXa3-K1', 'https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/analytics.readonly', 'access_type=offline;approval_prompt=force', 'http://ptone.dev.com/connect/oauth2Callback/googleanalysis', '-2');

-- ----------------------------
-- Table structure for `ptone_basic_chart_info`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_basic_chart_info`;
CREATE TABLE `ptone_basic_chart_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL COMMENT '统一小写',
  `data_type` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL COMMENT 'chart or tool',
  `config` text,
  `description` varchar(255) DEFAULT NULL,
  `order_number` int(11) DEFAULT NULL,
  `is_delete` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `NewIndex1` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10401 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_basic_chart_info
-- ----------------------------
INSERT INTO `ptone_basic_chart_info` VALUES ('100', 'line', 'line', 'line', 'chart', '{\"minSizeX\": 6,\"minSizeY\": 8,\"maxSizeX\": null,\"maxSizeY\": null,\"defaultSizeX\": 6,\"defaultSizeY\": 8}', 'spline', '4', '0');
INSERT INTO `ptone_basic_chart_info` VALUES ('220', 'areaspline', 'areaspline', 'line', 'chart', '{\"minSizeX\": 6,\"minSizeY\": 8,\"maxSizeX\": null,\"maxSizeY\": null,\"defaultSizeX\": 6,\"defaultSizeY\": 8}', null, '20', '1');
INSERT INTO `ptone_basic_chart_info` VALUES ('300', 'column', 'column', 'line', 'chart', '{\"minSizeX\": 6,\"minSizeY\": 8,\"maxSizeX\": null,\"maxSizeY\": null,\"defaultSizeX\": 6,\"defaultSizeY\": 8}', null, '2', '0');
INSERT INTO `ptone_basic_chart_info` VALUES ('400', 'bar', 'bar', 'line', 'chart', '{\"minSizeX\": 6,\"minSizeY\": 8,\"maxSizeX\": null,\"maxSizeY\": null,\"defaultSizeX\": 6,\"defaultSizeY\": 8}', null, '3', '0');
INSERT INTO `ptone_basic_chart_info` VALUES ('500', 'pie', 'pie', 'pie', 'chart', '{\"minSizeX\": 6,\"minSizeY\": 8,\"maxSizeX\": null,\"maxSizeY\": null,\"defaultSizeX\": 6,\"defaultSizeY\": 8}', null, '5', '0');
INSERT INTO `ptone_basic_chart_info` VALUES ('520', 'hollowpie', 'hollowpie', 'pie', 'chart', '{\"minSizeX\": 6,\"minSizeY\": 8,\"maxSizeX\": null,\"maxSizeY\": null,\"defaultSizeX\": 6,\"defaultSizeY\": 8}', null, '60', '1');
INSERT INTO `ptone_basic_chart_info` VALUES ('600', 'simplenumber', 'simplenumber', 'simplenumber', 'chart', '{\"minSizeX\": 6,\"minSizeY\": 8,\"maxSizeX\": null,\"maxSizeY\": null,\"defaultSizeX\": 6,\"defaultSizeY\": 8}', null, '70', '1');
INSERT INTO `ptone_basic_chart_info` VALUES ('620', 'number', 'number', 'qoqnumber', 'chart', '{\"minSizeX\": 6,\"minSizeY\": 8,\"maxSizeX\": null,\"maxSizeY\": null,\"defaultSizeX\": 6,\"defaultSizeY\": 8}', null, '6', '0');
INSERT INTO `ptone_basic_chart_info` VALUES ('700', 'circlepercent', 'circlepercent', 'simplenumber', 'chart', '{\"minSizeX\": 6,\"minSizeY\": 8,\"maxSizeX\": null,\"maxSizeY\": null,\"defaultSizeX\": 6,\"defaultSizeY\": 8}', null, '90', '1');
INSERT INTO `ptone_basic_chart_info` VALUES ('720', 'progressbar', 'progressbar', 'simplenumber', 'chart', '{\"minSizeX\": 6,\"minSizeY\": 8,\"maxSizeX\": null,\"maxSizeY\": null,\"defaultSizeX\": 6,\"defaultSizeY\": 8}', null, '7', '0');
INSERT INTO `ptone_basic_chart_info` VALUES ('800', 'table', 'table', 'table', 'chart', '{\"minSizeX\": 6,\"minSizeY\": 8,\"maxSizeX\": null,\"maxSizeY\": null,\"defaultSizeX\": 6,\"defaultSizeY\": 8}', null, '1', '0');
INSERT INTO `ptone_basic_chart_info` VALUES ('900', 'map', 'map', 'table', 'chart', '{\"minSizeX\": 6,\"minSizeY\": 8,\"maxSizeX\": null,\"maxSizeY\": null,\"defaultSizeX\": 6,\"defaultSizeY\": 8}', null, '8', '0');
INSERT INTO `ptone_basic_chart_info` VALUES ('10100', 'text', 'text', 'text', 'tool', '{\"minSizeX\": 6,\"minSizeY\": 8,\"maxSizeX\": null,\"maxSizeY\": null,\"defaultSizeX\": 6,\"defaultSizeY\": 8}', null, '10', '0');
INSERT INTO `ptone_basic_chart_info` VALUES ('10200', 'demo', 'demo', 'demo', 'demo', null, null, '100', '0');
INSERT INTO `ptone_basic_chart_info` VALUES ('10300', 'custom', 'custom', 'custom', 'custom', null, '', '100', '0');
INSERT INTO `ptone_basic_chart_info` VALUES ('10400', 'heatmap', 'heatmap', 'heatmap', 'tool', null, 'heatmap', '20', '0');

-- ----------------------------
-- Table structure for `ptone_basic_dict_category`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_basic_dict_category`;
CREATE TABLE `ptone_basic_dict_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `order_number` int(11) DEFAULT NULL,
  `is_delete` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_basic_dict_category
-- ----------------------------
INSERT INTO `ptone_basic_dict_category` VALUES ('1', '基础字典', 'basic', null, '10', '0');
INSERT INTO `ptone_basic_dict_category` VALUES ('2', '业务字典', 'business', null, '20', '0');

-- ----------------------------
-- Table structure for `ptone_basic_dict_define`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_basic_dict_define`;
CREATE TABLE `ptone_basic_dict_define` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `category_id` bigint(20) NOT NULL,
  `category_code` varchar(255) DEFAULT NULL,
  `order_number` int(11) DEFAULT NULL,
  `is_delete` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_basic_dict_define
-- ----------------------------
INSERT INTO `ptone_basic_dict_define` VALUES ('1', '性别', 'gender', null, '1', 'basic', null, '0');
INSERT INTO `ptone_basic_dict_define` VALUES ('2', '周起始日', 'week_start', '一周的起始日期，周日 or 周一', '1', 'basic', null, '0');
INSERT INTO `ptone_basic_dict_define` VALUES ('3', '语言', 'language', null, '1', 'basic', null, '0');
INSERT INTO `ptone_basic_dict_define` VALUES ('4', '数据类型', 'data_type', null, '1', 'basic', null, '0');
INSERT INTO `ptone_basic_dict_define` VALUES ('5', '全局控件', 'global_component', null, '1', 'basic', null, '0');

-- ----------------------------
-- Table structure for `ptone_basic_dict_item`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_basic_dict_item`;
CREATE TABLE `ptone_basic_dict_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `code` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `dict_id` bigint(20) NOT NULL,
  `dict_code` varchar(255) DEFAULT NULL,
  `order_number` int(11) DEFAULT NULL,
  `is_delete` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_basic_dict_item
-- ----------------------------
INSERT INTO `ptone_basic_dict_item` VALUES ('1', '{\"default\":\"男\",\"zh_CN\":\"男\",\"en_US\":\"Male\",\"ja_JP\":\"男jp\"}', 'male', null, '1', 'gender', '10', '0');
INSERT INTO `ptone_basic_dict_item` VALUES ('2', '{\"default\":\"女\",\"zh_CN\":\"女\",\"en_US\":\"Female\",\"ja_JP\":\"女jp\"}', 'female', null, '1', 'gender', '20', '0');
INSERT INTO `ptone_basic_dict_item` VALUES ('3', '{\"default\":\"Sunday\"}', 'sunday', null, '2', 'week_start', '10', '0');
INSERT INTO `ptone_basic_dict_item` VALUES ('4', '{\"default\":\"Monday\"}', 'monday', null, '2', 'week_start', '20', '0');
INSERT INTO `ptone_basic_dict_item` VALUES ('5', '{\"default\":\"中文\"}', 'zh_CN', null, '3', 'language', '10', '0');
INSERT INTO `ptone_basic_dict_item` VALUES ('6', '{\"default\":\"English\"}', 'en_US', null, '3', 'language', '20', '0');
INSERT INTO `ptone_basic_dict_item` VALUES ('7', '{\"default\":\"日文\"}', 'ja_JP', null, '3', 'language', '30', '0');
INSERT INTO `ptone_basic_dict_item` VALUES ('8', 'metrics', 'NUMBER', null, '4', 'data_type', '10', '0');
INSERT INTO `ptone_basic_dict_item` VALUES ('9', 'metrics', 'CURRENCY', null, '4', 'data_type', '20', '0');
INSERT INTO `ptone_basic_dict_item` VALUES ('10', 'metrics', 'PERCENT', null, '4', 'data_type', '30', '0');
INSERT INTO `ptone_basic_dict_item` VALUES ('11', 'dimension', 'STRING', null, '4', 'data_type', '40', '0');
INSERT INTO `ptone_basic_dict_item` VALUES ('12', 'dimension', 'DATE', null, '4', 'data_type', '50', '0');
INSERT INTO `ptone_basic_dict_item` VALUES ('13', 'dimension', 'TIME', null, '4', 'data_type', '60', '0');
INSERT INTO `ptone_basic_dict_item` VALUES ('14', 'dimension', 'DURATION', null, '4', 'data_type', '70', '0');
INSERT INTO `ptone_basic_dict_item` VALUES ('15', 'dimension', 'TIMESTAMP', null, '4', 'data_type', '80', '0');
INSERT INTO `ptone_basic_dict_item` VALUES ('16', 'GLOBAL_TIME', 'GLOBAL_TIME', null, '5', 'global_component', '10', '0');
INSERT INTO `ptone_basic_dict_item` VALUES ('17', 'dimension', 'DATETIME', null, '4', 'data_type', '90', '0');

-- ----------------------------
-- Table structure for `ptone_date_info`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_date_info`;
CREATE TABLE `ptone_date_info` (
  `Ptone_Date_Info_ID` int(20) NOT NULL AUTO_INCREMENT,
  `Date_Key` varchar(255) DEFAULT NULL,
  `Date_Value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Ptone_Date_Info_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_date_info
-- ----------------------------
INSERT INTO `ptone_date_info` VALUES ('10', 'today', 'Today');
INSERT INTO `ptone_date_info` VALUES ('14', 'yesterday', 'Yesterday');
INSERT INTO `ptone_date_info` VALUES ('20', 'this_week', 'This Week');
INSERT INTO `ptone_date_info` VALUES ('22', 'last_week', 'Last Week');
INSERT INTO `ptone_date_info` VALUES ('30', 'this_month', 'This Month');
INSERT INTO `ptone_date_info` VALUES ('32', 'last_month', 'Last Month');
INSERT INTO `ptone_date_info` VALUES ('40', 'last7day', 'Last 7 Days');
INSERT INTO `ptone_date_info` VALUES ('42', 'past7day', 'Past 7 Days');
INSERT INTO `ptone_date_info` VALUES ('44', 'last30day', 'Last 30 Days');
INSERT INTO `ptone_date_info` VALUES ('46', 'past30day', 'Past 30 Days');

-- ----------------------------
-- Table structure for `ptone_ds_dimension`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_ds_dimension`;
CREATE TABLE `ptone_ds_dimension` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `query_code` varchar(500) DEFAULT NULL COMMENT 'dimensionCode || metricsCode (过滤器查询用code)',
  `description` varchar(255) DEFAULT NULL,
  `ds_id` bigint(20) NOT NULL,
  `ds_code` varchar(255) DEFAULT NULL,
  `category_id` bigint(20) DEFAULT NULL COMMENT '所属分类ID',
  `category_code` varchar(255) DEFAULT NULL COMMENT '所属分类ID',
  `allow_segment` int(11) DEFAULT NULL COMMENT '针对GA，其他数据源暂时为0',
  `allow_filter` int(11) DEFAULT NULL,
  `order_number` int(11) DEFAULT NULL,
  `is_delete` int(11) NOT NULL,
  `i18n_code` varchar(255) DEFAULT NULL COMMENT '国际化的CODE',
  `data_type` varchar(255) DEFAULT NULL,
  `data_format` varchar(255) DEFAULT NULL,
  `has_filter_item` tinyint(4) DEFAULT NULL COMMENT '过滤器中是否含有固定选项，0：没有，1：有',
  `segment_user_session` tinyint(4) DEFAULT '2' COMMENT 'segment支持的级别：用户级:0，会话级:1,两者都是:2',
  `filter_user_session` tinyint(4) DEFAULT '2' COMMENT 'filter支持的级别：用户级:0，会话级:1,两者都是:2',
  `is_default_select` tinyint(4) DEFAULT '0' COMMENT '是否默认选中，0-否，1-是，设置1时，前端会将需要默认选中的指标维度显示在widget的指标维度列表，用户不能删除、修改',
  `is_show_on_time_dropdowns` tinyint(4) NOT NULL DEFAULT '1' COMMENT '标识Time控件中的下拉列表中是否显示',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_ds_dimension
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_ds_dimension_category`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_ds_dimension_category`;
CREATE TABLE `ptone_ds_dimension_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `ds_id` bigint(20) NOT NULL,
  `ds_code` varchar(255) DEFAULT NULL,
  `order_number` int(11) DEFAULT NULL,
  `is_delete` int(11) NOT NULL,
  `i18n_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_ds_dimension_category
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_ds_filter_item`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_ds_filter_item`;
CREATE TABLE `ptone_ds_filter_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `i18n_code` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `ds_id` bigint(20) NOT NULL,
  `ds_code` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL COMMENT 'metrics ||  dimension',
  `filter_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'dimensionId || metricsId',
  `filter_code` varchar(255) DEFAULT NULL COMMENT 'dimensionCode || metricsCode',
  `query_code` varchar(255) DEFAULT NULL COMMENT 'dimensionCode || metricsCode (过滤器查询用code)',
  `order_number` int(11) DEFAULT NULL,
  `is_delete` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_ds_filter_item
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_ds_info`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_ds_info`;
CREATE TABLE `ptone_ds_info` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `config` text,
  `is_support_templet` tinyint(4) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `order_number` int(11) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `query_type` varchar(255) DEFAULT NULL,
  `is_delete` int(11) NOT NULL,
  `is_show` tinyint(4) NOT NULL DEFAULT '1' COMMENT '0：不显示 1：显示',
  `is_plus` tinyint(4) DEFAULT NULL COMMENT '0:内测服务 1：高级服务 3：针对各别企业的服务',
  `order_cn` int(11) DEFAULT NULL COMMENT '中国区排序',
  `order_com` int(11) DEFAULT NULL COMMENT '欧美区排序',
  `order_jp` int(11) DEFAULT NULL COMMENT '日本区排序',
  `support_timezone` tinyint(4) DEFAULT NULL COMMENT '1:支持 0:不支持',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_ds_info
-- ----------------------------
INSERT INTO `ptone_ds_info` VALUES ('4', 'Excel/CSV ', 'upload', 'Model', '{\"config\":{\"widgetEditor\":{\"fields\":{\"getFiledsMethod\":\"Table\"}}}}', '0', 'Upload', '0', 'local', 'local', '1', '1', '2', '140', '140', '140', '1');
INSERT INTO `ptone_ds_info` VALUES ('5', 'MySQL', 'mysql', 'Model', '{\"config\":{\"widgetEditor\":{\"fields\":{\"getFiledsMethod\":\"Table\"}}}}', '0', 'MySQL', '3', 'rdatabase', 'remote', '0', '1', '2', '160', '160', '160', '1');

-- ----------------------------
-- Table structure for `ptone_ds_metrics`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_ds_metrics`;
CREATE TABLE `ptone_ds_metrics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `query_code` varchar(500) DEFAULT NULL COMMENT 'dimensionCode || metricsCode (过滤器查询用code)',
  `data_type` varchar(255) DEFAULT NULL,
  `data_format` varchar(255) DEFAULT NULL,
  `unit` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `default_date_period` varchar(255) DEFAULT NULL,
  `available_date_period` varchar(255) DEFAULT NULL,
  `ds_id` bigint(20) NOT NULL,
  `ds_code` varchar(255) DEFAULT NULL,
  `category_id` int(20) DEFAULT NULL COMMENT '所属分类ID',
  `category_code` varchar(255) DEFAULT NULL COMMENT '所属分类ID',
  `allow_segment` int(11) DEFAULT NULL,
  `allow_filter` int(11) DEFAULT NULL,
  `order_number` int(11) DEFAULT NULL,
  `is_delete` int(11) NOT NULL,
  `i18n_code` varchar(255) DEFAULT NULL,
  `segment_user_session` tinyint(4) DEFAULT '2' COMMENT 'segment过滤级别：用户：0，会话：1，两者都是：2',
  `filter_user_session` tinyint(4) DEFAULT '2' COMMENT 'filter支持等级：用户：0，会话：1，两者都是：2',
  `is_default_select` tinyint(4) DEFAULT '0' COMMENT '是否默认选中，0-否，1-是，设置1时，前端会将需要默认选中的指标维度显示在widget的指标维度列表，用户不能删除、修改',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_ds_metrics
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_ds_metrics_category`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_ds_metrics_category`;
CREATE TABLE `ptone_ds_metrics_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `ds_id` bigint(20) NOT NULL,
  `ds_code` varchar(255) DEFAULT NULL,
  `order_number` int(11) DEFAULT NULL,
  `is_delete` int(11) NOT NULL,
  `i18n_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_ds_metrics_category
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_panel_info`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_panel_info`;
CREATE TABLE `ptone_panel_info` (
  `Ptone_Panel_Info_ID` bigint(255) NOT NULL AUTO_INCREMENT,
  `Panel_ID` varchar(255) NOT NULL,
  `Panel_Title` varchar(255) DEFAULT NULL,
  `Creator_ID` varchar(255) NOT NULL,
  `Create_Time` bigint(20) DEFAULT NULL,
  `Status` char(4) NOT NULL DEFAULT '1',
  `Layout` text,
  `Panel_Width` varchar(255) DEFAULT NULL,
  `Default_Templet` tinyint(2) NOT NULL DEFAULT '0' COMMENT '1:是 0：不是',
  `order_number` int(11) DEFAULT NULL,
  `share_team` varchar(255) DEFAULT NULL COMMENT 'team分享状态：0 未共享，1：共享',
  `share_url` varchar(255) DEFAULT NULL COMMENT '分享url状态',
  `share_password` varchar(255) DEFAULT NULL COMMENT '分享密码',
  `share_date` varchar(255) DEFAULT NULL,
  `share_status` tinyint(4) DEFAULT NULL,
  `global_component_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '1:开启 0：关闭',
  `access` varchar(255) DEFAULT NULL,
  `share_source_id` varchar(255) DEFAULT NULL COMMENT '分享panel的来源ID',
  `share_source_status` tinyint(4) DEFAULT NULL COMMENT '来源panel状态 0：已删除 1：已关闭分享 2: 正常',
  `share_source_username` varchar(255) DEFAULT NULL COMMENT '分享panel的用户名（不是email）',
  `model` varchar(255) NOT NULL DEFAULT 'READ' COMMENT 'panel当前模式',
  `space_id` varchar(255) DEFAULT NULL COMMENT '空间ID',
  `space_name` varchar(255) DEFAULT NULL COMMENT '冗余字段',
  `description` longtext,
  `templet_id` varchar(255) DEFAULT NULL,
  `is_by_templet` tinyint(4) DEFAULT '0' COMMENT '1: 根据模板创建， 0：用户创建',
  `is_by_default` tinyint(4) DEFAULT '0' COMMENT '1: 预制panel， 0： 非预制panel',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '依赖mysql自动更新的记录修改时间',
  `source_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Ptone_Panel_Info_ID`),
  UNIQUE KEY `NewIndex1` (`Panel_ID`),
  KEY `share_source_id` (`share_source_id`),
  KEY `SPACE_ID` (`space_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_panel_info
-- ----------------------------
INSERT INTO `ptone_panel_info` VALUES ('1', '0e8d0c34-1adf-40e1-b5c8-791698529179', 'xupeng', '1', '1495334909968', '1', '%5B%0A%20%20%7B%0A%20%20%20%20%22id%22%3A%20%226b26bb8d-1884-42e1-87f6-755db382ed2a%22%2C%0A%20%20%20%20%22c%22%3A%200%2C%0A%20%20%20%20%22r%22%3A%200%2C%0A%20%20%20%20%22x%22%3A%2036%2C%0A%20%20%20%20%22y%22%3A%2012%0A%20%20%7D%0A%5D', null, '0', '0', null, null, null, null, null, '0', null, null, null, null, 'READ', '83a95adb-ffe9-4180-b638-998cb75f59ca', null, null, null, '0', '0', '2017-05-21 10:52:40', 'USER_CREATED');

-- ----------------------------
-- Table structure for `ptone_panel_layout`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_panel_layout`;
CREATE TABLE `ptone_panel_layout` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `panel_layout` longtext,
  `uid` varchar(255) DEFAULT NULL,
  `space_id` varchar(255) NOT NULL,
  `update_time` bigint(13) DEFAULT NULL,
  `panel_type` tinyint(4) DEFAULT NULL COMMENT 'type=0||null是用户的 type=1是管理员的',
  `data_version` bigint(20) DEFAULT '1' COMMENT '位置信息版本号',
  `is_delete` bigint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `space_id` (`space_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_panel_layout
-- ----------------------------
INSERT INTO `ptone_panel_layout` VALUES ('1', '[{\"columns\":[[{\"panelId\":\"0e8d0c34-1adf-40e1-b5c8-791698529179\",\"panelTitle\":\"xupeng\",\"type\":\"panel\"}]],\"containerId\":\"92395767-a4a1-46c6-a720-72c8664a0e6b\",\"containerName\":\"xupeng\",\"type\":\"container\",\"fold\":false}]', '1', '83a95adb-ffe9-4180-b638-998cb75f59ca', '1495334942609', null, '8', '0');

-- ----------------------------
-- Table structure for `ptone_panel_templet`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_panel_templet`;
CREATE TABLE `ptone_panel_templet` (
  `Ptone_Panel_Info_ID` bigint(255) NOT NULL AUTO_INCREMENT,
  `Panel_ID` varchar(255) NOT NULL,
  `Panel_Title` longtext,
  `Creator_ID` varchar(255) NOT NULL,
  `Create_Time` bigint(20) DEFAULT NULL,
  `Status` varchar(32) NOT NULL DEFAULT '1' COMMENT '0:无效，1：有效，3：默认预制,5,6,7中英日',
  `Layout` text,
  `Panel_Width` varchar(255) DEFAULT NULL,
  `Default_Templet` tinyint(2) NOT NULL DEFAULT '0' COMMENT '1:是 0：不是',
  `order_number` int(11) DEFAULT NULL,
  `Description` longtext,
  `space_id` varchar(255) DEFAULT NULL,
  `space_name` varchar(255) DEFAULT NULL COMMENT '冗余字段',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '依赖mysql自动更新的记录修改时间',
  PRIMARY KEY (`Ptone_Panel_Info_ID`),
  UNIQUE KEY `NewIndex1` (`Panel_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_panel_templet
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_panel_vision`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_panel_vision`;
CREATE TABLE `ptone_panel_vision` (
  `Ptone_Panel_Vision_ID` bigint(32) NOT NULL AUTO_INCREMENT,
  `Panel_ID` varchar(255) NOT NULL,
  `Widget_ID` varchar(255) NOT NULL,
  `Panel_Layout` text,
  PRIMARY KEY (`Ptone_Panel_Vision_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_panel_vision
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_panel_widget`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_panel_widget`;
CREATE TABLE `ptone_panel_widget` (
  `Ptone_Panel_Widget_ID` bigint(32) NOT NULL AUTO_INCREMENT,
  `Panel_ID` varchar(255) NOT NULL,
  `Widget_ID` varchar(255) NOT NULL,
  `is_delete` bigint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Ptone_Panel_Widget_ID`),
  KEY `NewIndex1` (`Panel_ID`),
  KEY `NewIndex2` (`Widget_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_panel_widget
-- ----------------------------
INSERT INTO `ptone_panel_widget` VALUES ('1', '0e8d0c34-1adf-40e1-b5c8-791698529179', '6b26bb8d-1884-42e1-87f6-755db382ed2a', '0');

-- ----------------------------
-- Table structure for `ptone_quartz_task`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_quartz_task`;
CREATE TABLE `ptone_quartz_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_id` varchar(255) DEFAULT NULL,
  `source_id` varchar(255) DEFAULT NULL,
  `connection_id` varchar(255) DEFAULT NULL,
  `time` varchar(255) DEFAULT NULL,
  `uid` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `task_name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_quartz_task
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_retain_domain`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_retain_domain`;
CREATE TABLE `ptone_retain_domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(255) DEFAULT NULL COMMENT 'string || regexp',
  `code` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=140 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_retain_domain
-- ----------------------------
INSERT INTO `ptone_retain_domain` VALUES ('1', 'string', 'apply', '1');
INSERT INTO `ptone_retain_domain` VALUES ('2', 'string', 'auth', '1');
INSERT INTO `ptone_retain_domain` VALUES ('3', 'string', 'css', '1');
INSERT INTO `ptone_retain_domain` VALUES ('4', 'string', 'collect', '1');
INSERT INTO `ptone_retain_domain` VALUES ('5', 'string', 'dev', '1');
INSERT INTO `ptone_retain_domain` VALUES ('6', 'string', 'developer', '1');
INSERT INTO `ptone_retain_domain` VALUES ('7', 'string', 'file', '1');
INSERT INTO `ptone_retain_domain` VALUES ('8', 'string', 'group', '1');
INSERT INTO `ptone_retain_domain` VALUES ('9', 'string', 'img', '1');
INSERT INTO `ptone_retain_domain` VALUES ('10', 'string', 'info', '1');
INSERT INTO `ptone_retain_domain` VALUES ('11', 'string', 'login', '1');
INSERT INTO `ptone_retain_domain` VALUES ('12', 'string', 'middle', '1');
INSERT INTO `ptone_retain_domain` VALUES ('13', 'string', 'monitor', '1');
INSERT INTO `ptone_retain_domain` VALUES ('14', 'string', 'monitor-jp', '1');
INSERT INTO `ptone_retain_domain` VALUES ('15', 'string', 'monitor-jptest', '1');
INSERT INTO `ptone_retain_domain` VALUES ('16', 'string', 'register', '1');
INSERT INTO `ptone_retain_domain` VALUES ('17', 'string', 'share', '1');
INSERT INTO `ptone_retain_domain` VALUES ('18', 'string', 'socket', '1');
INSERT INTO `ptone_retain_domain` VALUES ('19', 'string', 'ptmind', '1');
INSERT INTO `ptone_retain_domain` VALUES ('20', 'string', 'pt', '1');
INSERT INTO `ptone_retain_domain` VALUES ('21', 'string', 'ptengine', '1');
INSERT INTO `ptone_retain_domain` VALUES ('22', 'string', 'ptthink', '1');
INSERT INTO `ptone_retain_domain` VALUES ('23', 'string', 'ptone', '1');
INSERT INTO `ptone_retain_domain` VALUES ('24', 'string', 'test', '1');
INSERT INTO `ptone_retain_domain` VALUES ('25', 'string', 'testauth', '1');
INSERT INTO `ptone_retain_domain` VALUES ('26', 'string', 'testmiddle', '1');
INSERT INTO `ptone_retain_domain` VALUES ('27', 'string', 'testshare', '1');
INSERT INTO `ptone_retain_domain` VALUES ('28', 'string', 'testwww', '1');
INSERT INTO `ptone_retain_domain` VALUES ('29', 'string', 'trial', '1');
INSERT INTO `ptone_retain_domain` VALUES ('30', 'string', 'upload', '1');
INSERT INTO `ptone_retain_domain` VALUES ('31', 'string', 'websocket', '1');
INSERT INTO `ptone_retain_domain` VALUES ('32', 'string', 'www', '1');
INSERT INTO `ptone_retain_domain` VALUES ('33', 'string', 'marketing', '1');
INSERT INTO `ptone_retain_domain` VALUES ('34', 'string', 'mkt', '1');
INSERT INTO `ptone_retain_domain` VALUES ('35', 'string', 'sales', '1');
INSERT INTO `ptone_retain_domain` VALUES ('36', 'string', 'hr', '1');
INSERT INTO `ptone_retain_domain` VALUES ('37', 'string', 'it', '1');
INSERT INTO `ptone_retain_domain` VALUES ('38', 'string', 'bi', '1');
INSERT INTO `ptone_retain_domain` VALUES ('39', 'string', 'finance', '1');
INSERT INTO `ptone_retain_domain` VALUES ('40', 'string', 'financial', '1');
INSERT INTO `ptone_retain_domain` VALUES ('41', 'string', 'operation', '1');
INSERT INTO `ptone_retain_domain` VALUES ('42', 'string', 'pm', '1');
INSERT INTO `ptone_retain_domain` VALUES ('43', 'string', 'projectmanagement', '1');
INSERT INTO `ptone_retain_domain` VALUES ('44', 'string', 'crm', '1');
INSERT INTO `ptone_retain_domain` VALUES ('45', 'string', 'support', '1');
INSERT INTO `ptone_retain_domain` VALUES ('46', 'string', 'healthcare', '1');
INSERT INTO `ptone_retain_domain` VALUES ('47', 'string', 'health', '1');
INSERT INTO `ptone_retain_domain` VALUES ('48', 'string', 'education', '1');
INSERT INTO `ptone_retain_domain` VALUES ('49', 'string', 'consulting', '1');
INSERT INTO `ptone_retain_domain` VALUES ('50', 'string', 'ecommerce', '1');
INSERT INTO `ptone_retain_domain` VALUES ('51', 'string', 'manufacturing', '1');
INSERT INTO `ptone_retain_domain` VALUES ('52', 'string', 'manufacturer', '1');
INSERT INTO `ptone_retain_domain` VALUES ('53', 'string', 'resource', '1');
INSERT INTO `ptone_retain_domain` VALUES ('54', 'string', 'agriculture', '1');
INSERT INTO `ptone_retain_domain` VALUES ('55', 'string', 'energy', '1');
INSERT INTO `ptone_retain_domain` VALUES ('56', 'string', 'foodbeverage', '1');
INSERT INTO `ptone_retain_domain` VALUES ('57', 'string', 'sports', '1');
INSERT INTO `ptone_retain_domain` VALUES ('58', 'string', 'realestate', '1');
INSERT INTO `ptone_retain_domain` VALUES ('59', 'string', 'housing', '1');
INSERT INTO `ptone_retain_domain` VALUES ('60', 'string', 'printing', '1');
INSERT INTO `ptone_retain_domain` VALUES ('61', 'string', 'publishing', '1');
INSERT INTO `ptone_retain_domain` VALUES ('62', 'string', 'telecommunications', '1');
INSERT INTO `ptone_retain_domain` VALUES ('63', 'string', 'telecom', '1');
INSERT INTO `ptone_retain_domain` VALUES ('64', 'string', 'media', '1');
INSERT INTO `ptone_retain_domain` VALUES ('65', 'string', 'entertainment', '1');
INSERT INTO `ptone_retain_domain` VALUES ('66', 'string', 'blog', '1');
INSERT INTO `ptone_retain_domain` VALUES ('67', 'string', 'music', '1');
INSERT INTO `ptone_retain_domain` VALUES ('68', 'string', 'film', '1');
INSERT INTO `ptone_retain_domain` VALUES ('69', 'string', 'game', '1');
INSERT INTO `ptone_retain_domain` VALUES ('70', 'string', 'transportation', '1');
INSERT INTO `ptone_retain_domain` VALUES ('71', 'string', 'logistics', '1');
INSERT INTO `ptone_retain_domain` VALUES ('72', 'string', 'biotech', '1');
INSERT INTO `ptone_retain_domain` VALUES ('73', 'string', 'pharmacy', '1');
INSERT INTO `ptone_retain_domain` VALUES ('74', 'string', 'mining', '1');
INSERT INTO `ptone_retain_domain` VALUES ('75', 'string', 'drilling', '1');
INSERT INTO `ptone_retain_domain` VALUES ('76', 'string', 'miningdrilling', '1');
INSERT INTO `ptone_retain_domain` VALUES ('77', 'string', 'electricpower', '1');
INSERT INTO `ptone_retain_domain` VALUES ('78', 'string', 'power', '1');
INSERT INTO `ptone_retain_domain` VALUES ('79', 'string', 'electric', '1');
INSERT INTO `ptone_retain_domain` VALUES ('80', 'string', 'construction', '1');
INSERT INTO `ptone_retain_domain` VALUES ('81', 'string', 'chemical', '1');
INSERT INTO `ptone_retain_domain` VALUES ('82', 'string', 'automotive', '1');
INSERT INTO `ptone_retain_domain` VALUES ('83', 'string', 'consumergood', '1');
INSERT INTO `ptone_retain_domain` VALUES ('84', 'string', 'pornography', '1');
INSERT INTO `ptone_retain_domain` VALUES ('85', 'string', 'oil', '1');
INSERT INTO `ptone_retain_domain` VALUES ('86', 'string', 'gas', '1');
INSERT INTO `ptone_retain_domain` VALUES ('87', 'string', 'oilgas', '1');
INSERT INTO `ptone_retain_domain` VALUES ('88', 'string', 'nuclear', '1');
INSERT INTO `ptone_retain_domain` VALUES ('89', 'string', 'nuclearpower', '1');
INSERT INTO `ptone_retain_domain` VALUES ('90', 'string', 'power', '1');
INSERT INTO `ptone_retain_domain` VALUES ('91', 'string', 'account', '1');
INSERT INTO `ptone_retain_domain` VALUES ('92', 'string', 'accounting', '1');
INSERT INTO `ptone_retain_domain` VALUES ('93', 'string', 'service', '1');
INSERT INTO `ptone_retain_domain` VALUES ('94', 'string', 'businessintelligence', '1');
INSERT INTO `ptone_retain_domain` VALUES ('95', 'string', 'airline', '1');
INSERT INTO `ptone_retain_domain` VALUES ('96', 'string', 'aerospace', '1');
INSERT INTO `ptone_retain_domain` VALUES ('97', 'string', 'attorney', '1');
INSERT INTO `ptone_retain_domain` VALUES ('98', 'string', 'laws', '1');
INSERT INTO `ptone_retain_domain` VALUES ('99', 'string', 'bank', '1');
INSERT INTO `ptone_retain_domain` VALUES ('100', 'string', 'bankings', '1');
INSERT INTO `ptone_retain_domain` VALUES ('101', 'string', 'banking', '1');
INSERT INTO `ptone_retain_domain` VALUES ('102', 'string', 'restaurants', '1');
INSERT INTO `ptone_retain_domain` VALUES ('103', 'string', 'bars', '1');
INSERT INTO `ptone_retain_domain` VALUES ('104', 'string', 'barsrestaurants', '1');
INSERT INTO `ptone_retain_domain` VALUES ('105', 'string', 'broadcaster', '1');
INSERT INTO `ptone_retain_domain` VALUES ('106', 'string', 'broadcasters', '1');
INSERT INTO `ptone_retain_domain` VALUES ('107', 'string', 'broadcasts', '1');
INSERT INTO `ptone_retain_domain` VALUES ('108', 'string', 'broadcast', '1');
INSERT INTO `ptone_retain_domain` VALUES ('109', 'string', 'radio', '1');
INSERT INTO `ptone_retain_domain` VALUES ('110', 'string', 'tv', '1');
INSERT INTO `ptone_retain_domain` VALUES ('111', 'string', 'dentists', '1');
INSERT INTO `ptone_retain_domain` VALUES ('112', 'string', 'dentist', '1');
INSERT INTO `ptone_retain_domain` VALUES ('113', 'string', 'doctors', '1');
INSERT INTO `ptone_retain_domain` VALUES ('114', 'string', 'hospital', '1');
INSERT INTO `ptone_retain_domain` VALUES ('115', 'string', 'doctor', '1');
INSERT INTO `ptone_retain_domain` VALUES ('116', 'string', 'hospitals', '1');
INSERT INTO `ptone_retain_domain` VALUES ('117', 'string', 'environment', '1');
INSERT INTO `ptone_retain_domain` VALUES ('118', 'string', 'farming', '1');
INSERT INTO `ptone_retain_domain` VALUES ('119', 'string', 'farm', '1');
INSERT INTO `ptone_retain_domain` VALUES ('120', 'string', 'casino', '1');
INSERT INTO `ptone_retain_domain` VALUES ('121', 'string', 'casinos', '1');
INSERT INTO `ptone_retain_domain` VALUES ('122', 'string', 'gambling', '1');
INSERT INTO `ptone_retain_domain` VALUES ('123', 'string', 'insurance', '1');
INSERT INTO `ptone_retain_domain` VALUES ('124', 'string', 'publicrelations', '1');
INSERT INTO `ptone_retain_domain` VALUES ('125', 'string', 'pr', '1');
INSERT INTO `ptone_retain_domain` VALUES ('126', 'string', 'retails', '1');
INSERT INTO `ptone_retain_domain` VALUES ('127', 'string', 'retail', '1');
INSERT INTO `ptone_retain_domain` VALUES ('128', 'string', 'wholesales', '1');
INSERT INTO `ptone_retain_domain` VALUES ('129', 'string', 'wholesale', '1');
INSERT INTO `ptone_retain_domain` VALUES ('130', 'string', 'tobacco', '1');
INSERT INTO `ptone_retain_domain` VALUES ('131', 'string', 'venturecapital', '1');
INSERT INTO `ptone_retain_domain` VALUES ('132', 'string', 'vc', '1');
INSERT INTO `ptone_retain_domain` VALUES ('133', 'string', 'capital', '1');
INSERT INTO `ptone_retain_domain` VALUES ('134', 'string', 'facebook', '1');
INSERT INTO `ptone_retain_domain` VALUES ('135', 'string', 'google', '1');
INSERT INTO `ptone_retain_domain` VALUES ('136', 'string', 'paypal', '1');
INSERT INTO `ptone_retain_domain` VALUES ('137', 'string', 'data', '1');
INSERT INTO `ptone_retain_domain` VALUES ('138', 'regexp', 'c[a-z]o', '1');
INSERT INTO `ptone_retain_domain` VALUES ('139', 'string', 'datadeck', '1');

-- ----------------------------
-- Table structure for `ptone_short_url`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_short_url`;
CREATE TABLE `ptone_short_url` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `short_key` varchar(255) NOT NULL,
  `domain` varchar(255) DEFAULT NULL,
  `url` text,
  `create_time` datetime DEFAULT NULL,
  `modify_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '依赖mysql自动更新的修改时间',
  `is_delete` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `short_key` (`short_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_short_url
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_space_info`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_space_info`;
CREATE TABLE `ptone_space_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `space_id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `domain` varchar(255) NOT NULL,
  `logo` text,
  `description` text,
  `owner_id` varchar(255) NOT NULL,
  `owner_email` varchar(255) DEFAULT NULL,
  `creator_id` varchar(255) DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `modifier_id` varchar(255) DEFAULT NULL,
  `modify_time` bigint(20) DEFAULT NULL,
  `week_start` varchar(255) DEFAULT NULL,
  `is_delete` tinyint(4) NOT NULL,
  `space_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `owner_id_index` (`owner_id`),
  KEY `space_id_index` (`space_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_space_info
-- ----------------------------
INSERT INTO `ptone_space_info` VALUES ('1', '83a95adb-ffe9-4180-b638-998cb75f59ca', 'demo', 'demo', null, null, '1', 'admin@xp.com', '1', '1495334832308', '1', '1495334832308', 'monday', '0', null);

-- ----------------------------
-- Table structure for `ptone_space_user`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_space_user`;
CREATE TABLE `ptone_space_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `space_id` varchar(255) NOT NULL,
  `uid` varchar(255) DEFAULT NULL COMMENT '用户的uid，如果要去用户未注册，uid为null',
  `user_email` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL COMMENT 'owner || follower',
  `status` varchar(255) NOT NULL,
  `creator_id` varchar(255) DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `is_delete` tinyint(4) NOT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '依赖mysql自动更新的记录修改时间',
  PRIMARY KEY (`id`),
  KEY `spaceIdIndex` (`space_id`),
  KEY `uidIndex` (`uid`),
  KEY `creatorIdIndex` (`creator_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_space_user
-- ----------------------------
INSERT INTO `ptone_space_user` VALUES ('1', '83a95adb-ffe9-4180-b638-998cb75f59ca', '1', 'admin@xp.com', 'owner', 'accepted', '1', '1495334832308', '0', '2017-05-21 10:47:12');
INSERT INTO `ptone_space_user` VALUES ('2', '83a95adb-ffe9-4180-b638-998cb75f59ca', '2', 'guest@xp.com', 'follower', 'accepted', '1', '1495334832308', '0', '2017-05-21 10:57:36');
INSERT INTO `ptone_space_user` VALUES ('3', '83a95adb-ffe9-4180-b638-998cb75f59ca', '3', 'guest02@xp.com', 'follower', 'accepted', '1', '1495334832308', '0', '2017-05-21 10:47:12');
INSERT INTO `ptone_space_user` VALUES ('4', '83a95adb-ffe9-4180-b638-998cb75f59ca', '4', 'guest03@xp.com', 'follower', 'accepted', '1', '1495334832308', '0', '2017-05-21 10:47:12');
INSERT INTO `ptone_space_user` VALUES ('5', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'guest04@xp.com', 'follower', 'accepted', '1', '1495334832308', '0', '2017-05-21 10:47:12');
INSERT INTO `ptone_space_user` VALUES ('6', '83a95adb-ffe9-4180-b638-998cb75f59ca', '6', 'guest05@xp.com', 'follower', 'accepted', '1', '1495334832308', '0', '2017-05-21 10:47:12');

-- ----------------------------
-- Table structure for `ptone_sys_operation`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_sys_operation`;
CREATE TABLE `ptone_sys_operation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `operation_id` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `handler` varchar(255) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_sys_operation
-- ----------------------------
INSERT INTO `ptone_sys_operation` VALUES ('5', 'a5d62a47-24ea-49b6-8395-a53b015dda78', 'upload', 'upload', null, '1');
INSERT INTO `ptone_sys_operation` VALUES ('6', '9e6e0dc1-7e68-4992-ab98-4e9422123e4a', 'demo', 'demo', null, '1');
INSERT INTO `ptone_sys_operation` VALUES ('7', 'a46cbe00-adad-4e3e-8514-b4d65d5ece17', 'view', 'view', null, '1');
INSERT INTO `ptone_sys_operation` VALUES ('8', '80309c13-e6bf-4402-a5da-0988dbec5a4f', 'refresh', 'refresh', null, '1');
INSERT INTO `ptone_sys_operation` VALUES ('9', '5754c63f-261e-4973-8474-0c8b65117533', 'edit', 'edit', null, '1');
INSERT INTO `ptone_sys_operation` VALUES ('10', '164deba5-81b2-426e-93bc-b750d5c7dcc2', 'add', 'add', null, '1');

-- ----------------------------
-- Table structure for `ptone_sys_permission`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_sys_permission`;
CREATE TABLE `ptone_sys_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `permission_id` varchar(255) DEFAULT NULL,
  `resource_id` varchar(255) DEFAULT NULL,
  `operation_id` varchar(255) DEFAULT NULL,
  `space_id` varchar(255) NOT NULL DEFAULT 'ptone',
  `name` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '1',
  `url` longtext,
  `description` text,
  PRIMARY KEY (`id`),
  KEY `permission_id` (`permission_id`),
  KEY `resource_id` (`resource_id`),
  KEY `operation_id` (`operation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_sys_permission
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_sys_resource`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_sys_resource`;
CREATE TABLE `ptone_sys_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resource_id` varchar(255) DEFAULT NULL,
  `space_id` varchar(255) NOT NULL DEFAULT 'ptone',
  `name` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `link` varchar(255) DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `resource_id` (`resource_id`),
  UNIQUE KEY `codeIndex` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_sys_resource
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_sys_role`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_sys_role`;
CREATE TABLE `ptone_sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` varchar(255) DEFAULT NULL,
  `space_id` varchar(255) NOT NULL DEFAULT 'ptone',
  `name` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '1',
  `description` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_sys_role
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_sys_role_permission`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_sys_role_permission`;
CREATE TABLE `ptone_sys_role_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` varchar(255) DEFAULT NULL,
  `permission_id` varchar(255) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `role_id_index` (`role_id`),
  KEY `permission_id_index` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_sys_role_permission
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_sys_role_resource_quantity`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_sys_role_resource_quantity`;
CREATE TABLE `ptone_sys_role_resource_quantity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` varchar(255) DEFAULT NULL,
  `space_id` varchar(255) NOT NULL DEFAULT 'ptone',
  `resource_id` varchar(255) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_sys_role_resource_quantity
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_sys_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_sys_user_role`;
CREATE TABLE `ptone_sys_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) DEFAULT NULL,
  `space_id` varchar(255) NOT NULL DEFAULT 'ptone',
  `role_id` varchar(255) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_sys_user_role
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_tag_info`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_tag_info`;
CREATE TABLE `ptone_tag_info` (
  `Ptone_Tag_ID` int(11) NOT NULL AUTO_INCREMENT,
  `Ptone_Tag_Name` text NOT NULL,
  `Ptone_Tag_Type` int(11) DEFAULT NULL,
  `Parent_Tag_ID` int(11) DEFAULT NULL,
  `Status` char(4) NOT NULL DEFAULT '1' COMMENT '0:无效 1:有效 2：发布',
  `type` varchar(255) NOT NULL DEFAULT '0' COMMENT '标签类型 1：系统标签 0：panel标签 2:widget标签',
  PRIMARY KEY (`Ptone_Tag_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_tag_info
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_tag_panel_template`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_tag_panel_template`;
CREATE TABLE `ptone_tag_panel_template` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Ptone_Tag_ID` int(11) NOT NULL,
  `Template_Panel_ID` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `Ptone_Tag_ID` (`Ptone_Tag_ID`),
  KEY `Template_Widget_ID` (`Template_Panel_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of ptone_tag_panel_template
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_tag_type_info`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_tag_type_info`;
CREATE TABLE `ptone_tag_type_info` (
  `Ptone_Tag_Type_ID` int(11) NOT NULL AUTO_INCREMENT,
  `Ptone_Tag_Type_Name` varchar(1000) NOT NULL,
  PRIMARY KEY (`Ptone_Tag_Type_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of ptone_tag_type_info
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_tag_widget_template`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_tag_widget_template`;
CREATE TABLE `ptone_tag_widget_template` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Ptone_Tag_ID` int(11) NOT NULL,
  `Template_Widget_ID` varchar(255) NOT NULL,
  `is_delete` bigint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `Ptone_Tag_ID` (`Ptone_Tag_ID`),
  KEY `Template_Widget_ID` (`Template_Widget_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of ptone_tag_widget_template
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_user`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_user`;
CREATE TABLE `ptone_user` (
  `Pt_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `User_Name` varchar(255) DEFAULT NULL,
  `User_Email` varchar(255) NOT NULL,
  `User_Password` varchar(255) NOT NULL,
  `Api_Key` varchar(255) DEFAULT NULL,
  `Create_Date` datetime DEFAULT NULL,
  `Activite_Date` datetime DEFAULT NULL COMMENT '激活时间',
  `Delete_Date` datetime DEFAULT NULL COMMENT '删除时间',
  `Delete_Uid` bigint(20) DEFAULT NULL COMMENT '操作删除的用户ID',
  `Access` varchar(255) DEFAULT '0' COMMENT '0：普通用户权限 1：管理员权限（制作模板） 3：demo账户权限 8：超级管理员权限（管理菜单） A：ptengine权限 B：facebook ad 权限 U：Upload权限 P：预注册用户管理权限',
  `Login_Active` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否登录过系统',
  `Status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '0：无效用户 1：有效',
  `source` varchar(255) DEFAULT NULL COMMENT '用户注册来源',
  `is_pre_registration` int(11) NOT NULL DEFAULT '1' COMMENT '0:是预注册 1:不是预注',
  `login_count` bigint(20) NOT NULL DEFAULT '0',
  `facebook_count` bigint(20) NOT NULL DEFAULT '0',
  `twitter_count` bigint(20) NOT NULL DEFAULT '0',
  `total_count` bigint(20) NOT NULL DEFAULT '0' COMMENT '转发总次数',
  `utm_source` varchar(255) DEFAULT NULL,
  `utm_campaign` varchar(255) DEFAULT NULL,
  `utm_medium` varchar(255) DEFAULT NULL,
  `sales_manager` varchar(255) DEFAULT NULL,
  `account_manager` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `company` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `phone_country` varchar(255) DEFAULT NULL COMMENT '电话所属国家',
  `last_login_date` varchar(255) DEFAULT NULL COMMENT '最近登录时间',
  `is_from_space_invitation` tinyint(4) DEFAULT NULL COMMENT '是不是通过邀请注册的 0:不是 1：是',
  `total_password_changes` int(11) DEFAULT NULL COMMENT '修改密码的次数',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '依赖mysql自动更新的记录修改时间',
  `is_activited` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:未激活  1：已激活',
  PRIMARY KEY (`Pt_ID`),
  KEY `NewIndex1` (`User_Email`)
) ENGINE=InnoDB AUTO_INCREMENT=3240 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_user
-- ----------------------------
INSERT INTO `ptone_user` VALUES ('1', 'admin', 'admin@xp.com', '4297f44b13955235245b2497399d7a93', '123123', '2017-05-21 00:00:00', '2017-05-21 00:00:00', null, null, '0,8,U,A,B,P', '1', '1', 'dd-jp-basic', '1', '1431', '0', '0', '0', null, null, null, null, null, 'peng', null, null, null, null, null, '2017-05-21 10:47:01', null, null, '2017-05-21 10:50:28', '1');
INSERT INTO `ptone_user` VALUES ('2', 'guest', 'guest@xp.com', '4297f44b13955235245b2497399d7a93', '123123', '2017-05-21 00:00:00', '2017-05-21 00:00:00', null, null, '0', '1', '0', 'dd-jp-basic', '1', '0', '0', '0', '0', null, null, null, null, null, null, null, null, null, null, null, null, null, null, '2017-05-21 10:50:28', '1');
INSERT INTO `ptone_user` VALUES ('3', 'guest02', 'guest02@xp.com', '4297f44b13955235245b2497399d7a93', '123131', '2017-05-21 00:00:00', '2017-05-21 00:00:00', null, null, '0', '1', '0', 'dd-jp-basic', '1', '0', '0', '0', '0', null, null, null, null, null, null, null, null, null, null, null, null, null, null, '2017-05-21 10:50:28', '1');
INSERT INTO `ptone_user` VALUES ('4', 'guest03', 'guest03@xp.com', '4297f44b13955235245b2497399d7a93', '123131', '2017-05-21 00:00:00', '2017-05-21 00:00:00', null, null, '0', '0', '1', 'dd-jp-basic', '1', '0', '0', '0', '0', null, null, null, null, null, null, null, null, null, null, null, null, null, null, '2017-05-21 10:50:28', '1');
INSERT INTO `ptone_user` VALUES ('5', 'guest04', 'guest04@xp.com', '4297f44b13955235245b2497399d7a93', '123131', '2017-05-21 00:00:00', '2017-05-21 00:00:00', null, null, '0', '0', '0', 'dd-jp-basic', '1', '0', '0', '0', '0', null, null, null, null, null, null, null, null, null, null, null, null, null, null, '2017-05-21 10:50:28', '1');
INSERT INTO `ptone_user` VALUES ('6', 'guest05', 'guest05@xp.com', '4297f44b13955235245b2497399d7a93', '123131', '2017-05-21 00:00:00', '2017-05-21 00:00:00', null, null, '0', '0', '0', 'dd-jp-basic', '1', '0', '0', '0', '0', null, null, null, null, null, null, null, null, null, null, null, null, null, null, '2017-05-21 10:50:28', '1');

-- ----------------------------
-- Table structure for `ptone_user_basic_setting`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_user_basic_setting`;
CREATE TABLE `ptone_user_basic_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pt_id` int(11) NOT NULL,
  `week_start` varchar(255) DEFAULT NULL,
  `locale` varchar(255) DEFAULT NULL,
  `profile_selected` text,
  `user_selected` text,
  `show_tips` varchar(1024) DEFAULT '{"spaceSettingsEntry":0,"dataSourceManageEntry":0,"dashboardEdit":0,"dashboardAddEntry":0,"dashboardEditEntry":0,"dashboardShareEntry":0,"dashboardVideo":0}',
  `demo_switch` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:close 1:open ',
  `hide_onboarding` tinyint(4) DEFAULT NULL COMMENT '停用（不能删）',
  `view_onboarding` tinyint(4) NOT NULL DEFAULT '1' COMMENT '0:未创建 1：创建',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '依赖mysql自动更新的记录修改时间',
  `space_selected` varchar(255) DEFAULT NULL COMMENT '用户当前所选择的空间的domain',
  PRIMARY KEY (`id`),
  KEY `ptIdIndex` (`pt_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2435 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_user_basic_setting
-- ----------------------------
INSERT INTO `ptone_user_basic_setting` VALUES ('1', '1', 'monday', 'zh_CN', '{\"83a95adb-ffe9-4180-b638-998cb75f59ca\":{\"accountName\":\"127.0.0.1-sizzler\",\"connectionId\":\"674c3f31-2eb6-4e98-a535-4ed54cf473d9\",\"dsCode\":\"mysql\",\"dsId\":\"5\",\"prfileId\":\"912281b9-1bbe-4d2b-8b87-8763ee092432\"}}', '', '{\"spaceSettingsEntry\":1,\"dataSourceManageEntry\":1,\"dashboardEdit\":1,\"dashboardAddEntry\":1,\"dashboardEditEntry\":0,\"dashboardShareEntry\":1,\"dashboardVideo\":1}', '1', '1', '1', '2017-05-21 10:49:08', '');
INSERT INTO `ptone_user_basic_setting` VALUES ('2', '2', 'monday', 'zh_CN', '', null, '{\"spaceSettingsEntry\":1,\"dataSourceManageEntry\":1,\"dashboardEdit\":1,\"dashboardAddEntry\":1,\"dashboardEditEntry\":1,\"dashboardShareEntry\":1,\"dashboardVideo\":1}', '0', '1', '1', '2017-05-21 10:42:42', null);
INSERT INTO `ptone_user_basic_setting` VALUES ('3', '3', 'monday', 'zh_CN', '', null, '{\"spaceSettingsEntry\":1,\"dataSourceManageEntry\":1,\"dashboardEdit\":1,\"dashboardAddEntry\":1,\"dashboardEditEntry\":1,\"dashboardShareEntry\":1,\"dashboardVideo\":1}', '0', '1', '1', '2017-05-21 10:42:44', null);
INSERT INTO `ptone_user_basic_setting` VALUES ('4', '3', 'sunday', 'zh_CN', '', null, '{\"spaceSettingsEntry\":1,\"dataSourceManageEntry\":1,\"dashboardEdit\":1,\"dashboardAddEntry\":1,\"dashboardEditEntry\":1,\"dashboardShareEntry\":1,\"dashboardVideo\":1}', '0', '1', '1', '2017-05-21 10:42:45', null);
INSERT INTO `ptone_user_basic_setting` VALUES ('5', '4', 'sunday', 'en_US', '', '', '{\"spaceSettingsEntry\":1,\"dataSourceManageEntry\":1,\"dashboardEdit\":1,\"dashboardAddEntry\":1,\"dashboardEditEntry\":1,\"dashboardShareEntry\":1,\"dashboardVideo\":1}', '0', '1', '1', '2017-05-21 10:43:43', null);
INSERT INTO `ptone_user_basic_setting` VALUES ('6', '5', 'sunday', 'zh_CN', '', null, '{\"spaceSettingsEntry\":1,\"dataSourceManageEntry\":1,\"dashboardEdit\":1,\"dashboardAddEntry\":1,\"dashboardEditEntry\":1,\"dashboardShareEntry\":1,\"dashboardVideo\":1}', '0', '1', '1', '2017-05-21 10:42:50', null);

-- ----------------------------
-- Table structure for `ptone_variable_info`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_variable_info`;
CREATE TABLE `ptone_variable_info` (
  `Ptone_Variable_Info_ID` bigint(32) NOT NULL AUTO_INCREMENT,
  `Variable_ID` varchar(255) NOT NULL,
  `Variable_Name` varchar(255) DEFAULT NULL,
  `Variable_Color` text,
  `Variable_Graph_ID` int(20) DEFAULT NULL,
  `Ptone_Ds_Info_ID` int(20) DEFAULT NULL,
  `Graph_Name` varchar(255) DEFAULT NULL,
  `date_dimension_id` varchar(255) DEFAULT NULL COMMENT '时间维度列 col_id',
  `Widget_ID` varchar(255) DEFAULT NULL,
  `Panel_ID` varchar(255) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`Ptone_Variable_Info_ID`),
  UNIQUE KEY `NewIndex1` (`Variable_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_variable_info
-- ----------------------------
INSERT INTO `ptone_variable_info` VALUES ('1', '07779c02-c9fe-4ff2-843d-254b35840cd1', null, null, '800', '5', null, '6f5577d6-a4de-4cb7-9f4a-002657bc73ed', '6b26bb8d-1884-42e1-87f6-755db382ed2a', '0e8d0c34-1adf-40e1-b5c8-791698529179', '1');

-- ----------------------------
-- Table structure for `ptone_widget_chart_setting`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_widget_chart_setting`;
CREATE TABLE `ptone_widget_chart_setting` (
  `Widget_ID` varchar(255) NOT NULL,
  `stacked_chart` varchar(255) DEFAULT NULL COMMENT '是否堆图 ，0： 否，1：是',
  `area_chart` varchar(255) DEFAULT NULL COMMENT '是否面积图， 0：否，1：是',
  `show_legend` varchar(4) DEFAULT NULL COMMENT '是否显示图例',
  `show_data_labels` varchar(4) DEFAULT NULL COMMENT '是否显示数值标签',
  `x_Axis` text COMMENT 'x轴设置',
  `y_Axis` text COMMENT 'y轴设置',
  `show_multi_y` tinyint(4) DEFAULT NULL COMMENT '是否开启双轴， 0：否，1：是',
  `metrics_to_y` text COMMENT '指标对应y轴关系',
  `is_delete` tinyint(4) NOT NULL,
  `show_map_name` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:否，1 是',
  `hide_detail` varchar(255) DEFAULT '0' COMMENT '是否显示详细包括table、line、column',
  `hide_calculate_name` varchar(255) DEFAULT '0' COMMENT '隐藏SUM、AVG等函数显示',
  `reverse_target` varchar(255) DEFAULT '0' COMMENT '反向目标， 改变number类型chart数值上升、下降的指示颜色',
  PRIMARY KEY (`Widget_ID`),
  UNIQUE KEY `NewIndex1` (`Widget_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_widget_chart_setting
-- ----------------------------
INSERT INTO `ptone_widget_chart_setting` VALUES ('6b26bb8d-1884-42e1-87f6-755db382ed2a', '0', '0', '', '0', '[{\"enabled\":true}]', '[{\"enabled\":true},{\"enabled\":true}]', '0', '{}', '0', '0', '0', '0', '0');

-- ----------------------------
-- Table structure for `ptone_widget_info`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_widget_info`;
CREATE TABLE `ptone_widget_info` (
  `Ptone_Widget_Info_ID` bigint(32) NOT NULL AUTO_INCREMENT,
  `Widget_ID` varchar(255) NOT NULL,
  `Widget_Title` text,
  `Is_Title_Update` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0:未修改 1：修改过',
  `Date_Period` varchar(255) DEFAULT NULL,
  `Date_Key` varchar(255) DEFAULT NULL,
  `Creator_ID` varchar(255) NOT NULL,
  `Owner_ID` varchar(255) DEFAULT NULL,
  `IS_EXAMPLE` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否展示demo数据， 0：不展示，1：展示',
  `Modifier_ID` varchar(255) DEFAULT NULL,
  `Create_Time` bigint(20) DEFAULT NULL,
  `Modify_Time` bigint(20) DEFAULT NULL,
  `Status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '0:无效  1:有效',
  `Ptone_Graph_Info_ID` int(20) DEFAULT NULL,
  `Graph_Name` varchar(255) DEFAULT NULL,
  `map_code` varchar(255) DEFAULT NULL COMMENT '地图code，国家地图为国家名，世界地图为空',
  `Refresh_Interval` int(10) NOT NULL DEFAULT '-1',
  `Target_Value` varchar(255) DEFAULT NULL,
  `By_Template` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0：手动创建 1：从模板创建',
  `Is_Template` tinyint(2) DEFAULT '0' COMMENT '0：非模版 1：模版',
  `Templet_ID` varchar(255) DEFAULT NULL COMMENT '用于获取模板demo数据，用户修改widget后，此字段置空',
  `Description` text,
  `SIZE_X` int(4) NOT NULL DEFAULT '5',
  `SIZE_Y` int(4) NOT NULL DEFAULT '6',
  `Widget_Type` varchar(255) DEFAULT NULL COMMENT ' widget type : chart or tool',
  `show_time_period` varchar(4) DEFAULT NULL,
  `show_metric_amount` varchar(4) DEFAULT NULL,
  `is_demo` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0：不展示 1：展示',
  `Panel_ID` varchar(255) DEFAULT NULL,
  `space_id` varchar(255) DEFAULT NULL,
  `parent_id` varchar(255) DEFAULT NULL,
  `is_publish` tinyint(4) NOT NULL DEFAULT '0',
  `publish_area` text,
  `ds_code` varchar(255) DEFAULT NULL,
  `order_number` int(11) DEFAULT NULL COMMENT '排序字段',
  `templet_graph_name` varchar(255) DEFAULT NULL,
  `is_preview` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否预览widget 0不是 1是(onborading用)',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '依赖mysql自动更新的记录修改时间',
  `source_type` varchar(20) DEFAULT NULL COMMENT '来源类型：手动创建（USER_CREATED）、预制Panel模板（DEFAULT_TEMPLET）、Panel模板（PANEL_TEMPLET）、WidgetGallery（WIDGET_GALLERY）',
  `data_version` varchar(4) DEFAULT NULL COMMENT '当前数据版本号',
  `base_version` varchar(4) DEFAULT NULL COMMENT '基础数据版本号',
  `settings` longtext COMMENT 'widget样式，图形设置等数据',
  PRIMARY KEY (`Ptone_Widget_Info_ID`),
  UNIQUE KEY `NewIndex1` (`Widget_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_widget_info
-- ----------------------------
INSERT INTO `ptone_widget_info` VALUES ('1', '6b26bb8d-1884-42e1-87f6-755db382ed2a', 'by Pt_ID 并且 User_Name 并且 User_Email ', '0', 'day', 'last7day', '1', '1', '0', '1', '1495334944657', '1495334944657', '1', '800', 'table', 'China', '0', null, '0', '0', null, null, '5', '6', 'chart', '0', '1', '0', '0e8d0c34-1adf-40e1-b5c8-791698529179', '83a95adb-ffe9-4180-b638-998cb75f59ca', null, '0', null, null, null, null, '0', '2017-05-21 10:49:04', 'USER_CREATED', null, null, null);

-- ----------------------------
-- Table structure for `ptone_widget_info_extend`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_widget_info_extend`;
CREATE TABLE `ptone_widget_info_extend` (
  `Widget_ID` varchar(255) NOT NULL,
  `value` text COMMENT '用于获取模板demo数据，用户修改widget后，此字段置空',
  `extend` text,
  `layout` text,
  `is_delete` int(11) NOT NULL,
  PRIMARY KEY (`Widget_ID`),
  UNIQUE KEY `NewIndex1` (`Widget_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_widget_info_extend
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_widget_interval`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_widget_interval`;
CREATE TABLE `ptone_widget_interval` (
  `Ptone_Widget_Interval_ID` int(10) NOT NULL AUTO_INCREMENT,
  `Interval_Name` varchar(255) DEFAULT NULL,
  `Refresh_Interval` int(10) DEFAULT NULL,
  PRIMARY KEY (`Ptone_Widget_Interval_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_widget_interval
-- ----------------------------
INSERT INTO `ptone_widget_interval` VALUES ('1', 'Never refresh', '-1');
INSERT INTO `ptone_widget_interval` VALUES ('2', '1 Min', '1');
INSERT INTO `ptone_widget_interval` VALUES ('3', '5 Min', '5');
INSERT INTO `ptone_widget_interval` VALUES ('4', '10 Min', '10');
INSERT INTO `ptone_widget_interval` VALUES ('5', '30 Min', '30');
INSERT INTO `ptone_widget_interval` VALUES ('6', '45 Min', '45');
INSERT INTO `ptone_widget_interval` VALUES ('7', '1 Hour', '60');

-- ----------------------------
-- Table structure for `ptone_widget_templet`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_widget_templet`;
CREATE TABLE `ptone_widget_templet` (
  `Ptone_Widget_Info_ID` bigint(32) NOT NULL AUTO_INCREMENT,
  `Widget_ID` varchar(255) NOT NULL,
  `Widget_Title` text,
  `Date_Period` varchar(255) DEFAULT NULL,
  `Date_Key` varchar(255) DEFAULT NULL,
  `Creator_ID` varchar(255) NOT NULL,
  `Modifier_ID` varchar(255) DEFAULT NULL,
  `Create_Time` bigint(20) DEFAULT NULL,
  `Modify_Time` bigint(20) DEFAULT NULL,
  `Ptone_Graph_Info_ID` int(20) DEFAULT NULL,
  `Graph_Name` varchar(255) DEFAULT NULL,
  `Status` varchar(32) NOT NULL DEFAULT '1' COMMENT '0:无效 1:有效',
  `Refresh_Interval` int(10) NOT NULL DEFAULT '-1',
  `Target_Value` varchar(255) DEFAULT NULL,
  `Description` text,
  `Metrics_Json` text,
  `Dimensions_Json` text,
  `segments_json` text,
  `filters_json` text,
  `Templet_ID` varchar(255) DEFAULT NULL COMMENT '模板id用于获取demo数据，值同widget_id',
  `Is_Template` tinyint(2) NOT NULL DEFAULT '1',
  `Owner_ID` varchar(255) DEFAULT NULL,
  `IS_EXAMPLE` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否展示demo数据， 0：不展示，1：展示',
  `SIZE_X` int(4) NOT NULL DEFAULT '5',
  `SIZE_Y` int(4) NOT NULL DEFAULT '6',
  `Widget_Type` varchar(255) DEFAULT 'chart',
  `show_time_period` varchar(4) DEFAULT NULL,
  `Is_Title_Update` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0:未修改 1：已修改',
  `map_code` varchar(255) DEFAULT NULL COMMENT '地图code，国家地图为国家名，世界地图为空',
  `show_metric_amount` varchar(4) DEFAULT NULL,
  `is_demo` tinyint(2) NOT NULL DEFAULT '0',
  `Panel_ID` varchar(255) DEFAULT NULL,
  `space_id` varchar(255) DEFAULT NULL,
  `parent_id` varchar(255) DEFAULT NULL,
  `is_publish` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:未发布 1：发布',
  `publish_area` text,
  `ds_code` varchar(255) DEFAULT NULL,
  `order_number` int(11) NOT NULL DEFAULT '0' COMMENT '排序字段',
  `templet_graph_name` varchar(255) DEFAULT NULL,
  `is_preview` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否预览widget 0不是 1是',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '依赖mysql自动更新的记录修改时间',
  PRIMARY KEY (`Ptone_Widget_Info_ID`),
  UNIQUE KEY `NewIndex1` (`Widget_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_widget_templet
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_widget_templet_data`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_widget_templet_data`;
CREATE TABLE `ptone_widget_templet_data` (
  `templet_id` varchar(255) NOT NULL,
  `data` longtext,
  PRIMARY KEY (`templet_id`),
  UNIQUE KEY `NewIndex1` (`templet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_widget_templet_data
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_widget_templet_stat`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_widget_templet_stat`;
CREATE TABLE `ptone_widget_templet_stat` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT,
  `widget_id` varchar(255) NOT NULL,
  `widget_title` text,
  `ds_code` varchar(255) DEFAULT NULL,
  `Widget_Type` varchar(255) DEFAULT 'chart',
  `parent_id` varchar(255) DEFAULT NULL,
  `graph_name` varchar(255) DEFAULT NULL,
  `metrics_code` text,
  `metrics_name` text,
  `dimensions_code` text,
  `dimensions_name` text,
  `segments` text,
  `filters` text,
  `is_publish` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:未发布 1：发布',
  `publish_area` text,
  `tag_id` text,
  `tag_name` text,
  `last_add_date` varchar(255) DEFAULT NULL,
  `add_count` int(11) DEFAULT '0',
  `connection_count` int(11) DEFAULT '0',
  `user_delete_count` int(11) DEFAULT '0',
  `create_time` varchar(255) DEFAULT NULL,
  `modify_time` varchar(255) DEFAULT NULL,
  `order_number` int(11) NOT NULL DEFAULT '0' COMMENT '排序字段',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '0:无效 1:有效',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '依赖mysql自动更新的记录修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `NewIndex1` (`widget_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_widget_templet_stat
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_widget_templet_stat_tmp`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_widget_templet_stat_tmp`;
CREATE TABLE `ptone_widget_templet_stat_tmp` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT,
  `widget_id` varchar(255) NOT NULL,
  `add_count` int(11) DEFAULT '0',
  `connection_count` int(11) DEFAULT '0',
  `user_delete_count` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `NewIndex1` (`widget_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_widget_templet_stat_tmp
-- ----------------------------

-- ----------------------------
-- Table structure for `ptone_widget_variable`
-- ----------------------------
DROP TABLE IF EXISTS `ptone_widget_variable`;
CREATE TABLE `ptone_widget_variable` (
  `Ptone_Widget_Variable_ID` bigint(32) NOT NULL AUTO_INCREMENT,
  `Widget_ID` varchar(255) NOT NULL,
  `Variable_ID` varchar(255) NOT NULL,
  `is_delete` bigint(4) NOT NULL DEFAULT '0' COMMENT '0-可用;1-删除',
  PRIMARY KEY (`Ptone_Widget_Variable_ID`),
  KEY `Widget_ID_Index` (`Widget_ID`),
  KEY `Variable_ID_Index` (`Variable_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ptone_widget_variable
-- ----------------------------
INSERT INTO `ptone_widget_variable` VALUES ('1', '6b26bb8d-1884-42e1-87f6-755db382ed2a', '07779c02-c9fe-4ff2-843d-254b35840cd1', '0');

-- ----------------------------
-- Table structure for `sys_config_param`
-- ----------------------------
DROP TABLE IF EXISTS `sys_config_param`;
CREATE TABLE `sys_config_param` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `default_value` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1505 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_config_param
-- ----------------------------
INSERT INTO `sys_config_param` VALUES ('1', 'ProfileData Use Redis Cache ( Default )', 'PROFILE_DATA_USE_REDIS_CACHE_DEFAULT', 'true', 'true', null, '1');
INSERT INTO `sys_config_param` VALUES ('2', 'ProfileData Redis Cache Time(s) ( Default )', 'PROFILE_DATA_REDIS_CACHE_TIME_DEFAULT', '86400', '86400', 'a day', '1');
INSERT INTO `sys_config_param` VALUES ('3', 'ProfileData Use Redis Cache ( Google Analytics )', 'PROFILE_DATA_USE_REDIS_CACHE_googleanalysis', 'true', 'true', null, '1');
INSERT INTO `sys_config_param` VALUES ('4', 'ProfileData Use Redis Cache ( Google Adwords )', 'PROFILE_DATA_USE_REDIS_CACHE_googleadwords', 'false', 'true', null, '1');
INSERT INTO `sys_config_param` VALUES ('7', 'ProfileData Use Redis Cache ( Doubleclick )', 'PROFILE_DATA_USE_REDIS_CACHE_doubleclick', 'false', 'false', null, '1');
INSERT INTO `sys_config_param` VALUES ('8', 'ProfileData Use Redis Cache ( DoubleclickCompound)', 'PROFILE_DATA_USE_REDIS_CACHE_doubleclickCompound', 'false', 'false', null, '1');
INSERT INTO `sys_config_param` VALUES ('101', 'Datasource History Data Use Redis Cache ( Default )', 'DS_HISTORY_DATA_USE_REDIS_CACHE_DEFAULT', 'true', 'true', null, '1');
INSERT INTO `sys_config_param` VALUES ('102', 'Datasource History Data Redis Cache Time(s) ( Default )', 'DS_HISTORY_DATA_REDIS_CACHE_TIME_DEFAULT', '604800', '604800', 'a week', '1');
INSERT INTO `sys_config_param` VALUES ('103', 'Datasource Realtime Data Use Redis Cache ( Default )', 'DS_REALTIME_DATA_USE_REDIS_CACHE_DEFAULT', 'true', 'true', null, '1');
INSERT INTO `sys_config_param` VALUES ('104', 'Datasource Realtime Data Redis Cache Time(s) ( Default )', 'DS_REALTIME_DATA_REDIS_CACHE_TIME_DEFAULT', '3600', '3600', 'an hour', '1');
INSERT INTO `sys_config_param` VALUES ('105', 'ProfileData Use Redis Cache ( Salesforce )', 'PROFILE_DATA_USE_REDIS_CACHE_salesforce', 'true', 'true', null, '1');
INSERT INTO `sys_config_param` VALUES ('106', 'Data Dimension Metrics Cache ( Salesforce )', 'DATA_DIMENSION_METRICS_CACHE_salesforce', 'true', 'true', null, '1');
INSERT INTO `sys_config_param` VALUES ('201', 'Widget History Data Use Redis Cache ( Default )', 'WIDGET_HISTORY_DATA_USE_REDIS_CACHE_DEFAULT', 'true', 'true', null, '1');
INSERT INTO `sys_config_param` VALUES ('202', 'Widget History Data Redis Cache Time(s) ( Default )', 'WIDGET_HISTORY_DATA_REDIS_CACHE_TIME_DEFAULT', '86400', '86400', 'a day', '1');
INSERT INTO `sys_config_param` VALUES ('203', 'Widget Realtime Data Use Redis Cache ( Default )', 'WIDGET_REALTIME_DATA_USE_REDIS_CACHE_DEFAULT', 'true', 'true', null, '1');
INSERT INTO `sys_config_param` VALUES ('204', 'Widget Realtime Data Redis Cache Time(s) ( Default )', 'WIDGET_REALTIME_DATA_REDIS_CACHE_TIME_DEFAULT', '3600', '3600', 'an hour', '1');
INSERT INTO `sys_config_param` VALUES ('1000', 'Montior Service AccessToken Key', 'MONTIOR_SERVICE_ACCESSTOKEN_KEY', 'true', 'true', null, '1');
INSERT INTO `sys_config_param` VALUES ('1002', 'ProfileData Use Redis Cache ( Facebook Ads )', 'PROFILE_DATA_USE_REDIS_CACHE_facebookad', 'false', 'false', null, '1');
INSERT INTO `sys_config_param` VALUES ('1500', 'Widget Realtime Data Use Redis Cache ( GoogleAdsense )', 'WIDGET_REALTIME_DATA_USE_REDIS_CACHE_googleadsense', 'true', 'true', null, '1');
INSERT INTO `sys_config_param` VALUES ('1501', 'Datasource Realtime Data Use Redis Cache ( GoogleAdsense )', 'DS_REALTIME_DATA_USE_REDIS_CACHE_googleadsense', 'true', 'true', null, '1');
INSERT INTO `sys_config_param` VALUES ('1502', 'Datasource History Data Use Redis Cache ( GoogleAdsense )', 'DS_HISTORY_DATA_USE_REDIS_CACHE_googleadsense', 'true', 'true', null, '1');
INSERT INTO `sys_config_param` VALUES ('1503', 'Widget History Data Use Redis Cache ( GoogleAdsense )', 'WIDGET_HISTORY_DATA_USE_REDIS_CACHE_googleadsense', 'true', 'true', null, '1');
INSERT INTO `sys_config_param` VALUES ('1504', 'Widget Legend sort type', 'WIDGET_LEGEND_SORT_TYPE', 'valueDesc', 'valueDesc', '图例的排序顺序： 优先按照指标排序，然后按照设置规则排序，如果不设置默认按照valueDesc排序，设置为其他值则不排序,valueDesc(值总量倒序)||stringAsc(字符串升序)||default(不排序)', null);

-- ----------------------------
-- Table structure for `sys_data_version`
-- ----------------------------
DROP TABLE IF EXISTS `sys_data_version`;
CREATE TABLE `sys_data_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `version` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_data_version
-- ----------------------------
INSERT INTO `sys_data_version` VALUES ('1', 'GA Metrics List', 'googleanalysis-metrics', '20161215');
INSERT INTO `sys_data_version` VALUES ('2', 'GA Dimensions List', 'googleanalysis-dimensions', '20170328');
INSERT INTO `sys_data_version` VALUES ('3', 'GA Filters List', 'googleanalysis-filters', '20161215');
INSERT INTO `sys_data_version` VALUES ('4', 'GA Segments List', 'googleanalysis-segment', '20161215');
INSERT INTO `sys_data_version` VALUES ('11', 'GoogleAdwords Metrics List', 'googleadwords-metrics', '20170203');
INSERT INTO `sys_data_version` VALUES ('12', 'GoogleAdwords Dimensions List', 'googleadwords-dimensions', '20170203');
INSERT INTO `sys_data_version` VALUES ('13', 'GoogleAdwords Filters List', 'googleadwords-filters', '20170203');
INSERT INTO `sys_data_version` VALUES ('14', 'Ptengine Metrics List', 'ptengine-metrics', '20160918');
INSERT INTO `sys_data_version` VALUES ('15', 'Ptengine Dimensions List', 'ptengine-dimensions', '20160918');
INSERT INTO `sys_data_version` VALUES ('16', 'Ptengine Filters List', 'ptengine-filters', '20160918');
INSERT INTO `sys_data_version` VALUES ('17', 'Ptengine Segments List', 'ptengine-segment', '20160918');
INSERT INTO `sys_data_version` VALUES ('18', 'Facebook Ads Metrics List', 'facebookad-metrics', '20170505');
INSERT INTO `sys_data_version` VALUES ('19', 'Facebook Ads Dimensions List', 'facebookad-dimensions', '20170505');
INSERT INTO `sys_data_version` VALUES ('20', 'Facebook Ads Filters List', 'facebookad-filters', '20170505');
INSERT INTO `sys_data_version` VALUES ('21', 'Salesforce Metrics List', 'salesforce-metrics', '2016091406');
INSERT INTO `sys_data_version` VALUES ('22', 'Salesforce Dimensions List', 'salesforce-dimensions', '20160811-1');
INSERT INTO `sys_data_version` VALUES ('23', 'Salesforce Filters List', 'salesforce-filters', '20160811-1');
INSERT INTO `sys_data_version` VALUES ('24', 'Doubleclick Metrics List', 'doubleclick-metrics', '2016091406');
INSERT INTO `sys_data_version` VALUES ('25', 'Doubleclick Dimensions List', 'doubleclick-dimensions', '20160707');
INSERT INTO `sys_data_version` VALUES ('26', 'Doubleclick Filters List', 'doubleclick-filters', '20160707');
INSERT INTO `sys_data_version` VALUES ('27', 'DoubleclickCompound Metrics List', 'doubleclickCompound-metrics', '2016091406');
INSERT INTO `sys_data_version` VALUES ('28', 'DoubleclickCompound Dimensions List', 'doubleclickCompound-dimensions', '20160707');
INSERT INTO `sys_data_version` VALUES ('29', 'DoubleclickCompound Filter List', 'doubleclickCompound-filters', '20160707');
INSERT INTO `sys_data_version` VALUES ('30', 'Paypal Metrics List', 'paypal-metrics', '2016091406');
INSERT INTO `sys_data_version` VALUES ('31', 'Paypal Dimensions List', 'paypal-dimensions', '20160707');
INSERT INTO `sys_data_version` VALUES ('32', 'Paypal Filters List', 'paypal-filters', '20160707');
INSERT INTO `sys_data_version` VALUES ('33', 'Stripe Metrics List', 'stripe-metrics', '2016091406');
INSERT INTO `sys_data_version` VALUES ('34', 'Stripe Dimensions List', 'stripe-dimensions', '2016-08-30-1');
INSERT INTO `sys_data_version` VALUES ('35', 'Stripe Filter List', 'stripe-filters', '2016-08-30-1');
INSERT INTO `sys_data_version` VALUES ('36', 'MailChimp Metrics List', 'mailchimp-metrics', '20170504');
INSERT INTO `sys_data_version` VALUES ('37', 'MailChimp Dimensions List', 'mailchimp-dimensions', '20170504');
INSERT INTO `sys_data_version` VALUES ('38', 'MailChimp Filter List', 'mailchimp-filters', '20170504');
INSERT INTO `sys_data_version` VALUES ('39', 'GoogleAdsense Metrics List', 'googleadsense-metrics', '20170309');
INSERT INTO `sys_data_version` VALUES ('40', 'GoogleAdsense Dimensions List', 'googleadsense-dimensions', '20170309');
INSERT INTO `sys_data_version` VALUES ('41', 'GoogleAdsense Filter List', 'googleadsense-filters', '20170309');
INSERT INTO `sys_data_version` VALUES ('42', 'FacebookPages Metrics List', 'facebookPages-metrics', '20161009');
INSERT INTO `sys_data_version` VALUES ('43', 'FacebookPages Dimensions List', 'facebookPages-dimensions', '20161009');
INSERT INTO `sys_data_version` VALUES ('44', 'FacebookPages Filter List', 'facebookPages-filters', '20161009');
INSERT INTO `sys_data_version` VALUES ('45', 'YahooYDN Metrics List', 'yahooAdsYDN-metrics', '20161019');
INSERT INTO `sys_data_version` VALUES ('46', 'YahooYDN Dimensions List', 'yahooAdsYDN-dimensions', '20161019');
INSERT INTO `sys_data_version` VALUES ('47', 'YahooYDN Filter List', 'yahooAdsYDN-filters', '20161019');
INSERT INTO `sys_data_version` VALUES ('49', 'Ptapp Metrics List', 'ptapp-metrics', '2016101203');
INSERT INTO `sys_data_version` VALUES ('50', 'Ptapp Dimensions List', 'ptapp-dimensions', '2016101203');
INSERT INTO `sys_data_version` VALUES ('51', 'Ptapp Filters List', 'ptapp-filters', '2016101203');
INSERT INTO `sys_data_version` VALUES ('52', 'Ptapp Segments List', 'ptapp-segment', '2016101203');
INSERT INTO `sys_data_version` VALUES ('53', 'YahooSS Metrics List', 'yahooAdsSS-metrics', '201703241');
INSERT INTO `sys_data_version` VALUES ('54', 'YahooSS Dimensions List', 'yahooAdsSS-dimensions', '201703241');
INSERT INTO `sys_data_version` VALUES ('55', 'YahooSS Filter List', 'yahooAdsSS-filters', '201703241');

-- ----------------------------
-- Table structure for `sys_ds_client_query_log`
-- ----------------------------
DROP TABLE IF EXISTS `sys_ds_client_query_log`;
CREATE TABLE `sys_ds_client_query_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) DEFAULT NULL,
  `ds_id` varchar(255) DEFAULT NULL,
  `ds_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `query` bigint(20) DEFAULT '0',
  `remote_query` bigint(20) DEFAULT '0',
  `cache_hit` bigint(20) DEFAULT '0',
  `query_error` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_ds_client_query_log
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_ga_profile_query_log`
-- ----------------------------
DROP TABLE IF EXISTS `sys_ga_profile_query_log`;
CREATE TABLE `sys_ga_profile_query_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `profile_id` varchar(255) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `query` bigint(20) DEFAULT NULL,
  `remote_query` bigint(20) DEFAULT '0',
  `cache_hit` bigint(20) DEFAULT '0',
  `query_error` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_ga_profile_query_log
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_meta_log`
-- ----------------------------
DROP TABLE IF EXISTS `sys_meta_log`;
CREATE TABLE `sys_meta_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` varchar(1024) DEFAULT NULL,
  `time` bigint(20) DEFAULT NULL,
  `position` varchar(1024) DEFAULT NULL,
  `operate` varchar(1024) DEFAULT NULL,
  `operate_Id` varchar(1024) DEFAULT NULL,
  `content` text,
  `server_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_meta_log
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_panel_info_log`
-- ----------------------------
DROP TABLE IF EXISTS `sys_panel_info_log`;
CREATE TABLE `sys_panel_info_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `panel_id` varchar(1024) DEFAULT NULL,
  `panel_name` text,
  `create_time` bigint(20) DEFAULT NULL,
  `view_count` bigint(20) NOT NULL DEFAULT '0',
  `latest_view_time` bigint(20) NOT NULL DEFAULT '0',
  `current_widget_count` bigint(20) NOT NULL DEFAULT '0',
  `ga_data_request_count` bigint(20) NOT NULL DEFAULT '0',
  `valid` tinyint(4) DEFAULT NULL COMMENT '1: 有效 0：无效',
  `is_share` tinyint(4) NOT NULL DEFAULT '0' COMMENT '1: 有 0：无',
  `share_pv` bigint(20) DEFAULT NULL,
  `source_id` varchar(1024) DEFAULT NULL,
  `operate` varchar(1024) DEFAULT NULL,
  `server_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_panel_info_log
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_user_info_log`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_info_log`;
CREATE TABLE `sys_user_info_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` varchar(1024) DEFAULT NULL,
  `signup_time` bigint(20) DEFAULT NULL,
  `user_email` varchar(1024) DEFAULT NULL,
  `user_name` varchar(1024) DEFAULT NULL,
  `local` varchar(256) DEFAULT NULL,
  `week_start` varchar(256) DEFAULT NULL,
  `Latest_signin_time` bigint(20) DEFAULT NULL,
  `signin_count` bigint(20) NOT NULL DEFAULT '0',
  `current_panel_count` bigint(20) NOT NULL DEFAULT '0',
  `total_panel_count` bigint(20) NOT NULL DEFAULT '0',
  `current_widget_count` bigint(20) NOT NULL DEFAULT '0',
  `total_widget_count` bigint(20) NOT NULL DEFAULT '0',
  `share_panel_count` bigint(20) NOT NULL DEFAULT '0',
  `ga_data_request_count` bigint(20) NOT NULL DEFAULT '0',
  `server_time` bigint(20) DEFAULT NULL,
  `excel_upload_count` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_user_info_log
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_user_info_log_new`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_info_log_new`;
CREATE TABLE `sys_user_info_log_new` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `user_email` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `create_date` varchar(255) DEFAULT NULL,
  `activate_date` varchar(255) DEFAULT NULL,
  `last_login_date` varchar(255) DEFAULT NULL,
  `is_pre_registration` int(11) DEFAULT NULL,
  `is_from_space_invitation` int(11) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `utm_source` varchar(255) DEFAULT NULL,
  `utm_campaign` varchar(255) DEFAULT NULL,
  `utm_medium` varchar(255) DEFAULT NULL,
  `sales_manager` varchar(255) DEFAULT NULL,
  `account_manager` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `total_password_changes` int(11) DEFAULT NULL,
  `current_space_count` int(11) DEFAULT NULL,
  `total_space_count` int(11) DEFAULT NULL,
  `current_my_space_count` int(11) DEFAULT NULL,
  `total_my_space_count` int(11) DEFAULT NULL,
  `total_space_dashboard_count` int(11) DEFAULT NULL,
  `current_space_dashboard_count` int(11) DEFAULT NULL,
  `total_my_dashboard_count` int(11) DEFAULT NULL,
  `current_my_dashboard_count` int(11) DEFAULT NULL,
  `total_space_widget_count` int(11) DEFAULT NULL,
  `current_space_widget_count` int(11) DEFAULT NULL,
  `total_my_widget_count` int(11) DEFAULT NULL,
  `current_my_widget_count` int(11) DEFAULT NULL,
  `total_space_datasource_count` int(11) DEFAULT NULL,
  `current_space_datasource_count` int(11) DEFAULT NULL,
  `total_my_datasource_count` int(11) DEFAULT NULL,
  `current_my_datasource_count` int(11) DEFAULT NULL,
  `total_space_ds_table_and_account_count` int(11) DEFAULT NULL,
  `current_space_ds_table_and_account_count` int(11) DEFAULT NULL,
  `total_my_ds_table_and_account_count` int(11) DEFAULT NULL,
  `current_my_ds_table_and_account_count` int(11) DEFAULT NULL,
  `login_count` int(11) DEFAULT NULL,
  `current_space_collaborators` int(11) DEFAULT NULL,
  `total_my_collaborators_invitation_accepted` int(11) DEFAULT NULL,
  `total_my_collaborators_invitation_inviting` int(11) DEFAULT NULL,
  `total_my_dashboard_share_count` int(11) DEFAULT NULL,
  `total_my_dashboard_shared_pv` int(11) DEFAULT NULL,
  `total_dashboard_added_from_template` int(11) DEFAULT NULL,
  `current_dashboard_added_from_template` int(11) DEFAULT NULL,
  `request_days_count` int(11) DEFAULT NULL,
  `last_request_date` varchar(255) DEFAULT NULL,
  `Status` tinyint(2) NOT NULL DEFAULT '1',
  `is_activited` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_user_info_log_new
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_user_login_log`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_login_log`;
CREATE TABLE `sys_user_login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` varchar(1024) DEFAULT NULL,
  `time` varchar(20) DEFAULT NULL,
  `user_email` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_user_login_log
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_user_request_log`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_request_log`;
CREATE TABLE `sys_user_request_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) DEFAULT NULL,
  `user_email` varchar(255) DEFAULT NULL,
  `request_date` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `request_index` (`user_id`,`request_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_user_request_log
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_widget_info_log`
-- ----------------------------
DROP TABLE IF EXISTS `sys_widget_info_log`;
CREATE TABLE `sys_widget_info_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `widget_id` varchar(1024) DEFAULT NULL,
  `widget_name` text,
  `source_panel_id` varchar(1024) DEFAULT NULL,
  `source_panel_view_count` bigint(20) DEFAULT NULL,
  `graph_type` varchar(256) DEFAULT NULL,
  `time_setting` varchar(256) DEFAULT NULL,
  `width` int(10) DEFAULT NULL,
  `high` int(10) DEFAULT NULL,
  `metrics_count` int(10) DEFAULT NULL,
  `dimensions_count` int(10) DEFAULT NULL,
  `is_filter` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:无 1：有',
  `is_multi_y` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:无 1：有',
  `is_custom_max_min_y` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:无 1：有',
  `is_total_metrics` tinyint(4) NOT NULL DEFAULT '1' COMMENT '0:无 1：有',
  `is_show_x` tinyint(4) NOT NULL DEFAULT '1' COMMENT '0:无 1：有',
  `is_show_y` tinyint(4) NOT NULL DEFAULT '1' COMMENT '0:无 1：有',
  `is_show_time` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:无 1：有',
  `is_show_legend` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:无 1：有',
  `server_time` bigint(20) DEFAULT NULL,
  `valid` tinyint(4) DEFAULT '1' COMMENT '0:无效 1：有效',
  `ga_data_request_count` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_widget_info_log
-- ----------------------------

-- ----------------------------
-- Table structure for `user_calculate_tmp`
-- ----------------------------
DROP TABLE IF EXISTS `user_calculate_tmp`;
CREATE TABLE `user_calculate_tmp` (
  `uid` bigint(20) NOT NULL,
  `User_Email` varchar(255) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `Create_Date` varchar(255) DEFAULT NULL,
  `Activite_Date` varchar(255) DEFAULT NULL,
  `is_pre_registration` varchar(255) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `utm_source` varchar(255) DEFAULT NULL,
  `utm_campaign` varchar(255) DEFAULT NULL,
  `utm_medium` varchar(255) DEFAULT NULL,
  `sales_manager` varchar(255) DEFAULT NULL,
  `account_manager` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `login_count` int(11) DEFAULT '0',
  `total_my_dashboard_count` int(11) DEFAULT '0',
  `current_my_dashboard_count` int(11) DEFAULT '0',
  `total_my_dashboard_share_count` int(11) DEFAULT '0',
  `total_my_widget_count` int(11) DEFAULT NULL,
  `current_my_widget_count` int(11) DEFAULT NULL,
  `total_my_datasource_count` int(11) DEFAULT NULL,
  `current_my_datasource_count` int(11) DEFAULT NULL,
  `total_my_ds_table_and_account_count` int(11) DEFAULT NULL,
  `current_my_ds_table_and_account_count` int(11) DEFAULT NULL,
  `total_space_datasource_count` int(11) DEFAULT NULL,
  `current_space_datasource_count` int(11) DEFAULT NULL,
  `total_my_collaborators_invitation_accepted` int(11) DEFAULT NULL,
  `total_my_collaborators_invitation_inviting` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_calculate_tmp
-- ----------------------------

-- ----------------------------
-- Table structure for `user_compound_metrics_dimension`
-- ----------------------------
DROP TABLE IF EXISTS `user_compound_metrics_dimension`;
CREATE TABLE `user_compound_metrics_dimension` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `formula` text NOT NULL,
  `aggregator` text,
  `original_aggregator` text,
  `objects_data` text,
  `query_code` text COMMENT 'dimensionCode || metricsCode (过滤器查询用code)',
  `data_type` varchar(255) DEFAULT NULL,
  `data_format` varchar(255) DEFAULT NULL,
  `unit` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL COMMENT 'compoundMetrics || compoundDimension',
  `description` varchar(255) DEFAULT '',
  `allow_segment` int(11) DEFAULT NULL,
  `allow_filter` int(11) DEFAULT NULL,
  `space_id` varchar(255) DEFAULT NULL,
  `ds_id` varchar(255) DEFAULT NULL,
  `ds_code` varchar(255) DEFAULT NULL,
  `table_id` varchar(255) DEFAULT NULL,
  `uid` varchar(255) DEFAULT NULL,
  `user_email` varchar(255) DEFAULT NULL,
  `source_type` varchar(255) DEFAULT NULL COMMENT ' 用户创建user || 自动生成ptone',
  `templet_id` varchar(255) DEFAULT NULL COMMENT '来源自模板知道生成的计算指标的原计算指标id',
  `objects_id_list` text,
  `ds_id_list` varchar(255) DEFAULT NULL,
  `ds_code_list` varchar(255) DEFAULT NULL,
  `connection_id_list` text,
  `source_id_list` text,
  `table_id_list` text,
  `is_validate` tinyint(1) DEFAULT NULL,
  `creator_id` varchar(255) DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  `modifier_id` varchar(255) DEFAULT NULL,
  `modify_time` varchar(255) DEFAULT NULL,
  `order_number` int(11) DEFAULT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `is_contains_func` tinyint(1) DEFAULT NULL COMMENT '是否包含函数',
  `last_use_time` varchar(255) DEFAULT NULL COMMENT '最后使用时间（widget添加指标时统计）',
  `metrics_count` tinyint(4) DEFAULT NULL COMMENT '使用原始指标数量',
  `count_of_use` int(11) DEFAULT NULL COMMENT '使用数量，每天统计一次widget中使用次数',
  PRIMARY KEY (`id`),
  KEY `SPACE_ID` (`space_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_compound_metrics_dimension
-- ----------------------------

-- ----------------------------
-- Table structure for `user_connection`
-- ----------------------------
DROP TABLE IF EXISTS `user_connection`;
CREATE TABLE `user_connection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `connection_id` varchar(255) DEFAULT NULL COMMENT '唯一ID',
  `name` varchar(255) DEFAULT NULL COMMENT '链接名（1：授权模式获得的是接口返回的用户名，2：数据库模式获得的是用户自己填写的链接名，3：Yahoo、S3等获取的是自己填写的链接名，4：Upload使用的是文件名+UUID作为链接名）',
  `display_name` varchar(255) DEFAULT NULL,
  `uid` bigint(20) DEFAULT NULL COMMENT '链接所属的用户ID',
  `ds_id` bigint(20) DEFAULT NULL COMMENT '数据源ID',
  `ds_code` varchar(255) DEFAULT NULL COMMENT '数据源Code',
  `config` text COMMENT '配置信息（1：授权模式存储的是授权相关信息，2：数据库类型存储的是数据库的账号密码IP等链接信息，3：Upload存储的是文件的存储位置等信息）',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（-1：仅上传，0：删除，1：正常）',
  `update_time` bigint(20) DEFAULT NULL COMMENT '修改时间，存储的是时间戳',
  `space_id` varchar(255) DEFAULT NULL COMMENT '链接所属的空间ID',
  `user_name` varchar(255) DEFAULT NULL COMMENT '冗余字段，链接所属的用户名',
  `source_type` varchar(255) DEFAULT NULL,
  `is_default_timezone` tinyint(4) NOT NULL DEFAULT '1' COMMENT '取数默认时区',
  `data_timezone` varchar(255) DEFAULT NULL COMMENT '取数的时区设置',
  PRIMARY KEY (`id`),
  UNIQUE KEY `connection_id` (`connection_id`),
  KEY `space_id` (`space_id`),
  KEY `name_ds_code` (`name`,`ds_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_connection
-- ----------------------------
INSERT INTO `user_connection` VALUES ('1', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '127.0.0.1-sizzler', null, '1', '5', 'mysql', '{\"connectionName\":\"127.0.0.1-sizzler\",\"dataBaseName\":\"sizzler\",\"dataBaseType\":\"mysql\",\"dsCode\":\"mysql\",\"dsId\":5,\"host\":\"127.0.0.1\",\"operateType\":\"save\",\"password\":\"sizzler\",\"port\":\"3306\",\"spaceId\":\"83a95adb-ffe9-4180-b638-998cb75f59ca\",\"user\":\"sizzler\"}', '1', '1495334863748', '83a95adb-ffe9-4180-b638-998cb75f59ca', null, 'USER_CREATED', '1', null);

-- ----------------------------
-- Table structure for `user_connection_source`
-- ----------------------------
DROP TABLE IF EXISTS `user_connection_source`;
CREATE TABLE `user_connection_source` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `connection_id` varchar(255) DEFAULT NULL COMMENT '所属的ConnectionID',
  `source_id` varchar(255) DEFAULT NULL COMMENT '唯一ID',
  `folder_id` varchar(255) DEFAULT NULL COMMENT '目录ID，仅用于有目录的数据源',
  `file_id` varchar(255) DEFAULT NULL COMMENT '文件ID',
  `name` text COMMENT '文件名、表名',
  `uid` bigint(20) DEFAULT NULL COMMENT '所属用户ID',
  `ds_id` bigint(20) DEFAULT NULL COMMENT '所属数据源ID',
  `ds_code` varchar(256) DEFAULT NULL COMMENT '所属数据源Code',
  `config` text COMMENT 'JSON串形式，存储的是表列表的结构，包括：表名、忽略列、忽略行、表ID、列总数、行总数信息，重构后该字段废弃',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态（0：删除，1：正常）',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间，存储的是时间戳',
  `update_time` bigint(20) DEFAULT NULL COMMENT '修改时间，存储的是时间戳',
  `last_modified_date` bigint(20) DEFAULT NULL COMMENT '远程文件最后修改时间，存储的是时间戳',
  `remote_path` varchar(1024) DEFAULT NULL COMMENT '远程文件的地址',
  `remote_status` varchar(255) NOT NULL DEFAULT '1' COMMENT '远程文件的状态（0：删除，1：正常，Other：具体错误的字符串）',
  `space_id` varchar(255) DEFAULT NULL COMMENT '所属的空间ID',
  `user_name` varchar(255) DEFAULT NULL COMMENT '冗余字段，所属的用户名',
  `update_frequency` varchar(255) DEFAULT NULL COMMENT '文件更新频率， 值包括： never（不更新）、hour（按小时更新）、day（按天更新）、monday（每周一更新，即按周更新）',
  `update_hour` varchar(255) DEFAULT NULL COMMENT '文件 更新的小时数，默认为每天5点更新，默认值为 05:00， 可选值（00:00 -23:00）',
  `timezone` varchar(255) DEFAULT NULL COMMENT '用户时区： +08:00 （中国）， +09:00（日本） ， +00:00（欧美）',
  `cron_expr` varchar(255) DEFAULT NULL COMMENT 'quartz 定时更新任务对应的cron表达式',
  `last_update_time` varchar(255) DEFAULT NULL COMMENT '文件最近更新时间',
  `update_status` varchar(255) DEFAULT NULL COMMENT '文件更新状态，包括：success、failed、waiting、updating',
  `is_default_timezone` tinyint(4) NOT NULL DEFAULT '1' COMMENT '取数默认时区',
  `data_timezone` varchar(255) DEFAULT NULL COMMENT '取数的时区设置',
  PRIMARY KEY (`id`),
  UNIQUE KEY `source_id` (`source_id`) USING BTREE,
  KEY `connection_id` (`connection_id`),
  KEY `file_id` (`file_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_connection_source
-- ----------------------------
INSERT INTO `user_connection_source` VALUES ('1', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', 'sizzler', 'ptone_user', 'ptone_user', '1', '5', 'mysql', null, '1', '1495334895779', '1495335133449', null, '127.0.0.1-sizzler@#*sizzler', '1', '83a95adb-ffe9-4180-b638-998cb75f59ca', null, null, null, null, null, null, null, '1', null);

-- ----------------------------
-- Table structure for `user_connection_source_table`
-- ----------------------------
DROP TABLE IF EXISTS `user_connection_source_table`;
CREATE TABLE `user_connection_source_table` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id，无具体业务含义',
  `table_id` varchar(255) DEFAULT NULL COMMENT 'table的id （uuid）',
  `name` varchar(255) DEFAULT NULL COMMENT 'table或file的名称',
  `code` varchar(255) DEFAULT NULL COMMENT 'table的code， 文件类型： uuid， 数据库类型：table名称',
  `source_id` varchar(255) DEFAULT NULL COMMENT '对应source的id',
  `connection_id` varchar(255) DEFAULT NULL COMMENT '对应connection的id',
  `space_id` varchar(255) DEFAULT NULL COMMENT '所属空间ID',
  `ds_id` bigint(20) DEFAULT NULL COMMENT '对应数据源的id',
  `ds_code` varchar(255) DEFAULT NULL COMMENT '对应数据源的code',
  `uid` bigint(20) DEFAULT NULL COMMENT '用户id',
  `type` varchar(255) DEFAULT NULL COMMENT '表类型： row 横向表，行作为表头（目前只有这一种类型）',
  `head_index` bigint(20) DEFAULT NULL COMMENT '第几行为表头（起始行为0）',
  `head_mode` varchar(255) DEFAULT NULL COMMENT '表头类型： assign （自动分配） || custom（自定义）',
  `col_sum` bigint(20) DEFAULT NULL COMMENT '总列数',
  `row_sum` bigint(20) DEFAULT NULL COMMENT '总行数',
  `ignore_col` text COMMENT '忽略列列表',
  `ignore_row` text COMMENT '忽略行列表',
  `ignore_row_start` bigint(20) DEFAULT NULL COMMENT '忽略行起始行',
  `ignore_row_end` bigint(20) DEFAULT NULL COMMENT '忽略行j结束行',
  `create_time` varchar(255) DEFAULT NULL COMMENT '创建时间',
  `modify_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '有效状态： 0：无效， 1：有效',
  PRIMARY KEY (`id`),
  UNIQUE KEY `table_id` (`table_id`) USING BTREE,
  KEY `connection_id` (`connection_id`),
  KEY `source_id` (`source_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_connection_source_table
-- ----------------------------
INSERT INTO `user_connection_source_table` VALUES ('1', '912281b9-1bbe-4d2b-8b87-8763ee092432', 'ptone_user', 'ptone_user', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'row', '0', 'custom', '34', '6', '3,4,9,8,7', null, '0', '0', '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');

-- ----------------------------
-- Table structure for `user_connection_source_table_column`
-- ----------------------------
DROP TABLE IF EXISTS `user_connection_source_table_column`;
CREATE TABLE `user_connection_source_table_column` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id， 无具体业务含义',
  `col_id` varchar(255) DEFAULT NULL COMMENT '列id （uuid）',
  `name` varchar(255) DEFAULT NULL COMMENT '列名（指标、维度名）',
  `code` varchar(255) DEFAULT NULL COMMENT '列code， 对于文件类型列code为uuid，对于关系型数据库列code为列名',
  `col_index` bigint(20) DEFAULT NULL COMMENT '列索引顺序号(起始值为 0）',
  `table_id` varchar(255) DEFAULT NULL COMMENT '对应Table的Id',
  `source_id` varchar(255) DEFAULT NULL COMMENT '对应source的id',
  `connection_id` varchar(255) DEFAULT NULL COMMENT '对应connection的id',
  `space_id` varchar(255) DEFAULT NULL COMMENT '所属空间ID',
  `ds_id` bigint(20) DEFAULT NULL COMMENT '数据源id',
  `ds_code` varchar(255) DEFAULT NULL COMMENT '数据源code',
  `uid` bigint(20) DEFAULT NULL COMMENT '用户id',
  `type` varchar(255) DEFAULT NULL COMMENT '列类型： metrics（指标） || dimension（维度） || ignore(忽略)',
  `is_ignore` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否忽略， 0：不忽略 1：忽略',
  `is_custom` tinyint(4) NOT NULL DEFAULT '0' COMMENT '1:用户自定义 0：默认',
  `column_type` varchar(255) DEFAULT NULL COMMENT '列原始数据类型',
  `data_type` varchar(255) DEFAULT NULL COMMENT '数据类型',
  `data_format` varchar(255) DEFAULT NULL COMMENT '数据格式',
  `unit` varchar(255) DEFAULT NULL COMMENT '单位（目前没有使用）',
  `create_time` varchar(255) DEFAULT NULL COMMENT '创建时间',
  `modify_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否有效： 0：无效， 1：有效',
  PRIMARY KEY (`id`),
  UNIQUE KEY `col_id` (`col_id`) USING BTREE,
  KEY `connection_id` (`connection_id`),
  KEY `source_id` (`source_id`),
  KEY `table_id` (`table_id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_connection_source_table_column
-- ----------------------------
INSERT INTO `user_connection_source_table_column` VALUES ('1', '0dc116ad-0c1c-4dd8-9ead-f82753576f81', 'Pt_ID', 'Pt_ID', '0', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'metrics', '0', '0', null, 'NUMBER', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('2', 'f5ec3402-243c-4293-80ec-10cdeff59fb1', 'User_Name', 'User_Name', '1', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('3', '90eab13f-6021-4649-add8-557375f16ffc', 'User_Email', 'User_Email', '2', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('4', 'e7d53afd-9c35-4f8b-a380-915ca01be48d', 'User_Password', 'User_Password', '3', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '1', '1', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('5', 'f24d6305-5621-44e7-9bd3-14aae7935af6', 'Api_Key', 'Api_Key', '4', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'metrics', '1', '1', null, 'NUMBER', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('6', '6f5577d6-a4de-4cb7-9f4a-002657bc73ed', 'Create_Date', 'Create_Date', '5', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'DATETIME', 'yyyy-MM-dd HH:mm:ss', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('7', 'edf2217e-d151-425d-a982-ad7ea7d620ce', 'Activite_Date', 'Activite_Date', '6', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'DATETIME', 'yyyy-MM-dd HH:mm:ss', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('8', '2fa51513-e965-4db2-a333-751200eebe57', 'Delete_Date', 'Delete_Date', '7', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '1', '1', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('9', '7a506faf-4438-41e2-94e2-9f2c1ba794d1', 'Delete_Uid', 'Delete_Uid', '8', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '1', '1', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('10', '90063e7f-3043-4546-933a-0600e8554b40', 'Access', 'Access', '9', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'metrics', '1', '1', null, 'NUMBER', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('11', '37f6e8c3-3965-468f-a9c9-459df7c539e7', 'Login_Active', 'Login_Active', '10', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'metrics', '0', '0', null, 'NUMBER', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('12', '7ed1ad91-6159-4cee-91c5-846680bbb8c7', 'Status', 'Status', '11', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'metrics', '0', '0', null, 'NUMBER', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('13', '2b179b88-c666-4a8c-8d77-19e25df0bc5a', 'source', 'source', '12', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('14', '4a291577-e9f7-49f5-b5ed-13f678e4457b', 'is_pre_registration', 'is_pre_registration', '13', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'metrics', '0', '0', null, 'NUMBER', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('15', '910e18a9-aa86-4727-b7f2-f64adf33dec3', 'login_count', 'login_count', '14', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'metrics', '0', '0', null, 'NUMBER', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('16', '1e4bb430-593d-4c7d-b627-ac0a4281c6df', 'facebook_count', 'facebook_count', '15', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'metrics', '0', '0', null, 'NUMBER', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('17', '3e25f56b-2fa0-48e3-bebf-c532934bf9f4', 'twitter_count', 'twitter_count', '16', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'metrics', '0', '0', null, 'NUMBER', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('18', '97751bee-b02b-4e61-a02f-afad308a5ce5', 'total_count', 'total_count', '17', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'metrics', '0', '0', null, 'NUMBER', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('19', '2e41f9b8-435d-4f28-9911-f7424ddf5843', 'utm_source', 'utm_source', '18', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('20', 'c3334a0b-9d92-4870-abb2-1f8b47fbb4dc', 'utm_campaign', 'utm_campaign', '19', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('21', '113386fe-0b4b-432b-b5fc-cdf92ac3e086', 'utm_medium', 'utm_medium', '20', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('22', '71055621-77c0-467b-a05d-b8fca11e2c5a', 'sales_manager', 'sales_manager', '21', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('23', 'a1086bdd-6eb6-4e3d-85ee-09b33ead4591', 'account_manager', 'account_manager', '22', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('24', '566a637a-544c-42f9-ada0-f90639f1537f', 'name', 'name', '23', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('25', '14e2d67a-8e3d-441a-b3fc-49263b5cf839', 'company', 'company', '24', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('26', '7f6b2011-ddec-494c-add7-21e38bf7282b', 'department', 'department', '25', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('27', '765384c3-9680-4edd-b418-150623b4de76', 'phone', 'phone', '26', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('28', 'd8b9f3ab-25ee-4da7-9b6e-ba7b7f907d9c', 'title', 'title', '27', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('29', '1ce882ec-930b-49fe-8dd9-22205bd2565b', 'phone_country', 'phone_country', '28', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('30', '09da3e03-cc9b-4c06-bda0-23598a54376e', 'last_login_date', 'last_login_date', '29', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('31', '22bd03e5-4f1f-4dd0-b21d-53a051772ab1', 'is_from_space_invitation', 'is_from_space_invitation', '30', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('32', '90fa43eb-9866-4e50-b4dd-0b1c0078c1bc', 'total_password_changes', 'total_password_changes', '31', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'STRING', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('33', '5621ab43-d796-47bb-b24b-ada76d60fbd6', 'update_time', 'update_time', '32', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'dimension', '0', '0', null, 'DATETIME', 'yyyy-MM-dd HH:mm:ss', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
INSERT INTO `user_connection_source_table_column` VALUES ('34', 'c002ea2f-bec8-4c5e-9106-a80f1da76308', 'is_activited', 'is_activited', '33', '912281b9-1bbe-4d2b-8b87-8763ee092432', '08790ab1-eb26-4c8f-90f8-7fd104210ea7', '674c3f31-2eb6-4e98-a535-4ed54cf473d9', '83a95adb-ffe9-4180-b638-998cb75f59ca', '5', 'mysql', '1', 'metrics', '0', '0', null, 'NUMBER', '', null, '2017-05-21 10:52:13', '2017-05-21 10:52:13', '1');
