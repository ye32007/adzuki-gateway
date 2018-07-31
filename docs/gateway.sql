/*
SQLyog Enterprise - MySQL GUI v8.14 
MySQL - 5.6.16-log : Database - gateway
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`gateway` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `gateway`;

/*Table structure for table `api_config` */

DROP TABLE IF EXISTS `api_config`;

CREATE TABLE `api_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `api` varchar(100) NOT NULL,
  `version` varchar(100) NOT NULL,
  `dubbo_service_name` varchar(100) NOT NULL,
  `dubbo_service_version` varchar(100) NOT NULL,
  `dubbo_service_method` varchar(100) NOT NULL,
  `param_class` varchar(100) NOT NULL,
  `need_login` int(1) NOT NULL DEFAULT '0',
  `owner` varchar(100) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `name` (`api`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
