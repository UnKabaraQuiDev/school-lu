CREATE DATABASE  IF NOT EXISTS `Ex11_ClubSelect` /*!40100 DEFAULT CHARACTER SET latin1 COLLATE latin1_general_cs */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `Ex11_ClubSelect`;
-- MySQL dump 10.13  Distrib 8.0.21, for Win64 (x86_64)
--
-- Host: localhost    Database: Ex11_ClubSelect
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
-- Table structure for table `Affecter`
--

DROP TABLE IF EXISTS `Assigns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Assigns` (
  `pkfk_comission` int NOT NULL,
  `pkfk_member` int NOT NULL,
  `pk_entryDate` date NOT NULL,
  `leavingDate` date DEFAULT NULL,
  PRIMARY KEY (`pkfk_Comission`,`pkfk_Member`,`pk_entryDate`),
  KEY `fk_Commission_has_Membre_Membre1_idx` (`pkfk_Member`),
  KEY `fk_Commission_has_Membre_Commission1_idx` (`pkfk_Comission`),
  CONSTRAINT `fk_Commission_has_Membre_Commission1` FOREIGN KEY (`pkfk_comission`) REFERENCES `Commission` (`pk_code`),
  CONSTRAINT `fk_Commission_has_Membre_Membre1` FOREIGN KEY (`pkfk_member`) REFERENCES `Member` (`pk_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Assigns`
--

-- LOCK TABLES `Assigns` WRITE;
/*!40000 ALTER TABLE `Assigns` DISABLE KEYS */;
INSERT INTO `Assigns` VALUES (1,7,'1980-01-30','2000-10-16'),(1,10,'1980-01-30',NULL),(2,10,'1990-02-20',NULL),(2,20,'2000-01-01',NULL),(2,21,'2000-01-01','2021-01-01'),(2,22,'2000-01-01',NULL),(2,23,'2000-01-01','2020-08-15'),(2,24,'2000-01-01',NULL);
/*!40000 ALTER TABLE `Assigns` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Commission`
--

DROP TABLE IF EXISTS `Commission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Commission` (
  `pk_code` int NOT NULL,
  `title` varchar(45) COLLATE latin1_general_cs DEFAULT NULL,
  `maxAmountOfMembers` int DEFAULT NULL,
  PRIMARY KEY (`pk_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Commission`
--

LOCK TABLES `Commission` WRITE;
/*!40000 ALTER TABLE `Commission` DISABLE KEYS */;
INSERT INTO `Commission` VALUES (1,'Finances',3),(2,'Sports',10),(3,'Direction',2),(4,'Evénements',5),(5,'Décès',1);
/*!40000 ALTER TABLE `Commission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Member`
--

DROP TABLE IF EXISTS `Member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Member` (
  `pk_code` int NOT NULL,
  `surname` varchar(45) COLLATE latin1_general_cs DEFAULT NULL,
  `firstname` varchar(45) COLLATE latin1_general_cs DEFAULT NULL,
  `birthdate` date DEFAULT NULL,
  `streetNbr` varchar(100) COLLATE latin1_general_cs DEFAULT NULL,
  `city` varchar(45) COLLATE latin1_general_cs DEFAULT NULL,
  `postcode` varchar(45) COLLATE latin1_general_cs DEFAULT NULL,
  `fk_country_lives` int DEFAULT NULL,
  PRIMARY KEY (`pk_code`),
  KEY `fk_Member_country_idx` (`fk_country_lives`),
  CONSTRAINT `fk_member_country` FOREIGN KEY (`fk_country_lives`) REFERENCES `Country` (`pk_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Member`
--

LOCK TABLES `Member` WRITE;
/*!40000 ALTER TABLE `Member` DISABLE KEYS */;
INSERT INTO `Member` VALUES (1,'McGuire','Leroy','2000-06-07','2, Fuller','Xiaping','142720',64),(2,'Flowers','Hallie','2007-06-20','2393, Tennessee','Iwkowa','3580',89),(3,'Miller','Cole','2011-09-15','6, Bellgrove','Zhujiang','10802',176),(4,'Byrd','Christine','1991-01-05','36713, Fulton','Vellinge','512 63',142),(5,'Wong','David','2006-07-20','2707, Surrey','Iwkowa','4550',29),(6,'McCormick','Francisco','2008-10-13','4198, Dryden','Gorelki','94019',118),(7,'Washington','Ian','2001-12-02','37311, Linden','Yelizavetinskaya','664 61',36),(8,'Hampton','Dale','1996-02-26','7, Westerfield','Drachten','446870',75),(9,'Chavez','Sarah','2006-02-14','5, Aberg','Tirapata','2320',121),(10,'Bowen','Ronald','2008-07-06','673, Brentwood','Pasrukrajan Satu','415 22',112),(11,'Hanson','Jonathan','2018-04-09','48, North','Fencheng','629420',23),(12,'Sharp','Lee','1990-10-24','841, Linden','Palena','11400-000',132),(13,'Baldwin','Lydia','2018-01-07','65, Pennsylvania','Mari','663-8234',52),(14,'James','Charlie','2019-06-17','835, Elgar','Iwkowa','74110',145),(15,'Powers','Nellie','2015-03-21','9, Golf','Rudnik','88815',127),(16,'Richardson','Christopher','1998-06-14','1, Clove','Moyuan','34975',139),(17,'Ortiz','Nancy','2020-08-09','14, Cordelia','Iwkowa','G6K',12),(18,'Bailey','Glen','2000-02-19','5, Algoma','Francisco J Mujica','2705-085',132),(19,'Newton','Hunter','2016-10-29','8953, Manley','Tegalgede','251 63',232),(20,'Walton','Janie','2014-01-16','680, Karstens','Gerelayang','173 18',219),(21,'Hall','Gary','2019-02-02','3193, Menomonie','Wao','699-0103',20),(22,'Graves','Lenora','1999-09-03','89647, Marquette','Blois','92622',142),(23,'Rodriguez','Katharine','2019-10-11','47377, Boyd','Lantian','2605-150',133),(24,'Murray','Clarence','2007-05-18','58, Esch','Karabash','8305',235),(25,'Hicks','Jeff','1990-03-08','88970, Swallow','Dangmu','3025-600',25),(26,'Harper','Ina','2016-10-02','7806, Bowman','Daniwato','93591',212),(27,'Flowers','Cecilia','2010-03-16','939, Hallows','Kumanovo','5105',18),(28,'Henry','Carrie-Elise','1995-10-18','37884, Scott','Ljungby','5410',132),(29,'Lloyd','Carlos','2013-04-22','59, 2nd','Amorim','8045',111),(30,'Bryant','Derek','2020-09-24','6, Graedel','Pag','51074',172),(31,'Santos','Celia','2016-12-13','25, Killdeer','Xianghua','91010',169),(32,'Harvey','Susan','2004-12-19','9, Westridge','Ugra','32128',210),(33,'Lucas','Carolyn','2018-07-22','67042, Bonner','Sarangmeduro','75280',194),(34,'White','Rachel','1991-06-16','70, Aberg','Badou','77713',239),(35,'Cox','Garrett-Tom','2008-07-29','0, Springview','Miramar','19018',132),(99,'Mueller','Tommy','2004-09-15','12, rue du Gruef','Guepper','3333',132);
/*!40000 ALTER TABLE `Member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Sponsors`
--

DROP TABLE IF EXISTS `Sponsors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Sponsors` (
  `pkfk_member_godfather` int NOT NULL,
  `pkfk_member_godchild` int NOT NULL,
  `dateOfSponsoring` date DEFAULT NULL,
  PRIMARY KEY (`pkfk_member_godfather`,`pkfk_member_godchild`),
  KEY `fk_Membre_has_Membre_Membre2_idx` (`pkfk_member_godchild`),
  KEY `fk_Membre_has_Membre_Membre1_idx` (`pkfk_member_godfather`),
  CONSTRAINT `fk_Membre_has_Membre_Membre1` FOREIGN KEY (`pkfk_member_godfather`) REFERENCES `Member` (`pk_code`),
  CONSTRAINT `fk_Membre_has_Membre_Membre2` FOREIGN KEY (`pkfk_member_godchild`) REFERENCES `Member` (`pk_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Sponsors`
--

LOCK TABLES `Sponsors` WRITE;
/*!40000 ALTER TABLE `Sponsors` DISABLE KEYS */;
INSERT INTO `Sponsors` VALUES (1,30,'2010-01-01'),(10,30,'2010-01-01'),(20,13,'2002-03-17'),(20,30,'2010-01-01'),(21,13,'2001-05-07'),(22,13,'2001-07-07'),(27,99,'2003-12-01'),(33,99,'2003-12-01'),(34,99,'2003-12-01');
/*!40000 ALTER TABLE `Sponsors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Country`
--

DROP TABLE IF EXISTS `Country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Country` (
  `pk_code` int NOT NULL,
  `designation` varchar(100) COLLATE latin1_general_cs DEFAULT NULL,
  `prefix` varchar(4) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`pk_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Country`
--

LOCK TABLES `Country` WRITE;
/*!40000 ALTER TABLE `Country` DISABLE KEYS */;
INSERT INTO `Country` VALUES (1,'','RC'),(2,'Afghanistan','AFG'),(3,'Albanie','AL'),(4,'Algérie','DZ'),(5,'Samoa américaines','USA'),(6,'Andorre','AND'),(7,NULL,'AO'),(8,'Anguilla',''),(9,'Antarctique',NULL),(10,'Antigua-et-Barbuda',''),(11,'Argentine','RA'),(12,'Arménie','AM'),(13,'Aruba','AW'),(14,'Australie','AUS'),(15,'Autriche','A'),(16,'Azerbaïdjan','AZ'),(17,'Bahamas','BS'),(18,'Bahreïn','BRN'),(19,'Bangladesh','BD'),(20,'Barbade','BDS'),(21,'Bélarus','BY'),(22,'Belgique','B'),(23,'Belize','BH'),(24,'Bénin','DY'),(25,'Bermudes','BM'),(26,'Bhoutan','BT'),(27,'Bolivie (État plurinational de)','BOL'),(28,'Bonaire, Saint-Eustache et Saba','NA'),(29,'Bosnie-Herzégovine','BIH'),(30,'Botswana','BW'),(31,'Île Bouvet','BV'),(32,'Brésil','BR'),(33,'Territoire britannique de l\'océan Indien',''),(34,'Îles Vierges britanniques','BVI'),(35,'Brunéi Darussalam','BRU'),(36,'Bulgarie','BG'),(37,'Burkina Faso','BF'),(38,'Burundi','RU'),(39,'Cabo Verde','CV'),(40,'Cambodge','K'),(41,'Cameroun','CAM'),(42,'Canada','CDN'),(43,'Îles Caïmanes','KY'),(44,'République centrafricaine','RCA'),(45,'Tchad','TCH'),(46,'Chili','RCH'),(47,'Chine','CN'),(48,'Chine, région administrative spéciale de Hong Kong','HK'),(49,'Chine, région administrative spéciale de Macao','MO'),(50,'Île Christmas','AUS'),(51,'Îles des Cocos (Keeling)','AUS'),(52,'Colombie','CO'),(53,'Comores','KM'),(54,'Congo','RCB'),(55,'Îles Cook','NZ'),(56,'Costa Rica','CR'),(57,'Croatie','HR'),(58,'Cuba','C'),(59,'Curaçao',''),(60,'Chypre','CY'),(61,'Tchéquie','CZ'),(62,'Côte d\'Ivoire','CI'),(63,'République populaire démocratique de Corée',''),(64,'République démocratique du Congo','ZRE'),(65,'Danemark','DK'),(66,'Djibouti','F'),(67,'Dominique','WD'),(68,'République dominicaine','DOM'),(69,'Équateur','EC'),(70,'Égypte','ET'),(71,'El Salvador','ES'),(72,'Guinée équatoriale','EQ'),(73,'Érythrée','ER'),(74,'Estonie','EST'),(75,'Eswatini','SD'),(76,'Éthiopie','ETH'),(77,'Îles Falkland (Malvinas)',''),(78,'Îles Féroé','FO'),(79,'Fidji','FJI'),(80,'Finlande','FIN'),(81,'France','F'),(82,'Guyane française','F'),(83,'Polynésie française','F'),(84,'Terres australes françaises','F'),(85,'Gabon','G'),(86,'Gambie','WAG'),(87,'Géorgie','GE'),(88,'Allemagne','D'),(89,'Ghana','GH'),(90,'Gibraltar','GBZ'),(91,'Grèce','GR'),(92,'Groenland','DK'),(93,'Grenade','WG'),(94,'Guadeloupe','F'),(95,'Guam','USA'),(96,'Guatemala','GCA'),(97,'Guernesey','GBG'),(98,'Guinée','RG'),(99,'Guinée-Bissau','GW'),(100,'Guyana','GUY'),(101,'Haïti','RH'),(102,'Île Heard-et-Îles MacDonald','AUS'),(103,'Saint-Siège','V'),(104,'Honduras',''),(105,'Hongrie','H'),(106,'Islande','IS'),(107,'Inde','IND'),(108,'Indonésie','RI'),(109,'Iran (République islamique d\')','IR'),(110,'Iraq','IRQ'),(111,'Irlande','IRL'),(112,'Île de Man','GBM'),(113,'Israël','IL'),(114,'Italie','I'),(115,'Jamaïque','JA'),(116,'Japon','J'),(117,'Jersey','GBJ'),(118,'Jordanie','HKJ'),(119,'Kazakhstan','KZ'),(120,'Kenya','EAK'),(121,'Kiribati',''),(122,'Koweït','KWT'),(123,'Kirghizistan','KS'),(124,'République démocratique populaire lao','LAO'),(125,'Lettonie','LV'),(126,'Liban','RL'),(127,'Lesotho','LS'),(128,'Libéria','LB'),(129,'Libye','LAR'),(130,'Liechtenstein','FL'),(131,'Lituanie','LT'),(132,'Luxembourg','L'),(133,'Madagascar','RM'),(134,'Malawi','MW'),(135,'Malaisie','MAL'),(136,'Maldives','MV'),(137,'Mali','RMM'),(138,'Malte','M'),(139,'Îles Marshall',''),(140,'Martinique','F'),(141,'Mauritanie','RIM'),(142,'Maurice','MS'),(143,'Mayotte',''),(144,'Mexique','MEX'),(145,'Micronésie (États fédérés de)',''),(146,'Monaco','MC'),(147,'Mongolie','MGL'),(148,'Monténégro','MNE'),(149,'Montserrat',''),(150,'Maroc','MA'),(151,'Mozambique','MOC'),(152,'Myanmar','BUR'),(153,'Namibie','NAM'),(154,'Nauru','NAU'),(155,'Népal','NEP'),(156,'Pays-Bas','NL'),(157,'Nouvelle-Calédonie','F'),(158,'Nouvelle-Zélande','NZ'),(159,'Nicaragua','NIC'),(160,'Niger','RN'),(161,'Nigéria','WAN'),(162,'Nioué','NZ'),(163,'Île Norfolk','AUS'),(164,'Îles Mariannes du Nord','USA'),(165,'Norvège','N'),(166,'Oman',''),(167,'Pakistan','PK'),(168,'Palaos',''),(169,'Panama','PA'),(170,'Papouasie-Nouvelle-Guinée','PNG'),(171,'Paraguay','PY'),(172,'Pérou','PE'),(173,'Philippines','RP'),(174,'Pitcairn',''),(175,'Pologne','PL'),(176,'Portugal','P'),(177,'Porto Rico','USA'),(178,'Qatar','Q'),(179,'République de Corée','ROK'),(180,'République de Moldova','MD'),(181,'Roumanie','RO'),(182,'Fédération de Russie','RUS'),(183,'Rwanda','RWA'),(184,'Réunion','F'),(185,'Saint-Barthélemy',''),(186,'Sainte-Hélène','SH'),(187,'Saint-Kitts-et-Nevis','KN'),(188,'Sainte-Lucie','WL'),(189,'Saint-Martin (partie française)',''),(190,'Saint-Pierre-et-Miquelon','F'),(191,'Saint-Vincent-et-les Grenadines','WV'),(192,'Samoa','WS'),(193,'Saint-Marin','RSM'),(194,'Sao Tomé-et-Principe','ST'),(195,'Sercq',''),(196,'Arabie saoudite','SA'),(197,'Sénégal','SN'),(198,'Serbie','SRB'),(199,'Seychelles','SY'),(200,'Sierra Leone','WAL'),(201,'Singapour','SGP'),(202,'Saint-Martin (partie néerlandaise)',''),(203,'Slovaquie','SK'),(204,'Slovénie','SLO'),(205,'Îles Salomon','SB'),(206,'Somalie','SO'),(207,'Afrique du Sud','ZA'),(208,'Géorgie du Sud-et-les Îles Sandwich du Sud',''),(209,'Soudan du Sud',''),(210,'Espagne','E'),(211,'Sri Lanka','CL'),(212,'État de Palestine',''),(213,'Soudan','SUD'),(214,'Suriname','SME'),(215,'Îles Svalbard-et-Jan Mayen',''),(216,'Suède','S'),(217,'Suisse','CH'),(218,'République arabe syrienne','SYR'),(219,'Tadjikistan','TJ'),(220,'Thaïlande','T'),(221,'ex-République yougoslave de Macédoine','MK'),(222,'Timor-Leste','RI'),(223,'Togo','TG'),(224,'Tokélaou','NZ'),(225,'Tonga','TO'),(226,'Trinité-et-Tobago','TT'),(227,'Tunisie','TN'),(228,'Turquie','TR'),(229,'Turkménistan','TM'),(230,'Îles Turques-et-Caïques',''),(231,'Tuvalu','TV'),(232,'Ouganda','EAU'),(233,'Ukraine','UA'),(234,'Émirats arabes unis',''),(235,'Royaume-Uni de Grande-Bretagne et d\'Irlande du Nord','GB'),(236,'République-Unie de Tanzanie','EAT'),(237,'Îles mineures éloignées des États-Unis','USA'),(238,'Îles Vierges américaines','USA'),(239,'États-Unis d\'Amérique','USA'),(240,'Uruguay','ROU'),(241,'Ouzbékistan','UZ'),(242,'Vanuatu','VU'),(243,'Venezuela (République bolivarienne du)','YV'),(244,'Viet Nam','VN'),(245,'Îles Wallis-et-Futuna','F'),(246,'Sahara occidental',''),(247,'Yémen','YAR'),(248,'Zambie','Z'),(249,'Zimbabwe','ZW'),(250,'Îles d\'Åland','FIN');
/*!40000 ALTER TABLE `Country` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-04-28  8:40:08
