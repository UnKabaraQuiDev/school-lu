CREATE DATABASE  IF NOT EXISTS `exercise09` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `exercise09`;
-- MySQL dump 10.13  Distrib 8.0.21, for Win64 (x86_64)
--
-- Host: localhost    Database: Orders
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
-- Table structure for table `Client`
--

DROP TABLE IF EXISTS `Client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Client` (
  `pk_client` int unsigned NOT NULL AUTO_INCREMENT,
  `firstName` varchar(255) NOT NULL,
  `surname` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `town` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`pk_client`)
) ENGINE=MyISAM AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Client`
--

LOCK TABLES `Client` WRITE;
/*!40000 ALTER TABLE `Client` DISABLE KEYS */;
INSERT INTO `Client` VALUES (1,'Flavie','Da costa','f.da.costa@example.com','Pomoy','b444ac06613fc8d63795be9ad0beaf55011936ac'),(2,'Valentin','Vespasien','valentin@example.com','Buvilly','109f4b3c50d7b0df729d299bc6f8e9ef9066971f'),(3,'Gustave','Collin','gust@example.com','Marseille','3ebfa301dc59196f18593c45e519287a23297589'),(4,'Emilien','Camus','emilien@example.com','Toulouse','1ff2b3704aede04eecb51e50ca698efd50a1379b'),(5,'Firmin','Marais','firmin.marais@example.com','Lyon','911ddc3b8f9a13b5499b6bc4638a2b4f3f68bf23'),(6,'Olivier','Riou','olive.de.lugagnac@example.com','Lugagnac','a66df261120b6c2311c6ef0b1bab4e583afcbcc0'),(7,'Lucas','Jung','lucas.jung@example.com','Coulgens','ea3243132d653b39025a944e70f3ecdf70ee3994'),(8,'Maurice','Huet','maurice.townmareuil@example.com','Villemareuil','d03f9d34194393019e6d12d7c942827ebd694443'),(9,'Manon','Durand','m.durand.s.e@example.com','Saint-Etienne','53d525836cc96d089a5a4218b464fda532f7debe'),(10,'Joachim','Leon','joachim@example.com','Longwy-sur-le-Doubs','168f4029f416ee06565f12e697dfc1534ae69d32'),(11,'Muriel','Dupuis','muriel@example.com','Paris','100c4e57374fc998e57164d4c0453bd3a4876a58'),(12,'Christiane','Riou','chritianelesabrets@example.com','Les Abrets','4ff1a33e188b7b86123d6e3be2722a23514a83b4'),(13,'Jacinthe','Langlois','jacinthe.langlois@example.com','Lagney','d804cd9cc0c42b0652bab002f67858ab803c40c6'),(14,'Amaury','Payet','amaury@example.com','Avermes','d79336a97da7d284c0fe15497d2fa944d1f2abb1'),(15,'Maris','Buisson','maris@example.com','Le Havre','61bb70fa60368f069e62d601c357d203700ab2d2'),(16,'Fabrice','Foucher','fab.montlouis@example.com','Montlouis','1fbefee9cfb86926757519357e077fd6a21aef0f'),(17,'Patrick','Saunier','patrick.saunier@example.com','Saligney','08a25c0f270b29aeba650e6b2d1a9947a778c5da'),(18,'Emile','Ramos','emile@example.com','Arzay','cfc996a3aaac95f0fb508f46499dcb72b6d0abee'),(19,'Armel','Vigneron','armel.delain@example.com','Delain','bba019890aec72f6dd6b4e98513055cae61df098'),(20,'Arnaude','Vallee','armaude.vallee@example.com','Hostias','57e5a4df68387d1d97210cf40c41104ce9256cf6'),(21,'JosÃ©e','Buisson','josee.buisson@example.lu','Arzay','d79336a97da7d284c0fe15497d2fa944d1f2abb1'),(22,'Pas de commande','No order','noroder@no.com','nono','57e5a4df68387d1d97210cf40c41104ce9256cf6');
/*!40000 ALTER TABLE `Client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Order`
--

DROP TABLE IF EXISTS `Order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Order` (
  `pk_order` int unsigned NOT NULL AUTO_INCREMENT,
  `fk_client_issues` int unsigned NOT NULL,
  `purchaseDate` date NOT NULL,
  `reference` varchar(255) NOT NULL,
  `shipping` float NOT NULL,
  PRIMARY KEY (`pk_order`)
) ENGINE=MyISAM AUTO_INCREMENT=50 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Order`
--

