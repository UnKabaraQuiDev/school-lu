CREATE DATABASE IF NOT EXISTS `Ex07_CyclingFederation` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `Ex07_CyclingFederation`;
-- MySQL dump 10.13  Distrib 8.0.18, for macos10.14 (x86_64)
--
-- Host: 127.0.0.1    Database: Ex07_CyclingFederation
-- ------------------------------------------------------
-- Server version	8.0.18

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `DopingTest`
--

DROP TABLE IF EXISTS `DopingTest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `DopingTest` (
  `pk_test` int(11) NOT NULL AUTO_INCREMENT,
  `fk_doctor_performs` int(11) DEFAULT NULL,
  `fk_race_undertakes` varchar(45) DEFAULT NULL,
  `fk_cyclist_undergoes` varchar(10) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `result` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`pk_test`),
  KEY `FC_DopingTest_Medecin_idx` (`fk_doctor_performs`),
  KEY `FC_DopingTest_Course_idx` (`fk_race_undertakes`),
  KEY `FC_DopingTest_Coureur_idx` (`fk_cyclist_undergoes`),
  CONSTRAINT `FC_DopingTest_Coureur` FOREIGN KEY (`fk_cyclist_undergoes`) REFERENCES `Cyclist` (`pk_license`),
  CONSTRAINT `FC_DopingTest_Course` FOREIGN KEY (`fk_race_undertakes`) REFERENCES `Race` (`pk_name`),
  CONSTRAINT `FC_DopingTest_Medecin` FOREIGN KEY (`fk_doctor_performs`) REFERENCES `Doctor` (`pk_code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DopingTest`
--

LOCK TABLES `DopingTest` WRITE;
/*!40000 ALTER TABLE `DopingTest` DISABLE KEYS */;
INSERT INTO `DopingTest` VALUES (1,103,'TourDeFrance','109-60','2005-07-11','negative'),(2,103,'TourDeFrance','616-92','2005-07-11','negative'),(3,105,'TourDeFrance','109-60','2005-07-23','negative'),(4,103,'TourDeFrance','109-60','2005-07-24','negative');
/*!40000 ALTER TABLE `DopingTest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Coureur`
--

DROP TABLE IF EXISTS `Cyclist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Cyclist` (
  `pk_license` varchar(10) NOT NULL,
  `fk_team_belongs` varchar(45) DEFAULT NULL COMMENT 'fk',
  `surname` varchar(45) DEFAULT NULL,
  `firstName` varchar(45) DEFAULT NULL,
  `nationality` varchar(45) DEFAULT NULL,
  `dateOfBirth` date DEFAULT NULL COMMENT 'Format: YYYY/MM/DD',
  PRIMARY KEY (`pk_license`),
  KEY `FC_Cyclist_Team_idx` (`fk_team_belongs`),
  CONSTRAINT `FC_Cyclist_Team` FOREIGN KEY (`fk_team_belongs`) REFERENCES `Team` (`pk_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Coureur`
--

LOCK TABLES `Cyclist` WRITE;
/*!40000 ALTER TABLE `Cyclist` DISABLE KEYS */;
INSERT INTO `Cyclist` VALUES ('109-60','Très-Mobile Team','Zevilla','Oscar','ESP','1976-09-29'),('256-89','Très-Mobile Team','Tsabel','Rikki','ALL','1970-07-07'),('258-61','Très-Mobile Team','Besenmann','Stefan','GER','1976-06-09'),('258-62','Disney-Channel','Yoakim','Benoa','LUX','1976-01-14'),('258-66','Quick-Fall','Littini','Paolo','ITA','1977-05-12'),('259-48','Rabobank','Hoogard','Pitti','NED','1978-02-24'),('276-04','Très-Mobile Team','Ülrik','Jean','ALL','1973-12-02'),('300-09','Rabobank','Hoogard','Mich','NED','1975-11-20'),('403-24','Disney-Channel','Bezevedo','Jos','LUX','1976-08-24'),('404-24','Disney-Channel','Bezevedo','Jos','POR','1973-09-19'),('503-99','Team Tse-Tse','Vogel','Jens','GER','1975-05-19'),('616-92','Rabobank','Gonzalez','Michel','POR','1977-12-12'),('700-11','Fassa Bambino','Eglisias','Kimi','ESP','1976-03-03'),('718-33','Disney-Channel','Armstark','Länz','USA','1971-09-18');
/*!40000 ALTER TABLE `Cyclist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Race`
--

DROP TABLE IF EXISTS `Race`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Race` (
  `pk_name` varchar(45) NOT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  PRIMARY KEY (`pk_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Race`
--

LOCK TABLES `Race` WRITE;
/*!40000 ALTER TABLE `Race` DISABLE KEYS */;
INSERT INTO `Race` VALUES ('AmstelGoldRace','2005-04-17','2005-04-19'),('Paris-Roubaix','2005-04-10','2005-04-10'),('TourDeFrance','2005-07-02','2005-07-24');
/*!40000 ALTER TABLE `Race` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Equipe`
--

DROP TABLE IF EXISTS `Team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Team` (
  `pk_name` varchar(45) NOT NULL,
  `manager` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`pk_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='		';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Equipe`
--

LOCK TABLES `Team` WRITE;
/*!40000 ALTER TABLE `Team` DISABLE KEYS */;
INSERT INTO `Team` VALUES ('Disney-Channel','JOHANN DUCK'),('Fassa Bambino','DONALD DUCK'),('Quick-Fall','VON TIEF BERT'),('Robobank','QUINTO JOAO'),('Team Tse-Tse','VAN DER POOL LISI'),('Très-Mobile Team','WALTER KNALLTER');
/*!40000 ALTER TABLE `Team` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Stage`
--

DROP TABLE IF EXISTS `Stage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Stage` (
  `pk_code` varchar(45) NOT NULL,
  `fk_race_consists` varchar(45) DEFAULT NULL COMMENT 'fk',
  `date` date DEFAULT NULL,
  `start` varchar(45) DEFAULT NULL,
  `arrival` varchar(45) DEFAULT NULL,
  `distance` double DEFAULT NULL,
  PRIMARY KEY (`pk_code`),
  KEY `FC_Stage_race_idx` (`fk_race_consists`),
  CONSTRAINT `FC_Stage_Race` FOREIGN KEY (`fk_race_consists`) REFERENCES `Race` (`pk_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Stage`
--

LOCK TABLES `Stage` WRITE;
/*!40000 ALTER TABLE `Stage` DISABLE KEYS */;
INSERT INTO `Stage` VALUES ('AmstelGoldRace-01','AmstelGoldRace','2005-04-17','Maastricht','Valkenburg',180),('Paris-Roubaix-01','Paris-Roubaix','2005-04-10','Compiègne','Roubaix',260),('TourDeFrance-01','TourDeFrance','2005-07-02','Fromentine','Ile de Noirmoutier',19),('TourDeFrance-02','TourDeFrance','2005-07-03','Challans','Les Essarts',182),('TourDeFrance-03','TourDeFrance','2005-07-04','La Chataigneraie','Tours',208);
/*!40000 ALTER TABLE `Stage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Engages`
--

DROP TABLE IF EXISTS `Engages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Engages` (
  `pkfk_team` varchar(45) NOT NULL,
  `pkfk_race` varchar(45) NOT NULL,
  PRIMARY KEY (`pkfk_team`,`pkfk_race`),
  KEY `FK_Engages_Team_idx` (`pkfk_team`),
  CONSTRAINT `FC_Engages_Race` FOREIGN KEY (`pkfk_race`) REFERENCES `Race` (`pk_name`),
  CONSTRAINT `FC_Engages_Team` FOREIGN KEY (`pkfk_team`) REFERENCES `Team` (`pk_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Engages`
--

LOCK TABLES `Engages` WRITE;
/*!40000 ALTER TABLE `Engages` DISABLE KEYS */;
/*!40000 ALTER TABLE `Engages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Doctor`
--

DROP TABLE IF EXISTS `Doctor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Doctor` (
  `pk_code` int(11) NOT NULL,
  `surname` varchar(45) DEFAULT NULL,
  `firstName` varchar(45) DEFAULT NULL,
  `nationality` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`pk_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Medecin`
--

LOCK TABLES `Doctor` WRITE;
/*!40000 ALTER TABLE `Doctor` DISABLE KEYS */;
INSERT INTO `Doctor` VALUES (103,'Roinard','Emilien', null),(105,'Duclos','Pierre', null);
/*!40000 ALTER TABLE `Doctor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Participer`
--

DROP TABLE IF EXISTS `Participates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Participates` (
  `pkfk_cyclist_Participates` varchar(10) NOT NULL,
  `pkfk_stage_Participates` varchar(45) NOT NULL,
  `time` time DEFAULT NULL,
  `points` int(11) DEFAULT NULL,
  PRIMARY KEY (`pkfk_cyclist_Participates`,`pkfk_stage_Participates`),
  KEY `FC_Participers_Stage` (`pkfk_stage_Participates`),
  CONSTRAINT `FC_Participes_Cyclist` FOREIGN KEY (`pkfk_cyclist_Participates`) REFERENCES `Cyclist` (`pk_license`),
  CONSTRAINT `FC_Participes_Stage` FOREIGN KEY (`pkfk_stage_Participates`) REFERENCES `Stage` (`pk_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Participer`
--

LOCK TABLES `Participates` WRITE;
/*!40000 ALTER TABLE `Participates` DISABLE KEYS */;
INSERT INTO `Participates` VALUES ('258-61','AmstelGoldRace-01','05:39:52',74),('258-66','AmstelGoldRace-01','05:39:53',68),('300-09','AmstelGoldRace-01','05:40:01',62),('300-09','TourDeFrance-01','06:15:45',65),('300-09','TourDeFrance-02','04:59:12',50),('300-09','TourDeFrance-03','05:46:23',45),('403-24','TourDeFrance-03','05:46:48',40),('503-99','TourDeFrance-03','05:47:03',35),('700-11','TourDeFrance-03','05:46:24',50),('718-33','AmstelGoldRace-01','05:37:08',80),('718-33','TourDeFrance-01','06:02:58',80);
/*!40000 ALTER TABLE `Participates` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-11-26 20:01:17
