CREATE DATABASE IF NOT EXISTS `PenAndPaper` /*!40100 DEFAULT CHARACTER SET latin1 COLLATE latin1_general_cs */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `PenAndPaper`;

-- MySQL dump 10.13  Distrib 8.0.21, for Win64 (x86_64)
--
-- Host: localhost    Database: PenAndPaper
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
-- Table structure for table `FeaturesIn`
--

DROP TABLE IF EXISTS `FeaturesIn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `FeaturesIn` (
  `pkfk_adventure` int NOT NULL,
  `pkfk_creature` int NOT NULL,
  PRIMARY KEY (`pkfk_adventure`,`pkfk_creature`),
  KEY `FeaturesIn_Creature_idx` (`pkfk_creature`),
  CONSTRAINT `FeaturesIn_Adventure` FOREIGN KEY (`pkfk_adventure`) REFERENCES `Adventure` (`pk_adventure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FeaturesIn_Creature` FOREIGN KEY (`pkfk_creature`) REFERENCES `Creature` (`pk_creature`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `FeaturesIn`
--

LOCK TABLES `FeaturesIn` WRITE;
/*!40000 ALTER TABLE `FeaturesIn` DISABLE KEYS */;
INSERT INTO `FeaturesIn` VALUES (3,1),(2,2),(2,3),(2,4),(2,5),(3,5),(3,6),(3,7),(3,8),(3,9),(3,10),(0,11),(0,12),(0,13),(0,14),(1,15),(1,16),(1,17),(4,18),(4,19),(4,20),(4,21),(4,22),(6,23),(6,24),(6,25),(6,26),(6,27),(7,28),(7,29),(7,30),(7,31),(7,32),(8,33),(8,34),(8,35),(8,36),(8,37),(9,38),(9,39),(9,40),(9,41),(9,42),(9,43),(9,44),(9,45),(9,46),(10,47),(10,48),(10,49),(11,50),(11,51),(11,52),(11,53),(12,54),(12,55),(12,56),(13,57),(13,58),(13,59),(14,60),(14,61),(15,63),(15,64),(16,65),(16,66),(16,67),(16,68),(16,69);
/*!40000 ALTER TABLE `FeaturesIn` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Adventure`
--

DROP TABLE IF EXISTS `Adventure`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Adventure` (
  `pk_adventure` int NOT NULL,
  `fk_campaign_isPartOf` int NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `synopsis` longtext,
  PRIMARY KEY (`pk_adventure`),
  KEY `Adventure_Campaign_idx` (`fk_campaign_isPartOf`),
  CONSTRAINT `Adventure_Campaign` FOREIGN KEY (`fk_campaign_isPartOf`) REFERENCES `Campaign` (`pk_campaign`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Adventure`
--