LOCK TABLES `Order` WRITE;
/*!40000 ALTER TABLE `Order` DISABLE KEYS */;
INSERT INTO `Order` VALUES (1,20,'2019-01-01','004214',148.71),(2,3,'2019-01-03','007120',334.76),(3,11,'2019-01-04','002957',132.37),(4,6,'2019-01-07','003425',612.56),(5,17,'2019-01-08','008255',864.5),(6,7,'2019-01-09','000996',764.37),(7,2,'2019-01-10','000214',100.14),(8,7,'2019-01-11','008084',1000.08),(9,12,'2019-01-11','009773',1129.3),(10,16,'2019-01-13','004616',222.62),(11,4,'2019-01-14','003757',97),(12,9,'2019-01-15','004939',94.72),(13,14,'2019-01-16','003421',225.56),(14,6,'2019-01-16','002286',632.38),(15,3,'2019-01-17','001167',912.87),(16,15,'2019-01-18','008974',136.4),(17,9,'2019-01-19','001369',154.8),(18,17,'2019-01-20','009924',546.6),(19,3,'2019-01-21','005510',907.2),(20,17,'2019-01-22','007778',507.84),(21,17,'2019-01-23','002359',381.55),(22,15,'2019-01-25','008459',308.64),(23,4,'2019-01-27','005217',121.92),(24,12,'2019-01-29','000706',30.71),(25,9,'2019-02-01','007879',108.12),(26,8,'2019-02-02','007277',784),(27,11,'2019-02-02','002745',20.58),(28,11,'2019-02-03','001893',250.92),(29,20,'2019-02-04','001230',93.36),(30,10,'2019-02-05','000469',114.4),(31,7,'2019-02-05','008653',515.6),(32,3,'2019-02-06','001858',23.66),(33,14,'2019-02-07','003330',79.92),(34,2,'2019-02-08','001074',810.2),(35,5,'2019-02-08','005379',93.68),(36,16,'2019-02-09','003672',554.7),(37,10,'2019-02-09','002220',15.68),(38,19,'2019-02-10','000086',343.62),(39,8,'2019-02-11','003770',647.1),(40,2,'2019-02-12','008590',562.02),(41,2,'2019-02-12','001639',445.9),(42,4,'2019-02-13','002426',68.1),(43,13,'2019-02-14','007209',131),(44,13,'2019-02-15','008768',267.2),(45,7,'2019-02-16','002213',49.77),(46,12,'2019-02-17','004759',56.43),(47,19,'2019-02-18','007155',145.44),(48,2,'2019-02-19','001496',938.7),(49,21,'2020-09-11','002213',30.48);
/*!40000 ALTER TABLE `Order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Item`
--

DROP TABLE IF EXISTS `Item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Item` (
  `pk_Item` int unsigned NOT NULL AUTO_INCREMENT,
  `fk_order_concerns` int unsigned NOT NULL,
  `designation` varchar(255) NOT NULL,
  `quantity` int unsigned DEFAULT NULL,
  `unitPrice` float unsigned DEFAULT NULL,
  PRIMARY KEY (`pk_Item`)
) ENGINE=MyISAM AUTO_INCREMENT=123 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Item`
--

LOCK TABLES `Item` WRITE;
/*!40000 ALTER TABLE `Item` DISABLE KEYS */;
INSERT INTO `Item` VALUES (1,1,'Produit 19',3,49.57),
(2,1,'Produit 92',4,81.24),
(3,1,'Produit 68',2,17.48),(4,2,'Produit 53',4,83.69),(5,2,'Produit 78',6,5.99),(6,3,'Produit D9',7,18.91),(7,4,'Produit A3',8,76.57),(8,4,'Produit BB',10,86.14),(9,4,'Produit 7C',4,80.96),(10,4,'Produit 78',9,26.4),(11,4,'Produit 07',6,9.13),(12,5,'Produit 00',10,86.45),(13,5,'Produit 7A',2,44.86),(14,6,'Produit E1',9,84.93),(15,7,'Produit D6',2,50.07),(16,7,'Produit BD',7,115.55),(17,7,'Produit D9',3,67.55),(18,8,'Produit 55',9,111.12),(19,9,'Produit C7',10,112.93),(20,10,'Produit 2A',2,111.31),(21,10,'Produit 07',5,97.75),(22,10,'Produit FC',10,34.8),(23,10,'Produit A6',5,0.76),(24,11,'Produit 4B',5,19.4),(25,12,'Produit 12',1,94.72),(26,12,'Produit 09',7,55.39),(27,13,'Produit EA',2,112.78),(28,13,'Produit CB',6,37.73),
(29,14,'Produit A1',7,90.34),(30,14,'Produit 00',6,98.49),(31,15,'Produit 67',9,101.43),(32,15,'Produit 52',8,91.68),(33,16,'Produit 2E',4,34.1),(34,17,'Produit 36',10,15.48),(35,17,'Produit 67',2,95.08),(36,17,'Produit 4D',5,68.97),(37,17,'Produit 1D',10,59.6),(38,18,'Produit 4C',6,91.1),(39,18,'Produit 12',8,36.69),(40,18,'Produit 13',6,30.17),(41,18,'Produit 22',1,40.78),(42,19,'Produit CE',8,113.4),(43,20,'Produit DD',6,84.64),(44,20,'Produit C4',5,82.99),(45,20,'Produit 43',6,41.06),(46,21,'Produit F4',5,76.31),(47,21,'Produit FC',7,18.36),
(48,22,'Produit 95',3,102.88),(49,22,'Produit E1',7,99.32),(50,22,'Produit 2D',9,67.95),
(51,22,'Produit 6C',7,41.8),(52,22,'Produit 49',4,5.14),(53,23,'Produit 6D',6,20.32),
(54,23,'Produit 8A',9,86.16),(55,23,'Produit EE',10,9.84),(56,24,'Produit B9',1,30.71),(57,24,'Produit C8',4,4.03),(58,24,'Produit E5',4,48.04),
(59,25,'Produit 0F',1,108.12),
(60,25,'Produit DD',10,36.47),
(61,26,'Produit 67',8,98),
(62,27,'Produit 41',1,20.58),(63,27,'Produit 54',7,48.89),(64,28,'Produit 65',4,62.73),(65,28,'Produit 93',9,46.97),(66,29,'Produit 9A',2,46.68),(67,29,'Produit D2',9,92.48),
(68,29,'Produit 6D',4,82.35),(69,30,'Produit 20',8,14.3),(70,31,'Produit 3C',8,64.45),
(71,31,'Produit 60',6,39.34),(72,32,'Produit 63',7,3.38),(73,32,'Produit 95',8,18.86),(74,32,'Produit 62',6,84.17),(75,32,'Produit DE',5,4.28),
(76,33,'Produit D0',9,8.88),(
77,33,'Produit D6',9,31.55),(78,33,'Produit C3',7,11.14),(79,34,'Produit 5E',10,81.02),(80,35,'Produit B2',8,11.71),(81,36,'Produit 3C',10,55.47),(82,37,'Produit 6F',7,2.24),(83,37,'Produit 16',2,84.8),(84,38,'Produit 1A',6,57.27),(85,38,'Produit 24',7,31.93),(86,39,'Produit DF',6,107.85),(87,39,'Produit 7F',9,83.44),(88,40,'Produit 6D',6,93.67),(89,40,'Produit 6B',3,98.04),(90,41,'Produit 8A',5,89.18),(91,41,'Produit 6D',4,31.78),(92,42,'Produit 1C',6,11.35),(93,42,'Produit 52',8,81.43),(94,43,'Produit B4',10,13.1),(95,43,'Produit FD',8,61.21),(96,44,'Produit 4A',10,26.72),(97,44,'Produit D4',5,70.01),(98,44,'Produit 9B',6,29.86),(99,44,'Produit BE',3,59.3),(100,44,'Produit 86',4,86.9),(101,45,'Produit F0',3,16.59),(102,45,'Produit 6A',2,62.25),(103,45,'Produit 85',10,21.48),(104,45,'Produit EF',5,40.65),(105,46,'Produit C4',3,18.81),(106,46,'Produit F9',6,92.09),(107,46,'Produit 05',6,44.02),(108,46,'Produit 3A',8,63.84),(109,46,'Produit 2E',2,67.15),(110,47,'Produit 6E',6,24.24),(111,47,'Produit F8',6,39.74),(112,47,'Produit A9',10,5.97),(113,47,'Produit 21',2,30.63),(114,47,'Produit 93',7,15.24),(115,48,'Produit E4',9,104.3),(116,48,'Produit 72',5,115.8),(117,48,'Produit DB',7,26.1),(118,48,'Produit DE',9,23.12),(119,48,'Produit 3D',7,37.26),(120,48,'Produit C5',4,116.97),(121,49,'Produit 93',2,15.24),(122,49,'Produit 72',1,115.8);
/*!40000 ALTER TABLE `Item` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-11-10 22:27:52
