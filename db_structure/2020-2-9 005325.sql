/*
SQLyog Enterprise - MySQL GUI v8.14 
MySQL - 8.0.11 : Database - eamuse_db
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`eamuse_db` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `eamuse_db`;

/*Table structure for table `card` */

DROP TABLE IF EXISTS `card`;

CREATE TABLE `card` (
  `rawId` varchar(16) NOT NULL COMMENT 'E004卡号',
  `refId` varchar(16) NOT NULL COMMENT 'E004加密卡号',
  `pin` varchar(4) NOT NULL COMMENT '密码',
  UNIQUE KEY `card_rawId_uindex` (`rawId`),
  UNIQUE KEY `card_refId_uindex` (`refId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='卡数据';

/*Table structure for table `paseli` */

DROP TABLE IF EXISTS `paseli`;

CREATE TABLE `paseli` (
  `rawId` varchar(16) NOT NULL,
  `balance` int(10) NOT NULL DEFAULT '0',
  `acid` varchar(15) NOT NULL DEFAULT 'dummy_id',
  `acname` varchar(15) NOT NULL DEFAULT 'dummy_name',
  `infinite_balance` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`rawId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `paseli_season` */

DROP TABLE IF EXISTS `paseli_season`;

CREATE TABLE `paseli_season` (
  `season_id` varchar(10) NOT NULL,
  `rawId` varchar(16) NOT NULL,
  UNIQUE KEY `paseli_season_rawId_uindex` (`rawId`),
  UNIQUE KEY `paseli_season_season_name_uindex` (`season_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `sdvx_5_course` */

DROP TABLE IF EXISTS `sdvx_5_course`;

CREATE TABLE `sdvx_5_course` (
  `season_id` int(2) NOT NULL,
  `course_id` int(2) NOT NULL,
  `score` int(10) NOT NULL,
  `clear_type` int(2) NOT NULL,
  `grade` int(2) NOT NULL COMMENT '通过等级，1D 2B 3C 4A',
  `achievement_rate` int(6) NOT NULL COMMENT '通过率，如9448为 94%',
  `cnt` int(5) NOT NULL DEFAULT '1' COMMENT '尚不清楚，有可能是 COUNT',
  `refId` varchar(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='SDVX5段位成绩记录';

/*Table structure for table `sdvx_5_profile` */

DROP TABLE IF EXISTS `sdvx_5_profile`;

CREATE TABLE `sdvx_5_profile` (
  `refId` varchar(16) NOT NULL,
  `packet_point` int(15) NOT NULL DEFAULT '20000',
  `block_point` int(15) NOT NULL DEFAULT '20000',
  `blaster_energy` int(15) DEFAULT '0',
  `blaster_count` int(15) NOT NULL DEFAULT '0',
  `appeal_id` varchar(8) NOT NULL DEFAULT '1266',
  `skill_level` varchar(2) NOT NULL DEFAULT '0',
  `skill_base_id` varchar(2) NOT NULL DEFAULT '0',
  `skill_name_id` varchar(2) NOT NULL DEFAULT '0',
  `player_name` varchar(8) NOT NULL,
  `player_code` varchar(9) NOT NULL,
  UNIQUE KEY `sdvx_5_profile_refId_uindex` (`refId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='SDVX5的用户基础数据';

/*Table structure for table `sdvx_5_setting` */

DROP TABLE IF EXISTS `sdvx_5_setting`;

CREATE TABLE `sdvx_5_setting` (
  `refId` varchar(16) NOT NULL,
  `last_music_id` int(10) NOT NULL DEFAULT '1000' COMMENT '最后选择的音乐ID',
  `last_music_type` int(2) NOT NULL DEFAULT '1' COMMENT '最后选择的谱面类型',
  `sort_type` int(2) NOT NULL DEFAULT '0',
  `narrow_down` int(2) NOT NULL DEFAULT '0',
  `headphone` int(2) NOT NULL DEFAULT '0',
  `gauge_option` int(2) NOT NULL DEFAULT '0',
  `ars_option` int(2) DEFAULT '0',
  `early_late_disp` int(2) NOT NULL DEFAULT '0',
  `note_option` int(2) NOT NULL DEFAULT '0',
  `eff_c_left` int(2) NOT NULL DEFAULT '0',
  `eff_c_right` int(2) NOT NULL DEFAULT '0',
  `lanespeed` int(11) NOT NULL DEFAULT '0',
  `hispeed` int(11) NOT NULL DEFAULT '0',
  `draw_adjust` int(2) NOT NULL DEFAULT '0',
  UNIQUE KEY `sdvx_5_setting_refId_uindex` (`refId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='SDVX玩家设置';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