LOCK TABLES `Adventure` WRITE;
/*!40000 ALTER TABLE `Adventure` DISABLE KEYS */;
INSERT INTO `Adventure` VALUES (0,0,'Entering the domain of the Wicche','The players find a worn journal detailing the location of a powerful Evil. It must be destroyed at any cost!'),(1,0,'The Fall of the Wicche','Having found the weakness of the Wicche, the players need to come up with a plan to banish the wichhe one and for all.'),(2,1,'Escaping Candlekeep','The players find themselves cast into the night with little preparation. Can they survive what lurks in the dark?'),(3,1,'The Mines of Nashkel','The players uncover a heinous plot. What will they do?'),(4,1,'Baldur\'s Gate awaits','The players face off with a powerful foe. Will they triumph?'),(5,1,'Murder!','Accused of murder, can the players prove their innocence?'),(6,2,'Greenest in Flames','Ambushed by a dragon, the players think it can\'t get any worse, only to disover their rest stop, the city of Greenest, besieged and in flames.'),(7,2,'Raider\'s Camp','The players are tasked by the duke of Greenest to rid the region of the raiders that besieged his city.'),(8,2,'On the Road','Travelling north, the adventurers accompany a caravan and unravel the secrets of its hosts.'),(9,2,'Castle Naerytar','Forgotten and overgrown, castle Naerytar lies in the moor. However, it is home to the next clue the adventurers need on their quest.'),(10,3,'Johnson, John Johnson','The players get a new job which promises many perks should they pull it off. But at what price?'),(11,3,'The origins of Paradise','The players uncover that the formula they are looking for isn\'t as innocent as they think!'),(12,4,'The Eye of Uldûr','A mysterious artifact is found in a peddler\'s inventory.'),(13,4,'A sacrifice is made','Strange magic requires strange ingredients. What will it take?'),(14,4,'Uldûr beckons','A place of power, a place of mystery. What will the Eye reveal?'),(15,5,'The Derelict','A long lost derelict is found in the Outer Rim. Scavangers race to pillage what they can. But can they handle what they find?'),(16,5,'A chance encounter','Some crafty troopers managed to survive on the derelict. Good for them, bad for you as they think the Clone Wars are still a thing!'),(17,5,'Hyperspace mysteries','Together with their new allies, our interpid heroes try to jump the derelict star destroyer to a safe haven. But hyperspace travel isn\'t easy and full of mysteries.');
/*!40000 ALTER TABLE `Adventure` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Adventurer`
--

DROP TABLE IF EXISTS `Adventurer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Adventurer` (
  `pk_adventurer` int NOT NULL AUTO_INCREMENT,
  `fk_player_plays` int NOT NULL,
  `fk_campaign_diesIn` int DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `firstName` varchar(45) DEFAULT NULL,
  `age` varchar(45) DEFAULT NULL,
  `race` varchar(45) DEFAULT NULL,
  `class` varchar(45) DEFAULT NULL,
  `level` int NOT NULL DEFAULT '1',
  `strength` int NOT NULL,
  `dexterity` int NOT NULL,
  `intelligence` int NOT NULL,
  `charisma` int NOT NULL,
  PRIMARY KEY (`pk_adventurer`),
  KEY `Adventurer_Player_idx` (`fk_player_plays`),
  KEY `Adventurer_Campaign_idx` (`fk_campaign_diesIn`),
  CONSTRAINT `Adventurer_Campaign` FOREIGN KEY (`fk_campaign_diesIn`) REFERENCES `Campaign` (`pk_campaign`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `Adventurer_Player` FOREIGN KEY (`fk_player_plays`) REFERENCES `Player` (`pk_ssn`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Adventurer`
--

LOCK TABLES `Adventurer` WRITE;
/*!40000 ALTER TABLE `Adventurer` DISABLE KEYS */;
INSERT INTO `Adventurer` VALUES (1,2,2,'Greyhaven','Fiona','124','Elf','Wizard',14,8,19,20,14),(2,3,2,'Pimbleton','Sam','16','Halfling','Rogue',15,12,22,16,14),(3,4,NULL,'Gramsh','Uk','14','Half-Orc','Fighter',8,18,12,10,8),(4,5,NULL,'Joyran','Sybil','26','Human','Bard',6,12,16,16,18),(5,6,NULL,'Sva\'ah','Celethor','358','Elf','Sorcerer',9,10,14,17,20),(6,7,NULL,'Bombadil','Tom','687','Celestial','Savant',24,10,12,24,20),(7,8,NULL,'Caster','Miro','16','Half-Elf','Warlock',7,8,12,14,18),(8,9,NULL,'Smash','Mulk','21','Mon Calamari','Fighter',6,20,12,8,8),(9,10,NULL,'Goldenglow','Simya','116','Elf','Cleric',12,11,12,13,18),(10,12,NULL,'Sother','Piet','38','Human','Cleric',6,15,12,11,15),(11,13,0,'Wormtongue','Grima','36','Human','Assassin',12,12,14,18,11),(12,14,NULL,'Claron','Jake','34','Troll','Decker',8,10,12,18,16),(13,15,NULL,'Silent','Bob','36','Human','Bard',15,10,12,16,20),(14,2,NULL,'Clearthorn','Mirielle','110','Elf','Ranger',3,11,18,14,14),(15,3,NULL,'Stronghammer','Baradin','89','Dwarf','Fighter',5,18,12,12,14),(16,1,NULL,'The Barbarian','Minsk','48','Human','Barbarian',18,21,16,11,16),(17,12,NULL,'Lighthearth','Qui\'ugon','52','Human','Jedi Master',16,18,16,16,14),(18,14,NULL,'Plagueis','Darth','496','Muun','Sith',35,28,26,32,24);
/*!40000 ALTER TABLE `Adventurer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Campaign`
--

DROP TABLE IF EXISTS `Campaign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Campaign` (
  `pk_campaign` int NOT NULL,
  `fk_player_masters` int NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `setting` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`pk_campaign`),
  KEY `Campaign_Player_idx` (`fk_player_masters`),
  CONSTRAINT `Campaign_Player` FOREIGN KEY (`fk_player_masters`) REFERENCES `Player` (`pk_ssn`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Campaign`
--

LOCK TABLES `Campaign` WRITE;
/*!40000 ALTER TABLE `Campaign` DISABLE KEYS */;
INSERT INTO `Campaign` VALUES (0,5,'Lands of the Dark Wicche','Call of Cthulhu'),(1,11,'Baldur\'s Gate','Forgotten Realms'),(2,11,'Hoard of the Dragon Queen','Forgotten Realms'),(3,1,'Paradise Lost','Shadowrun'),(4,16,'On Elven Shores','Lord of the Rings'),(5,14,'Full Force Ahead','Star Wars');
/*!40000 ALTER TABLE `Campaign` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Creature`
--

DROP TABLE IF EXISTS `Creature`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Creature` (
  `pk_creature` int NOT NULL,
  `fk_creature_serves_master` int DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `race` varchar(45) DEFAULT NULL,
  `strength` int NOT NULL DEFAULT '10',
  `dexterity` int NOT NULL DEFAULT '10',
  `intelligence` int NOT NULL DEFAULT '10',
  `charisma` int NOT NULL DEFAULT '10',
  `elite` tinyint NOT NULL DEFAULT '0',
  `experience` int NOT NULL DEFAULT '20',
  PRIMARY KEY (`pk_creature`),
  KEY `Creature_Self_idx` (`fk_creature_serves_master`),
  CONSTRAINT `Creature_Self` FOREIGN KEY (`fk_creature_serves_master`) REFERENCES `Creature` (`pk_creature`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Creature`
--

LOCK TABLES `Creature` WRITE;
/*!40000 ALTER TABLE `Creature` DISABLE KEYS */;
INSERT INTO `Creature` VALUES (1,NULL,'Gurk','Beholder',8,16,20,8,1,800),(2,1,'Brak','Goblin',8,12,8,6,0,100),(3,1,'Bik','Goblin',8,12,8,6,0,100),(4,1,'Bit','Goblin',8,12,8,6,0,100),(5,1,'Palik','Hobgoblin',12,14,10,8,1,250),(6,1,'Mok','Goblin',8,12,8,6,0,100),(7,1,'Min','Goblin',8,12,8,6,0,100),(8,1,'Kot','Goblin',8,12,8,6,0,100),(9,1,'Prak','Goblin',8,12,8,6,0,100),(10,1,'Vik','Hobgoblin',12,14,10,8,1,250),(11,15,'Vladyslav the Ancient','Vampire',105,50,65,65,1,1200),(12,11,'Catherine','Ghost',85,25,80,85,0,400),(13,11,'William','Ghost',85,25,80,85,0,400),(14,11,'Matthew','Ghost',85,25,80,85,0,400),(15,NULL,'Osterhildis','Witch',110,85,110,90,1,1800),(16,15,'Patty','Demon Goat',70,60,15,20,0,250),(17,15,'Pitty','Demon Goat',70,60,15,20,0,250),(18,22,'Sam','Bandit',16,11,8,9,0,200),(19,22,'Greg','Bandit',14,12,10,8,0,200),(20,22,'Timmy','Bandit',14,13,12,10,0,200),(21,22,'Felicy','Bandit',16,12,10,11,0,200),(22,NULL,'Thomas','Bandit Captain',18,16,14,12,1,500),(23,NULL,'Klik','Kobold',8,14,10,10,0,75),(24,NULL,'Klak','Kobold',8,14,10,10,0,75),(25,NULL,'Kluk','Kobold',8,14,10,10,0,75),(26,NULL,'Krack','Kobold',8,14,10,10,0,75),(27,NULL,'Kink','Kobold',8,14,10,10,0,75),(28,NULL,'Viktor','Cultist',13,14,13,10,0,300),(29,NULL,'Grolin','Cultist',11,15,12,11,0,300),(30,NULL,'Mark','Cultist',12,13,12,12,0,300),(31,NULL,'Michael','Bandit',14,12,10,8,0,200),(32,NULL,'Sandy','Bandit',13,13,11,9,0,200),(33,NULL,'Bert','Cultist',12,13,11,10,0,350),(34,NULL,'Wanda','Cultist',13,12,11,12,0,350),(35,NULL,'Perin','Cultist',14,10,10,10,0,350),(36,NULL,'Penny','Cultist',12,11,12,9,0,350),(37,NULL,'Misha','Rakshasa',14,18,22,18,1,1400),(38,NULL,'Bog','Giant Frog',16,16,3,6,0,300),(39,NULL,'Brug','Giant Frog',16,16,3,6,0,300),(40,NULL,'Manny','Bullywug',14,14,8,8,0,400),(41,NULL,'Mohg','Bullywug',14,14,8,8,0,400),(42,NULL,'Satash','Lizardfolk',12,16,10,9,0,450),(43,NULL,'Zortis','Lizardfolk',12,16,10,8,0,450),(44,46,'Brad','Cultist',12,14,11,10,0,350),(45,46,'Mike','Cultist',13,14,10,11,0,350),(46,NULL,'Rezmir','Dragonborn',16,18,20,16,1,1400),(47,NULL,'Trog','Troll',15,8,6,6,1,1100),(48,47,'Maria','Metahuman',8,12,14,12,0,600),(49,47,'Pendolin','Gnome',6,16,14,10,0,650),(50,NULL,'Yoshi','Yakuza Samurai',12,14,4,4,0,800),(51,NULL,'Ishi','Yakuza Shaman',14,9,6,9,0,900),(52,NULL,'Kenzai','Yakuza Samurai',12,14,4,4,0,800),(53,NULL,'Yonda','Yakuza Wizard',4,5,16,4,1,1200),(54,NULL,'Gruk','Orc',16,14,5,6,0,400),(55,NULL,'Ragor','Orc',15,15,6,5,0,400),(56,NULL,'Krak','Orc',16,13,7,6,0,400),(57,NULL,'Goldroth','Giant Spider',12,19,11,4,0,600),(58,NULL,'Krathiht','Giant Spider',12,18,10,3,0,600),(59,NULL,'Cheldroth','Giant Spider',12,18,9,4,0,600),(60,NULL,'Puk','Cave Troll',20,13,6,6,0,800),(61,NULL,'Pak','Cave Troll',22,14,8,6,1,1200),(62,NULL,'Darth Palgodir','Sith Lord',26,24,24,28,1,3400),(63,62,'P20D13','Imperial Probe Droid',3,16,16,3,0,400),(64,62,'P20D12','Imperial Probe Droid',3,16,16,3,0,400),(65,NULL,'Kit','Clone Commander',16,14,14,12,1,800),(66,65,'Benny','Clone Trooper',14,12,10,10,0,400),(67,65,'Killian','Clone Trooper',13,11,11,9,0,400),(68,65,'Ace','Clone Trooper',15,10,11,8,0,400),(69,65,'Portos','Clone Trooper',16,12,10,10,0,400);
/*!40000 ALTER TABLE `Creature` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Player`
--

DROP TABLE IF EXISTS `Player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Player` (
  `pk_ssn` int NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `firstName` varchar(45) NOT NULL,
  `birthday` date DEFAULT NULL,
  PRIMARY KEY (`pk_ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='	';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Player`
--

LOCK TABLES `Player` WRITE;
/*!40000 ALTER TABLE `Player` DISABLE KEYS */;
INSERT INTO `Player` VALUES (0,'Schmit','Bob','1992-13-11'),(1,'Miltgen','Fernand','1994-08-18'),(2,'Probst','Sam','1995-04-16'),(3,'Michels','Anne','1992-02-12'),(4,'Vingen','Sally','1993-03-30'),(5,'Schmidt','Pascale','1990-06-02'),(6,'Blesius','Marie','2002-11-12'),(7,'Arnaud','Pit','2000-11-23'),(8,'Onager','Michel','1998-09-02'),(9,'Marechal','Marc','1996-02-12'),(10,'Piets','Gwenaël','1986-010-16'),(11,'Mercer','Mike','1983-04-08'),(12,'Diesel','Vin','1976-01-01'),(13,'Favreau','Jon','1972-12-11'),(14,'Oswalt','Patton','1970-03-22'),(15,'Smith','Kevin','1971-02-14'),(16,'Colbert','Steven','1965-04-24');
/*!40000 ALTER TABLE `Player` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Location`
--

DROP TABLE IF EXISTS `Location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Location` (
  `pk_location` int NOT NULL AUTO_INCREMENT,
  `number` int NOT NULL,
  `street` varchar(45) NOT NULL,
  `postalCode` int NOT NULL,
  `town` varchar(45) NOT NULL,
  PRIMARY KEY (`pk_location`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Location`
--

LOCK TABLES `Location` WRITE;
/*!40000 ALTER TABLE `Location` DISABLE KEYS */;
INSERT INTO `Location` VALUES (1,48,'rue Ingolds',6456,'Hornbach'),(2,56,'Simmerhaf',7313,'Steinsel'),(3,11,'Grand Rue',4673,'Luxembourg'),(4,97,'rue Principale',4653,'Steinsel'),(5,126,'rue de la Sidérurgie',1479,'Dudelange'),(6,23,'um Monkeler',3216,'Vianden'),(7,54,'Schlassbierg',5252,'Brouch'),(8,44,'rue de la Gare',4625,'Steinsel');
/*!40000 ALTER TABLE `Location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Participate`
--

DROP TABLE IF EXISTS `Participate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Participate` (
  `pkfk_adventurer` int NOT NULL,
  `pkfk_campaign` int NOT NULL,
  PRIMARY KEY (`pkfk_adventurer`,`pkfk_campaign`),
  KEY `Participate_Campaign_idx` (`pkfk_campaign`),
  CONSTRAINT `Participate_Adventurer` FOREIGN KEY (`pkfk_adventurer`) REFERENCES `Adventurer` (`pk_adventurer`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `Participate_Campaign` FOREIGN KEY (`pkfk_campaign`) REFERENCES `Campaign` (`pk_campaign`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Participate`
--

LOCK TABLES `Participate` WRITE;
/*!40000 ALTER TABLE `Participate` DISABLE KEYS */;
INSERT INTO `Participate` VALUES (10,0),(11,0),(13,0),(1,1),(2,1),(3,1),(16,1),(1,2),(2,2),(3,2),(4,2),(14,2),(15,2),(16,2),(7,3),(12,3),(15,3),(5,4),(6,4),(9,4),(8,5),(17,5),(18,5);
/*!40000 ALTER TABLE `Participate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TakesPlaceAt`
--

DROP TABLE IF EXISTS `TakesPlaceAt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `TakesPlaceAt` (
  `pkfk_location` int NOT NULL,
  `pkfk_adventure` int NOT NULL,
  `pk_date` date NOT NULL,
  `startTime` int NOT NULL,
  PRIMARY KEY (`pkfk_location`,`pkfk_adventure`,`pk_date`),
  KEY `TakesPlaceAt_Aventure_idx` (`pkfk_adventure`),
  CONSTRAINT `TakesPlaceAt_Aventure` FOREIGN KEY (`pkfk_adventure`) REFERENCES `Aventure` (`pk_adventure`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `TakesPlaceAt_Location` FOREIGN KEY (`pkfk_location`) REFERENCES `Location` (`pk_location`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TakesPlaceAt`
--

LOCK TABLES `TakesPlaceAt` WRITE;
/*!40000 ALTER TABLE `TakesPlaceAt` DISABLE KEYS */;
INSERT INTO `TakesPlaceAt` VALUES (1,0,'2019-05-28',16),(1,1,'2019-06-04',16),(2,2,'2019-04-14',14),(2,3,'2019-05-07',14),(2,4,'2019-06-04',14),(2,5,'2019-07-19',14),(3,6,'2020-01-02',15),(3,8,'2020-03-04',17),(4,10,'2019-03-02',10),(4,11,'2019-03-09',10),(5,12,'2019-05-08',18),(5,14,'2019-05-22',18),(6,15,'2024-05-11',14),(6,16,'2024-06-12',14),(6,17,'2024-07-21',14),(7,13,'2019-05-15',18),(8,7,'2020-02-03',16),(8,9,'2020-05-09',12);
/*!40000 ALTER TABLE `TakesPlaceAt` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-02-14 14:53:11
