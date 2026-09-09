CREATE DATABASE  IF NOT EXISTS `Réservations` /*!40100 DEFAULT CHARACTER SET latin1 COLLATE latin1_general_cs */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `Réservations`;
-- MySQL dump 10.13  Distrib 8.0.21, for Win64 (x86_64)
--
-- Host: localhost    Database: Réservations
-- ------------------------------------------------------
-- Server version	8.0.21

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
-- Table structure for table `Concerner`
--

DROP TABLE IF EXISTS `Concerner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Concerner` (
  `réservation_Concerner_FKPK` int NOT NULL,
  `noHoraire_Concerner_FKPK` varchar(4) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `date_PK` date NOT NULL COMMENT 'La date exacte à laquelle l''impression aura/a lieu\nFormat: ANNEE-MOIS-JOUR',
  PRIMARY KEY (`réservation_Concerner_FKPK`,`noHoraire_Concerner_FKPK`,`date_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Concerner`
--

LOCK TABLES `Concerner` WRITE;
/*!40000 ALTER TABLE `Concerner` DISABLE KEYS */;
INSERT INTO `Concerner` VALUES (1,'MA10','2020-01-28'),(3,'MA08','2020-01-21');
/*!40000 ALTER TABLE `Concerner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `EtreOccupée`
--

DROP TABLE IF EXISTS `EtreOccupée`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `EtreOccupée` (
  `noHoraire_EtreOccupée_FKPK` varchar(4) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `noSalle_EtreOccupée_FKPK` varchar(5) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`noHoraire_EtreOccupée_FKPK`,`noSalle_EtreOccupée_FKPK`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `EtreOccupée`
--

LOCK TABLES `EtreOccupée` WRITE;
/*!40000 ALTER TABLE `EtreOccupée` DISABLE KEYS */;
INSERT INTO `EtreOccupée` VALUES ('JE08','REL01'),('JE09','REL01'),('JE10','REL01'),('JE16','REL01'),('LU08','REL01'),('LU10','REL01'),('LU11','REL01'),('LU16','REL01'),('MA08','MAK2'),('MA08','REL01'),('MA09','MAK2'),('MA09','REL01'),('ME09','REL01'),('ME10','REL01'),('ME12','REL01'),('ME14','REL01'),('ME15','REL01'),('VE08','REL01'),('VE09','REL01'),('VE11','REL01'),('VE13','MAK2'),('VE13','REL01'),('VE15','MAK2'),('VE15','REL01');
/*!40000 ALTER TABLE `EtreOccupée` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Horaire`
--

DROP TABLE IF EXISTS `Horaire`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Horaire` (
  `noHoraire_PK` varchar(4) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `noJourSemaine` tinyint DEFAULT NULL,
  `jourSemaine` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  `noLeçonDuJour` tinyint DEFAULT NULL,
  `descriptionHoraire` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`noHoraire_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Horaire`
--

LOCK TABLES `Horaire` WRITE;
/*!40000 ALTER TABLE `Horaire` DISABLE KEYS */;
INSERT INTO `Horaire` VALUES ('DI00',7,'Dimanche',NULL,'0:00 - 1:00'),('DI01',7,'Dimanche',NULL,'1:00 -2:00'),('DI02',7,'Dimanche',NULL,'2:00 - 3.00'),('DI03',7,'Dimanche',NULL,'3:00 - 4:00'),('DI04',7,'Dimanche',NULL,'4:00 - 5:00'),('DI05',7,'Dimanche',NULL,'5:00 - 6:00'),('DI06',7,'Dimanche',NULL,'6:00 - 7:00'),('DI07',7,'Dimanche',NULL,'7:00 - 8:00'),('DI08',7,'Dimanche',NULL,'8:00 - 9:00'),('DI09',7,'Dimanche',NULL,'9:00 - 10:00'),('DI10',7,'Dimanche',NULL,'10:00 - 11:00'),('DI11',7,'Dimanche',NULL,'11:00 - 12:00'),('DI12',7,'Dimanche',NULL,'12:00 - 13:00'),('DI13',7,'Dimanche',NULL,'13:00 - 14:00'),('DI14',7,'Dimanche',NULL,'14:00 - 15:00'),('DI15',7,'Dimanche',NULL,'15:00 - 16:00'),('DI16',7,'Dimanche',NULL,'16:00 - 17:00'),('DI17',7,'Dimanche',NULL,'17:00 - 18:00'),('DI18',7,'Dimanche',NULL,'18:00 - 19:00'),('DI19',7,'Dimanche',NULL,'19:00 - 20:00'),('DI20',7,'Dimanche',NULL,'20:00 - 21:00'),('DI21',7,'Dimanche',NULL,'21:00 - 22:00'),('DI22',7,'Dimanche',NULL,'22:00 - 23:00'),('DI23',7,'Dimanche',NULL,'23:00 - 24:00'),('JE00',4,'Jeudi',NULL,'0:00 - 1:00'),('JE01',4,'Jeudi',NULL,'1:00 -2:00'),('JE02',4,'Jeudi',NULL,'2:00 - 3.00'),('JE03',4,'Jeudi',NULL,'3:00 - 4:00'),('JE04',4,'Jeudi',NULL,'4:00 - 5:00'),('JE05',4,'Jeudi',NULL,'5:00 - 6:00'),('JE06',4,'Jeudi',NULL,'6:00 - 7:00'),('JE07',4,'Jeudi',NULL,'7:00 - 8:00'),('JE08',4,'Jeudi',1,'8:00 - 9:00'),('JE09',4,'Jeudi',2,'9:00 - 10:00'),('JE10',4,'Jeudi',3,'10:00 - 11:00'),('JE11',4,'Jeudi',4,'11:00 - 12:00'),('JE12',4,'Jeudi',5,'12:00 - 13:00'),('JE13',4,'Jeudi',6,'13:00 - 14:00'),('JE14',4,'Jeudi',7,'14:00 - 15:00'),('JE15',4,'Jeudi',8,'15:00 - 16:00'),('JE16',4,'Jeudi',9,'16:00 - 17:00'),('JE17',4,'Jeudi',NULL,'17:00 - 18:00'),('JE18',4,'Jeudi',NULL,'18:00 - 19:00'),('JE19',4,'Jeudi',NULL,'19:00 - 20:00'),('JE20',4,'Jeudi',NULL,'20:00 - 21:00'),('JE21',4,'Jeudi',NULL,'21:00 - 22:00'),('JE22',4,'Jeudi',NULL,'22:00 - 23:00'),('JE23',4,'Jeudi',NULL,'23:00 - 24:00'),('LU00',1,'Lundi',NULL,'0:00 - 1:00'),('LU01',1,'Lundi',NULL,'1:00 -2:00'),('LU02',1,'Lundi',NULL,'2:00 - 3.00'),('LU03',1,'Lundi',NULL,'3:00 - 4:00'),('LU04',1,'Lundi',NULL,'4:00 - 5:00'),('LU05',1,'Lundi',NULL,'5:00 - 6:00'),('LU06',1,'Lundi',NULL,'6:00 - 7:00'),('LU07',1,'Lundi',NULL,'7:00 - 8:00'),('LU08',1,'Lundi',1,'8:00 - 9:00'),('LU09',1,'Lundi',2,'9:00 - 10:00'),('LU10',1,'Lundi',3,'10:00 - 11:00'),('LU11',1,'Lundi',4,'11:00 - 12:00'),('LU12',1,'Lundi',5,'12:00 - 13:00'),('LU13',1,'Lundi',6,'13:00 - 14:00'),('LU14',1,'Lundi',7,'14:00 - 15:00'),('LU15',1,'Lundi',8,'15:00 - 16:00'),('LU16',1,'Lundi',9,'16:00 - 17:00'),('LU17',1,'Lundi',NULL,'17:00 - 18:00'),('LU18',1,'Lundi',NULL,'18:00 - 19:00'),('LU19',1,'Lundi',NULL,'19:00 - 20:00'),('LU20',1,'Lundi',NULL,'20:00 - 21:00'),('LU21',1,'Lundi',NULL,'21:00 - 22:00'),('LU22',1,'Lundi',NULL,'22:00 - 23:00'),('LU23',1,'Lundi',NULL,'23:00 - 24:00'),('MA00',2,'Mardi',NULL,'0:00 - 1:00'),('MA01',2,'Mardi',NULL,'1:00 -2:00'),('MA02',2,'Mardi',NULL,'2:00 - 3.00'),('MA03',2,'Mardi',NULL,'3:00 - 4:00'),('MA04',2,'Mardi',NULL,'4:00 - 5:00'),('MA05',2,'Mardi',NULL,'5:00 - 6:00'),('MA06',2,'Mardi',NULL,'6:00 - 7:00'),('MA07',2,'Mardi',NULL,'7:00 - 8:00'),('MA08',2,'Mardi',1,'8:00 - 9:00'),('MA09',2,'Mardi',2,'9:00 - 10:00'),('MA10',2,'Mardi',3,'10:00 - 11:00'),('MA11',2,'Mardi',4,'11:00 - 12:00'),('MA12',2,'Mardi',5,'12:00 - 13:00'),('MA13',2,'Mardi',6,'13:00 - 14:00'),('MA14',2,'Mardi',7,'14:00 - 15:00'),('MA15',2,'Mardi',8,'15:00 - 16:00'),('MA16',2,'Mardi',9,'16:00 - 17:00'),('MA17',2,'Mardi',NULL,'17:00 - 18:00'),('MA18',2,'Mardi',NULL,'18:00 - 19:00'),('MA19',2,'Mardi',NULL,'19:00 - 20:00'),('MA20',2,'Mardi',NULL,'20:00 - 21:00'),('MA21',2,'Mardi',NULL,'21:00 - 22:00'),('MA22',2,'Mardi',NULL,'22:00 - 23:00'),('MA23',2,'Mardi',NULL,'23:00 - 24:00'),('ME00',3,'Mercredi',NULL,'0:00 - 1:00'),('ME01',3,'Mercredi',NULL,'1:00 -2:00'),('ME02',3,'Mercredi',NULL,'2:00 - 3.00'),('ME03',3,'Mercredi',NULL,'3:00 - 4:00'),('ME04',3,'Mercredi',NULL,'4:00 - 5:00'),('ME05',3,'Mercredi',NULL,'5:00 - 6:00'),('ME06',3,'Mercredi',NULL,'6:00 - 7:00'),('ME07',3,'Mercredi',NULL,'7:00 - 8:00'),('ME08',3,'Mercredi',1,'8:00 - 9:00'),('ME09',3,'Mercredi',2,'9:00 - 10:00'),('ME10',3,'Mercredi',3,'10:00 - 11:00'),('ME11',3,'Mercredi',4,'11:00 - 12:00'),('ME12',3,'Mercredi',5,'12:00 - 13:00'),('ME13',3,'Mercredi',6,'13:00 - 14:00'),('ME14',3,'Mercredi',7,'14:00 - 15:00'),('ME15',3,'Mercredi',8,'15:00 - 16:00'),('ME16',3,'Mercredi',9,'16:00 - 17:00'),('ME17',3,'Mercredi',NULL,'17:00 - 18:00'),('ME18',3,'Mercredi',NULL,'18:00 - 19:00'),('ME19',3,'Mercredi',NULL,'19:00 - 20:00'),('ME20',3,'Mercredi',NULL,'20:00 - 21:00'),('ME21',3,'Mercredi',NULL,'21:00 - 22:00'),('ME22',3,'Mercredi',NULL,'22:00 - 23:00'),('ME23',3,'Mercredi',NULL,'23:00 - 24:00'),('SA00',6,'Samedi',NULL,'0:00 - 1:00'),('SA01',6,'Samedi',NULL,'1:00 -2:00'),('SA02',6,'Samedi',NULL,'2:00 - 3.00'),('SA03',6,'Samedi',NULL,'3:00 - 4:00'),('SA04',6,'Samedi',NULL,'4:00 - 5:00'),('SA05',6,'Samedi',NULL,'5:00 - 6:00'),('SA06',6,'Samedi',NULL,'6:00 - 7:00'),('SA07',6,'Samedi',NULL,'7:00 - 8:00'),('SA08',6,'Samedi',NULL,'8:00 - 9:00'),('SA09',6,'Samedi',NULL,'9:00 - 10:00'),('SA10',6,'Samedi',NULL,'10:00 - 11:00'),('SA11',6,'Samedi',NULL,'11:00 - 12:00'),('SA12',6,'Samedi',NULL,'12:00 - 13:00'),('SA13',6,'Samedi',NULL,'13:00 - 14:00'),('SA14',6,'Samedi',NULL,'14:00 - 15:00'),('SA15',6,'Samedi',NULL,'15:00 - 16:00'),('SA16',6,'Samedi',NULL,'16:00 - 17:00'),('SA17',6,'Samedi',NULL,'17:00 - 18:00'),('SA18',6,'Samedi',NULL,'18:00 - 19:00'),('SA19',6,'Samedi',NULL,'19:00 - 20:00'),('SA20',6,'Samedi',NULL,'20:00 - 21:00'),('SA21',6,'Samedi',NULL,'21:00 - 22:00'),('SA22',6,'Samedi',NULL,'22:00 - 23:00'),('SA23',6,'Samedi',NULL,'23:00 - 24:00'),('VE00',5,'Vendredi',NULL,'0:00 - 1:00'),('VE01',5,'Vendredi',NULL,'1:00 -2:00'),('VE02',5,'Vendredi',NULL,'2:00 - 3.00'),('VE03',5,'Vendredi',NULL,'3:00 - 4:00'),('VE04',5,'Vendredi',NULL,'4:00 - 5:00'),('VE05',5,'Vendredi',NULL,'5:00 - 6:00'),('VE06',5,'Vendredi',NULL,'6:00 - 7:00'),('VE07',5,'Vendredi',NULL,'7:00 - 8:00'),('VE08',5,'Vendredi',1,'8:00 - 9:00'),('VE09',5,'Vendredi',2,'9:00 - 10:00'),('VE10',5,'Vendredi',3,'10:00 - 11:00'),('VE11',5,'Vendredi',4,'11:00 - 12:00'),('VE12',5,'Vendredi',5,'12:00 - 13:00'),('VE13',5,'Vendredi',6,'13:00 - 14:00'),('VE14',5,'Vendredi',7,'14:00 - 15:00'),('VE15',5,'Vendredi',8,'15:00 - 16:00'),('VE16',5,'Vendredi',9,'16:00 - 17:00'),('VE17',5,'Vendredi',NULL,'17:00 - 18:00'),('VE18',5,'Vendredi',NULL,'18:00 - 19:00'),('VE19',5,'Vendredi',NULL,'19:00 - 20:00'),('VE20',5,'Vendredi',NULL,'20:00 - 21:00'),('VE21',5,'Vendredi',NULL,'21:00 - 22:00'),('VE22',5,'Vendredi',NULL,'22:00 - 23:00'),('VE23',5,'Vendredi',NULL,'23:00 - 24:00');
/*!40000 ALTER TABLE `Horaire` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Imprimante3D`
--

DROP TABLE IF EXISTS `Imprimante3D`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Imprimante3D` (
  `noImprimante_PK` int NOT NULL AUTO_INCREMENT,
  `noSérie` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  `marque` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  `modèle` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  `ip` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  `noSalle_EtreInstallée_FK` varchar(5) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`noImprimante_PK`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Imprimante3D`
--

LOCK TABLES `Imprimante3D` WRITE;
/*!40000 ALTER TABLE `Imprimante3D` DISABLE KEYS */;
INSERT INTO `Imprimante3D` VALUES (1,'AY1234','Prusa','i3 MK3s','10.0.2.3','MAK02'),(2,'CE2343','CEL-UK','RoboxPro','10.1.2.3','REL01'),(3,'ULM2443G','Ultimaker','S3','200.1.1.1','MAK02'),(4,'FL24NSS','FormLabs','Form 3','50.2.5.43','CLI01'),(5,'UL2ERG4','Ultimaker','2+','10.0.4.5','CLI02'),(6,'ULM11GE','Ultimaker','1','200.1.2.2',NULL),(7,'GT44d','Prusa','i2','50.2.3.77',NULL);
/*!40000 ALTER TABLE `Imprimante3D` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Personne`
--

DROP TABLE IF EXISTS `Personne`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Personne` (
  `codeIAM_PK` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `nom` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  `prénom` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  `email` varchar(100) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`codeIAM_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Personne`
--

LOCK TABLES `Personne` WRITE;
/*!40000 ALTER TABLE `Personne` DISABLE KEYS */;
INSERT INTO `Personne` VALUES ('BarGu111','Baron de Rotschild','Gusti','BarGu111@school.com'),('BisGa608','Bishop','Gabriel','BisGa608@school.com'),('ColAm521','Collins','Amanda','ColAm521@school.com'),('ColHe094','Coleman','Herbert','ColHe094@school.com'),('DucJo107','Duc De Nassau','Johann','DucJo107@school.com'),('EllCl093','Elliott','Clifford','EllCl093@school.com'),('FraSe369','Frank','Sean','FraSe369@school.com'),('HanBi524','Hansen','Bill','HanBi524@school.com'),('HolBr122','Holloway','Bradley','HolBr122@school.com'),('HufTi376','Huff','Tillie','HufTi376@school.com'),('LunMa784','Luna','Maggie','LunMa784@school.com'),('MarSa933','Martinez','Sally','MarSa933@school.com'),('ParMa236','Parker','Matthew','ParMa236@school.com'),('PatCl151','Patterson','Clara','PatCl151@school.com'),('RayTe217','Ray','Terry','RayTe217@school.com'),('RobCa613','Roberson','Cameron','RobCa613@school.com'),('RobCo751','Robinson','Cody','RobCo751@school.com'),('RyaVi661','Ryan','Victoria','RyaVi661@school.com'),('StoEl304','Stone','Elsie','StoEl304@school.com'),('ValJa262','Valdez','Jared','ValJa262@school.com'),('VarMe603','Vargas','Melvin','VarMe603@school.com'),('WasSh380','Washington','Shane','WasSh380@school.com'),('WatSa387','Waters','Samuel','WatSa387@school.com');
/*!40000 ALTER TABLE `Personne` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Réservation`
--

DROP TABLE IF EXISTS `Réservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Réservation` (
  `idRéservation_PK` int NOT NULL AUTO_INCREMENT,
  `typePlastique` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  `grammesPlastique` int DEFAULT NULL COMMENT 'Nombre de grammes de plastique dont on a besoin pour imprimer',
  `prixAuGrammePlastique` int DEFAULT NULL COMMENT 'Prix en cents pour 1 gramme du plastique utilisé. Cette valeur est manuellement inscrite',
  `plastiquePayé` tinyint DEFAULT '0',
  `noImprimante_Utiliser_FK` int DEFAULT NULL,
  `codeIAM_Faire_FK` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`idRéservation_PK`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Réservation`
--

LOCK TABLES `Réservation` WRITE;
/*!40000 ALTER TABLE `Réservation` DISABLE KEYS */;
INSERT INTO `Réservation` VALUES (1,'PLA1',10,3,0,2,'FraSe369'),(2,'PLA1',200,3,1,2,'MarSa933'),(3,'PLA2',50,5,0,1,'HolBr122'),(4,'PLA1',50,3,0,1,'StoEl304'),(5,'PLA2',100,5,0,4,'StoEl304'),(6,'PET1',122,10,1,5,'EllCl093'),(7,'PET1',80,10,1,4,'MarSa933'),(8,'PLA1',40,3,0,3,'VarMe603'),(9,'PLV',12,3,1,2,'VarMe603'),(10,'PLA2',33,5,0,2,'VarMe603'),(11,'PET1',89,10,1,1,'VarMe603'),(12,'PET1',101,10,1,7,'VarMe603');
/*!40000 ALTER TABLE `Réservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Salle`
--

DROP TABLE IF EXISTS `Salle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Salle` (
  `noSalle_PK` varchar(5) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `accèsLibre` tinyint DEFAULT NULL,
  PRIMARY KEY (`noSalle_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Salle`
--

LOCK TABLES `Salle` WRITE;
/*!40000 ALTER TABLE `Salle` DISABLE KEYS */;
INSERT INTO `Salle` VALUES ('CLI01',0),('CLI02',0),('CLI03',0),('MAK01',1),('MAK02',0),('REL01',1),('REL02',0),('TECNO',1);
/*!40000 ALTER TABLE `Salle` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-03-09 14:15:19
