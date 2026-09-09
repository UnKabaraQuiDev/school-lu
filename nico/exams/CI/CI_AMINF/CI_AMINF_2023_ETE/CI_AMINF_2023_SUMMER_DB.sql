CREATE DATABASE  IF NOT EXISTS `fifa` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `fifa`;
-- MySQL dump 10.13  Distrib 8.0.30, for Win64 (x86_64)
--
-- Host: localhost    Database: fifa
-- ------------------------------------------------------
-- Server version	8.0.30

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
-- Table structure for table `Arbitre`
--

DROP TABLE IF EXISTS `Arbitre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Arbitre` (
  `idArbitre_PK` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(45) NOT NULL,
  `prénom` varchar(45) NOT NULL,
  `dateDeNaissance` datetime DEFAULT NULL,
  `idNationalité_attribuer_FK` int NOT NULL,
  PRIMARY KEY (`idArbitre_PK`),
  KEY `idNationalité_FK_idx` (`idNationalité_attribuer_FK`),
  CONSTRAINT `idNationalité_attribuer_FK` FOREIGN KEY (`idNationalité_attribuer_FK`) REFERENCES `Nationalité` (`idNationalité_PK`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Arbitre`
--

LOCK TABLES `Arbitre` WRITE;
/*!40000 ALTER TABLE `Arbitre` DISABLE KEYS */;
INSERT INTO `Arbitre` VALUES (1,'Ramos','César','1983-12-15 00:00:00',23),(2,'Sampaio','Wilton','1981-12-21 00:00:00',6),(3,'Barton','Iván','1991-01-27 00:00:00',11),(4,'Elfath','Ismail','1982-03-03 00:00:00',40),(5,'Marciniak','Szymon','1981-01-07 00:00:00',28),(6,'Mateu Lahoz','Antonio','1977-03-12 00:00:00',34),(7,'Oliver','Michael','1985-02-20 00:00:00',12),(8,'Orsato','Daniele','1975-11-23 00:00:00',18),(9,'Rapallini','Fernando','1978-04-28 00:00:00',4),(10,'Tello','Facundo','1982-05-04 00:00:00',4),(11,'Turpin','Clément','1982-05-16 00:00:00',13),(12,'Al Jassim','Abdulrahman','1987-10-14 00:00:00',21),(13,'Claus','Raphael','1979-09-06 00:00:00',6),(14,'Faghani','Alireza','1978-03-21 00:00:00',17),(15,'Ghorbal','Mustapha','1985-08-19 00:00:00',2),(16,'Gomes','Victor','1982-12-15 00:00:00',35),(17,'Makkelie','Danny','1983-01-28 00:00:00',26),(18,'Abdulla Hassan Mohamed','Mohammed','1978-12-02 00:00:00',41),(19,'Siebert','Daniel','1984-05-04 00:00:00',9),(20,'Taylor','Anthony','1978-10-20 00:00:00',12),(21,'Valenzuela','Jesús','1983-11-24 00:00:00',42),(22,'Vincic','Slavko','1979-11-25 00:00:00',33),(23,'Beath','Chris','1984-11-17 00:00:00',5),(24,'Conger','Matthew','1978-10-11 00:00:00',25),(25,'Escobar','Mario','1986-09-19 00:00:00',15),(26,'Frappart','Stéphanie','1983-12-14 00:00:00',13),(27,'Gassama','Bakary','1979-02-10 00:00:00',14),(28,'Matonte','Andrés','1988-03-30 00:00:00',39),(29,'Sikazwe','Janny','1979-05-26 00:00:00',31),(30,'Kovács','István','1984-09-16 00:00:00',30),(31,'Ma','Ning','1979-06-20 00:00:00',7),(32,'Martínez','Said','1991-08-07 00:00:00',16),(33,'Mukansanga','Salima','1988-07-25 00:00:00',29),(34,'N\'Diaye','Maguette','1986-09-01 00:00:00',32),(35,'Ortega','Kevin','1992-03-26 00:00:00',27),(36,'Yamashita','Yoshimi','1986-02-20 00:00:00',19),(37,'Boschilia','Bruno','1983-04-13 00:00:00',6),(38,'Hernández','Miguel','1977-06-18 00:00:00',23),(39,'Morín','Alberto','1980-08-10 00:00:00',23),(40,'Raphael Pires','Bruno','1985-09-20 00:00:00',6),(41,'Atkins','Kyle','1986-12-23 00:00:00',40),(42,'Pablo Belatti','Juan','1979-11-30 00:00:00',4),(43,'Beswick','Gary','1977-08-10 00:00:00',12),(44,'Bonfá','Diego','1977-12-04 00:00:00',4),(45,'Brailovsky','Ezequiel','1979-04-14 00:00:00',4),(46,'Burt','Stuart','1980-03-03 00:00:00',12),(47,'Carbone','Ciro','1978-07-24 00:00:00',18),(48,'Cebrián Devís','Pau','1979-05-15 00:00:00',34),(49,'Chade','Gabriel','1980-05-22 00:00:00',4),(50,'Danos','Nicolas','1980-09-27 00:00:00',13),(51,'Díaz Pérez','Roberto','1976-04-29 00:00:00',34),(52,'Giallatini','Alessandro','1975-07-04 00:00:00',18),(53,'Gringore','Cyril','1972-10-02 00:00:00',13),(54,'Listkiewicz','Tomasz','1978-10-06 00:00:00',28),(55,'Morán','David','1985-10-28 00:00:00',11),(56,'Parker','Corey','1986-03-29 00:00:00',40),(57,'Sokolnicki','Pawel','1981-01-07 00:00:00',28),(58,'Abolfazli','Mohammadreza','1977-07-14 00:00:00',17),(59,'Al Hammadi','Mohamed','1984-12-18 00:00:00',41),(60,'Al Mahri','Hasan','1978-02-08 00:00:00',41),(61,'Al Maqaleh','Saoud','1988-12-18 00:00:00',21),(62,'Al Marri','Taleb','1988-12-14 00:00:00',21),(63,'Bennett','Simon','1985-05-24 00:00:00',12),(64,'Correa','Rodrigo','1983-01-02 00:00:00',6),(65,'de Vries','Jan','1982-07-14 00:00:00',26),(66,'Etchali','Abdelhak','1981-06-27 00:00:00',2),(67,'Foltyn','Rafael','1985-06-21 00:00:00',9),(68,'Gourari','Mokrane','1982-01-04 00:00:00',2),(69,'Klan?nik','Tomaž','1982-11-30 00:00:00',33),(70,'Kova?i?','Andraž','1985-10-24 00:00:00',33),(71,'Manis','Danilo','1981-06-06 00:00:00',6),(72,'Mansouri','Mohammadreza','1978-04-23 00:00:00',17),(73,'Moreno','Tulio','1986-07-19 00:00:00',42),(74,'Nunn','Adam','1985-05-01 00:00:00',12),(75,'Phatsoane','Souro','1988-08-05 00:00:00',22),(76,'Seidel','Jan','1984-10-10 00:00:00',9),(77,'Siwela','Zakhele','1982-09-02 00:00:00',35),(78,'Steegstra','Hessel','1978-03-27 00:00:00',26),(79,'Urrego','Jorge','1981-10-09 00:00:00',42),(80,'Zeegelaar','Zachari','1989-10-03 00:00:00',36),(81,'Abo El Regal','Mahmoud','1984-02-02 00:00:00',1),(82,'Ines Back','Neuza','1984-08-11 00:00:00',6),(83,'Beecham','Ashley','1988-05-23 00:00:00',5),(84,'Díaz','Karen','1984-10-11 00:00:00',23),(85,'Dos Santos','Jerson','1983-05-01 00:00:00',3),(86,'Makasini','Tevita','1976-11-26 00:00:00',37),(87,'Marengula','Arsénio','1986-06-30 00:00:00',24),(88,'Carlos Mora','Juan','1989-08-05 00:00:00',8),(89,'Nesbitt','Kathryn','1988-11-07 00:00:00',40),(90,'Guy Noupue','Elvis','1983-07-25 00:00:00',20),(91,'Rule','Mark','1981-06-08 00:00:00',25),(92,'Shchetinin','Anton','1986-03-27 00:00:00',5),(93,'Soppi','Martín','1987-08-04 00:00:00',39),(94,'Taran','Nicolás','1980-08-27 00:00:00',39),(95,'Wales','Caleb','1988-08-31 00:00:00',38),(96,'Artene','Mihai','1977-10-04 00:00:00',30),(97,'Camara','Djibril','1983-08-20 00:00:00',32),(98,'Cao','Yi','1982-05-15 00:00:00',7),(99,'Feliz','Helpys','1989-06-06 00:00:00',10),(100,'López','Walter','1978-02-02 00:00:00',16),(101,'Marinescu','Vasile','1976-04-06 00:00:00',30),(102,'Orué','Michael','1985-05-01 00:00:00',27),(103,'Hadji Samba','El','1979-04-02 00:00:00',32),(104,'Sánchez','Jesús','1987-05-11 00:00:00',27),(105,'Shi','Xiang','1980-10-14 00:00:00',7);
/*!40000 ALTER TABLE `Arbitre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Assister`
--

DROP TABLE IF EXISTS `Assister`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Assister` (
  `idArbitre_Assister_PKFK` int NOT NULL,
  `idMatch_Assistere_PKFK` int NOT NULL,
  PRIMARY KEY (`idArbitre_Assister_PKFK`,`idMatch_Assistere_PKFK`),
  KEY `idMatch_FK_idx` (`idMatch_Assistere_PKFK`),
  CONSTRAINT `idArbitre_assister_FK` FOREIGN KEY (`idArbitre_Assister_PKFK`) REFERENCES `Arbitre` (`idArbitre_PK`),
  CONSTRAINT `idMatch_jouer_FK` FOREIGN KEY (`idMatch_Assistere_PKFK`) REFERENCES `MatchDeFoot` (`idMatch_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Assister`
--

LOCK TABLES `Assister` WRITE;
/*!40000 ALTER TABLE `Assister` DISABLE KEYS */;
INSERT INTO `Assister` VALUES (43,1),(46,1),(48,2),(51,2),(45,3),(49,3),(13,4),(37,4),(47,5),(52,5),(38,6),(39,6),(61,7),(62,7),(54,8),(57,8);
/*!40000 ALTER TABLE `Assister` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `But`
--

DROP TABLE IF EXISTS `But`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `But` (
  `idBut_PK` int NOT NULL AUTO_INCREMENT,
  `minuteDeJeu` int NOT NULL,
  `idMatch_Passer_FK` int NOT NULL,
  `idJoueur_Correspendre_FK` int NOT NULL,
  PRIMARY KEY (`idBut_PK`),
  KEY `idJoueur_Correspendre_FK_idx` (`idJoueur_Correspendre_FK`),
  KEY `idMatch_passer_FK` (`idMatch_Passer_FK`),
  CONSTRAINT `idJoueur_Correspendre_FK` FOREIGN KEY (`idJoueur_Correspendre_FK`) REFERENCES `Joueur` (`idJoueur_PK`),
  CONSTRAINT `idMatch_passer_FK` FOREIGN KEY (`idMatch_Passer_FK`) REFERENCES `MatchDeFoot` (`idMatch_PK`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `But`
--

LOCK TABLES `But` WRITE;
/*!40000 ALTER TABLE `But` DISABLE KEYS */;
INSERT INTO `But` VALUES (1,105,1,47),(2,117,1,129),(3,35,2,7),(4,73,2,26),(5,93,2,198),(6,101,2,198),(7,42,3,164),(8,17,4,95),(9,54,4,73),(10,78,4,99),(11,34,5,26),(12,39,5,22),(13,69,5,22),(14,5,6,84),(15,79,6,101),(16,7,7,109),(17,9,7,140),(18,42,7,128),(19,23,8,26),(20,36,8,15),(21,80,8,102),(22,81,8,102),(23,108,8,26),(24,118,8,102);
/*!40000 ALTER TABLE `But` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Joueur`
--

DROP TABLE IF EXISTS `Joueur`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Joueur` (
  `idJoueur_PK` int NOT NULL,
  `prénom` varchar(45) DEFAULT NULL,
  `nom` varchar(45) NOT NULL,
  `dateDeNaissance` varchar(45) DEFAULT NULL,
  `numéroMaillot` varchar(45) DEFAULT NULL,
  `club` varchar(45) DEFAULT NULL,
  `idÉquipeNationale_Appartenir_FK` int NOT NULL,
  `blessé` tinyint NOT NULL DEFAULT '0',
  `bloqué` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`idJoueur_PK`),
  KEY `idÉquipeNationale_Appertenir_FK_idx` (`idÉquipeNationale_Appartenir_FK`),
  CONSTRAINT `idÉquipeNationale_Appertenir_FK` FOREIGN KEY (`idÉquipeNationale_Appartenir_FK`) REFERENCES `ÉquipeNationale` (`idÉquipeNationale_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Joueur`
--

LOCK TABLES `Joueur` WRITE;
/*!40000 ALTER TABLE `Joueur` DISABLE KEYS */;
INSERT INTO `Joueur` VALUES (1,'Franco','Armani','','1','River Plate',1,0,0),(2,'Emiliano','Mart¡nez','','23','Aston Villa',1,0,0),(3,'Ger¢nimo','Rulli','','12','Villarreal CF',1,0,0),(4,'Marcos','Acu¤a','','8','Sevilla FC',1,1,0),(5,'Juan','Foyth','','2','Villarreal CF',1,0,0),(6,'Lisandro','Mart¡nez','','25','Manchester United',1,0,0),(7,'Nahuel','Molina','','26','Atl‚tico Madrid',1,0,0),(8,'Gonzalo','Montiel','','4','Sevilla FC',1,0,1),(9,'Nicol s','Otamendi','','19','SL Benfica',1,0,0),(10,'Germ n','Pezzella','','6','Betis Sevilla',1,0,0),(11,'Cristian','Romero','','13','Tottenham Hotspur',1,0,0),(12,'Nico','Tagliafico','','3','Olympique Lyon',1,0,0),(13,'Thiago','Almada','','16','Atlanta United FC',1,0,0),(14,'Rodrigo','De Paul','','7','Atl‚tico Madrid',1,0,0),(15,'Ángel','Di Maria','','11','Juventus Turin',1,0,0),(16,'Enzo','Fern ndez','','24','SL Benfica',1,0,0),(17,'Papu','G¢mez','','17','Sevilla FC',1,0,0),(18,'Alexis','Mac Allister','','20','Brighton & Hove Albion',1,0,0),(19,'Exequiel','Palacios','','14','Bayer Leverkusen',1,1,0),(20,'Leandro','Paredes','','5','Juventus Turin',1,0,0),(21,'Guido','Rodr¡guez','','18','Betis Sevilla',1,0,0),(22,'Julián','Álvarez','','9','Manchester City',1,0,0),(23,'µngel','Correa','','15','Atl‚tico Madrid',1,0,0),(24,'Paulo','Dybala','','21','AS Rom',1,0,0),(25,'Lautaro','Mart¡nez','','22','Inter Mailand',1,0,0),(26,'Lionel','Messi','','10','Paris Saint-Germain',1,0,0),(27,NULL,'Alisson','','1','Liverpool FC',4,0,0),(28,NULL,'Ederson','','23','Manchester City',4,0,1),(29,NULL,'Wéverton','','12','Palmeiras',4,0,0),(30,'Sandro','Alex','','6','Juventus Turin',4,0,0),(31,'Telles','Alex','','16','Sevilla FC',4,0,0),(32,NULL,'Bremer','','24','Juventus Turin',4,0,0),(33,'Alves','Dani','','13','Pumas UNAM',4,0,0),(34,NULL,'Danilo','','2','Juventus Turin',4,1,0),(35,'Militão','Eder','','14','Real Madrid',4,0,0),(36,NULL,'Marquinhos','','4','Paris Saint-Germain',4,0,0),(37,'Silva','Thiago','','3','Chelsea FC',4,0,0),(38,'Guimarães','Bruno','','17','Newcastle United',4,0,0),(39,NULL,'Casemiro','','5','Manchester United',4,0,0),(40,'Ribeiro','Éverton','','22','Flamengo RJ',4,0,0),(41,NULL,'Fabinho','','15','Liverpool FC',4,0,0),(42,NULL,'Fred','','8','Manchester United',4,0,0),(43,'Paquetá','Lucas','','7','West Ham United',4,0,0),(44,NULL,'Antony','','19','Manchester United',4,0,0),(45,'Jesus','Gabriel','','18','Arsenal FC',4,0,0),(46,'Martinelli','Gabriel','','26','Arsenal FC',4,0,0),(47,NULL,'Neymar','','10','Paris Saint-Germain',4,0,0),(48,NULL,'Pedro','','25','Flamengo RJ',4,0,0),(49,NULL,'Raphinha','','11','FC Barcelona',4,0,0),(50,NULL,'Richarlison','','9','Tottenham Hotspur',4,0,0),(51,NULL,'Rodrygo','','21','Real Madrid',4,0,0),(52,'Júnior','Vinícius','','20','Real Madrid',4,0,0),(53,'Jordan','Pickford','','1','Everton FC',9,0,0),(54,'Nick','Pope','','13','Newcastle United',9,0,0),(55,'Aaron','Ramsdale','','23','Arsenal FC',9,0,0),(56,'Trent','Alexander-Arnold','','18','Liverpool FC',9,0,0),(57,'Conor','Coady','','16','Everton FC',9,0,0),(58,'Eric','Dier','','15','Tottenham Hotspur',9,0,0),(59,'Harry','Maguire','','6','Manchester United',9,0,0),(60,'Luke','Shaw','','3','Manchester United',9,0,0),(61,'John','Stones','','5','Manchester City',9,0,0),(62,'Kieran','Trippier','','12','Newcastle United',9,0,0),(63,'Kyle','Walker','','2','Manchester City',9,0,0),(64,'Ben','White','','21','Arsenal FC',9,0,0),(65,'Jude','Bellingham','','22','Borussia Dortmund',9,0,0),(66,'Conor','Gallagher','','26','Chelsea FC',9,0,0),(67,'Jordan','Henderson','','8','Liverpool FC',9,0,0),(68,'Mason','Mount','','19','Chelsea FC',9,0,0),(69,'Kalvin','Phillips','','14','Manchester City',9,0,0),(70,'Declan','Rice','','4','West Ham United',9,0,0),(71,'Phil','Foden','','20','Manchester City',9,0,0),(72,'Jack','Grealish','','7','Manchester City',9,0,0),(73,'Harry','Kane','','9','Tottenham Hotspur',9,0,0),(74,'James','Maddison','','25','Leicester City',9,0,0),(75,'Marcus','Rashford','','11','Manchester United',9,0,0),(76,'Bukayo','Saka','','17','Arsenal FC',9,0,0),(77,'Raheem','Sterling','','10','Chelsea FC',9,0,0),(78,'Callum','Wilson','','24','Newcastle United',9,0,0),(79,'Alphonse','Aréola','','23','West Ham United',10,0,0),(80,'Hugo','Lloris','','1','Tottenham Hotspur',10,0,0),(81,'Steve','Mandanda','','16','Stade Rennes',10,0,0),(82,'Axel','Disasi','','3','AS Monaco',10,0,1),(83,'Lucas','Hernández','','21','FC Bayern München',10,0,0),(84,'Theo','Hernández','','22','AC Mailand',10,0,0),(85,'Ibrahima','Konaté','','24','Liverpool FC',10,0,0),(86,'Jules','Koundé','','5','FC Barcelona',10,0,0),(87,'Benjamin','Pavard','','2','FC Bayern München',10,0,0),(88,'William','Saliba','','17','Arsenal FC',10,0,0),(89,'Dayot','Upamecano','','18','FC Bayern München',10,0,0),(90,'Raphaël','Varane','','4','Manchester United',10,0,0),(91,'Eduardo','Camavinga','','25','Real Madrid',10,0,0),(92,'Youssouf','Fofana','','13','AS Monaco',10,0,0),(93,'Mattéo','Guendouzi','','6','Olympique Marseille',10,0,0),(94,'Adrien','Rabiot','','14','Juventus Turin',10,0,0),(95,'Aurélien','Tchouaméni','','8','Real Madrid',10,0,0),(96,'Jordan','Veretout','','15','Olympique Marseille',10,0,0),(97,'Kingsley','Coman','','20','FC Bayern München',10,0,0),(98,'Ousmane','Dembélé','','11','FC Barcelona',10,0,0),(99,'Olivier','Giroud','','9','AC Mailand',10,0,0),(100,'Antoine','Griezmann','','7','Atlético Madrid',10,0,0),(101,'Randal','Kolo','','12','Eintracht Frankfurt',10,0,0),(102,'Kylian','Mbappé','','10','Paris Saint-Germain',10,0,0),(103,'Marcus','Thuram','','26','Bor. Mönchengladbach',10,0,0),(104,'Ivo','Grbi?','','12','Atlético Madrid',17,0,0),(105,'Ivica','Ivuši?','','23','NK Osijek',17,0,0),(106,'Dominik','Livakovi?','','1','Dinamo Zagreb',17,0,0),(107,'Borna','Bariši?','','3','Rangers FC',17,0,0),(108,'Martin','Erli?','','5','Sassuolo Calcio',17,0,0),(109,'Joško','Gvardiol','','20','RB Leipzig',17,0,0),(110,'Josip','Juranovi?','','22','Celtic FC',17,0,0),(111,'Dejan','Lovren','','6','Zenit St. Petersburg',17,0,0),(112,'Borna','Sosa','','19','VfB Stuttgart',17,0,0),(113,'Josip','Stanišic','','2','FC Bayern München',17,0,0),(114,'Josip','Šutalo','','24','Dinamo Zagreb',17,0,0),(115,'Domagoj','Vida','','21','AEK Athen',17,1,0),(116,'Marcelo','Brozovic','','11','Inter Mailand',17,0,0),(117,'Kristijan','Jakic','','26','Eintracht Frankfurt',17,0,0),(118,'Mateo','Kovacic','','8','Chelsea FC',17,0,0),(119,'Lovro','Majer','','7','Stade Rennes',17,0,0),(120,'Luka','Modric','','10','Real Madrid',17,0,0),(121,'Mario','Pašalic','','15','Atalanta',17,0,0),(122,'Ivan','Perišic','','4','Tottenham Hotspur',17,0,0),(123,'Luka','Sucic','','25','RB Salzburg',17,0,0),(124,'Nikola','Vlašic','','13','FC Turin',17,0,0),(125,'Ante','Budimir','','17','CA Osasuna',17,0,0),(126,'Andrej','Kramaric','','9','1899 Hoffenheim',17,0,0),(127,'Marko','Livaja','','14','Hajduk Split',17,0,1),(128,'Mislav','Oršic','','18','Dinamo Zagreb',17,0,0),(129,'Bruno','Petkovic','','16','Dinamo Zagreb',17,0,0),(130,'Bono','','','1','Sevilla FC',18,0,0),(131,'Munir','','','2','Al Wehda',18,0,0),(132,'Ahmed','Tagnaouti','','3','Wydad AC',18,0,0),(133,'Anas','Zniti','','4','Raja Casablanca',18,0,0),(134,'Nayef','Aguerd','','5','West Ham United',18,0,0),(135,'Sofiane','Alakouch','','6','FC Metz',18,0,0),(136,'Yahia','Attiat-Allah','','7','Wydad AC',18,0,0),(137,'Badr','Benoun','','8','Qatar SC',18,0,0),(138,'Soufiane','Chakla','','9','Oud-Heverlee Leuven',18,0,0),(139,'Mohamed','Chibi','','10','Pyramids FC',18,0,0),(140,'Achraf','Dari','','11','Stade Brest',18,0,0),(141,'Jawad','El-Yamiq','','12','Real Valladolid',18,0,0),(142,'Achraf','Hakimi','','13','Paris Saint-Germain',18,0,0),(143,'Adam','Masina','','14','Udinese Calcio',18,0,0),(144,'Noussair','Mazraoui','','15','FC Bayern München',18,0,0),(145,'Samy','Mmaee','','16','Ferencvaros Budapest',18,0,0),(146,'Fahd','Moufi','','17','Portimonense SC',18,0,0),(147,'Romain','Saïss','','18','Besiktas',18,0,0),(148,'Selim','Amallah','','19','Standard Lüttich',18,0,0),(149,'Sofyan','Amrabat','','20','AC Florenz',18,0,0),(150,'Aymane','Barkok','','21','1. FSV Mainz 05',18,0,0),(151,'Younès','Belhanda','','22','Adana Demirspor',18,0,0),(152,'Sofiane','Boufal','','23','Angers SCO',18,0,0),(153,'Ilias','Chair','','24','Queens Park Rangers',18,0,0),(154,'Bilal','El Khannouss','','25','KRC Genk',18,0,0),(155,'Fayçal','Fajr','','26','Al Wehda',18,0,0),(156,'Amine','Harit','','27','Olympique Marseille',18,0,0),(157,'Yahya','Jabrane','','28','Wydad AC',18,0,0),(158,'Azzedine','Ounahi','','29','Angers SCO',18,0,0),(159,'Abdelhamid','Sabiri','','30','Sampdoria',18,0,0),(160,'Adel','Taarabt','','31','Al Nasr SC',18,0,0),(161,'Zakaria','Aboukhlal','','32','Toulouse FC',18,0,0),(162,'Walid','Cheddira','','33','SSC Bari',18,0,0),(163,'Ayoub','El Kaabi Khannouss','','34','Hatayspor',18,0,0),(164,'Youssef','En-Nesyri','','35','Sevilla FC',18,0,0),(165,'Abde','Ezzalzouli','','36','CA Osasuna',18,0,0),(166,'Abderrazak','Hamdallah','','37','Al Ittihad',18,0,0),(167,'Ryan','Mmaee','','38','Ferencvaros Budapest',18,0,0),(168,'Munir','','','39','Getafe CF',18,0,0),(169,'Soufiane','Rahimi','','40','Al Ain FC',18,0,0),(170,'Tarik','Tissoudali','','41','KAA Gent',18,0,0),(171,'Anass','Zaroury','','42','Burnley FC',18,0,0),(172,'Hakim','Ziyech','','43','Chelsea FC',18,0,0),(173,'Justin','Bijlow','','13','Feyenoord Rotterdam',20,0,0),(174,'Andries','Noppert','','23','Sc Heerenveen',20,0,0),(175,'Remko','Pasveer','','1','Ajax Amsterdam',20,0,0),(176,'Nathan','Aké','','5','Manchester City',20,0,0),(177,'Daley','Blind','','17','Ajax Amsterdam',20,0,0),(178,'Matthijs','de Ligt','','3','FC Bayern München',20,0,0),(179,'Stefan','de Vrij','','6','Inter Mailand',20,0,0),(180,'Denzel','Dumfries','','22','Inter Mailand',20,0,0),(181,'Jeremie','Frimpong','','26','Bayer Leverkusen',20,0,0),(182,'Tyrell','Malacia','','16','Manchester United',20,0,0),(183,'Jurriën','Timber','','2','Ajax Amsterdam',20,0,0),(184,'Virgil','van Dijk','','4','Liverpool FC',20,0,0),(185,'Steven','Berghuis','','11','Ajax Amsterdam',20,0,0),(186,'Frenkie','de Jong','','21','FC Barcelona',20,0,0),(187,'Marten','de Roon','','15','Atalanta',20,0,0),(188,'Davy','Klaassen','','14','Ajax Amsterdam',20,0,0),(189,'Teun','Koopmeiners','','20','Atalanta',20,0,0),(190,'Xavi','Simons','','25','PSV Eindhoven',20,0,0),(191,'Kenneth','Taylor','','24','Ajax Amsterdam',20,0,0),(192,'Steven','Bergwijn','','7','Ajax Amsterdam',20,0,0),(193,'Luuk','de Jong','','9','PSV Eindhoven',20,0,0),(194,'Memphis','Depay','','10','FC Barcelona',20,0,0),(195,'Cody','Gakpo','','8','PSV Eindhoven',20,0,0),(196,'Vincent','Janssen','','18','Royal Antwerp FC',20,1,0),(197,'Noa','Lang','','12','FC Brügge',20,0,0),(198,'Wout','Weghorst','','19','Besiktas',20,0,0),(199,'Diogo','Costa','','22','FC Porto',22,0,0),(200,'José','Sá','','12','Wolverhampton Wanderers',22,0,0),(201,'Rui','Patrício','','1','AS Rom',22,0,0),(202,'António','Silva','','24','SL Benfica',22,0,0),(203,'Diogo','Dalot','','2','Manchester United',22,0,0),(204,'Raphaël','Guerreiro','','5','Borussia Dortmund',22,0,0),(205,'João','Cancelo','','20','Manchester City',22,0,0),(206,'Nuno','Mendes','','19','Paris Saint-Germain',22,0,0),(207,'Pepe','','','3','FC Porto',22,0,0),(208,'Rúben','Dias','','4','Manchester City',22,0,0),(209,'Bernardo','Silva','','10','Manchester City',22,0,0),(210,'Bruno','Fernandes','','8','Manchester United',22,0,0),(211,'Danilo','','','13','Paris Saint-Germain',22,0,0),(212,'João','Mário','','17','SL Benfica',22,0,0),(213,'João','Palhinha','','6','Fulham FC',22,0,0),(214,'Matheus','Nunes','','23','Wolverhampton Wanderers',22,0,0),(215,'Otávio','','','25','FC Porto',22,0,0),(216,'Rúben','Neves','','18','Wolverhampton Wanderers',22,0,0),(217,'Vitinha','','','16','Paris Saint-Germain',22,0,0),(218,'William','Carvalho','','14','Betis Sevilla',22,0,0),(219,'André','Silva','','9','RB Leipzig',22,0,0),(220,'Cristiano','Ronaldo','','7','',22,0,0),(221,'Gonçalo','Ramos','','26','SL Benfica',22,0,0),(222,'João','Félix','','11','Atlético Madrid',22,1,0),(223,'Rafael','Leão','','15','AC Mailand',22,0,0),(224,'Ricardo','Horta','','21','SC Braga',22,0,0);
/*!40000 ALTER TABLE `Joueur` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MatchDeFoot`
--

DROP TABLE IF EXISTS `MatchDeFoot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MatchDeFoot` (
  `idMatch_PK` int NOT NULL AUTO_INCREMENT,
  `dateEtHeures` datetime DEFAULT NULL,
  `idStade_Jouer_FK` int DEFAULT NULL,
  `idArbitre_insister_FK` int DEFAULT NULL,
  `idÉquipeNationale_Participer1_FK` int NOT NULL,
  `idÉquipeNationale_Participer2_FK` int NOT NULL,
  PRIMARY KEY (`idMatch_PK`),
  KEY `idStade_FK_idx` (`idStade_Jouer_FK`),
  KEY `idArbitre_FK_idx` (`idArbitre_insister_FK`),
  KEY `idÉquipeNationale1_FK_idx` (`idÉquipeNationale_Participer1_FK`),
  KEY `idÉquipeNationale2_FK_idx` (`idÉquipeNationale_Participer2_FK`),
  KEY `idStade_jouer_FK_idx` (`idStade_Jouer_FK`),
  KEY `idArbitre_insister_FK_idx` (`idArbitre_insister_FK`),
  KEY `idÉquipeNationale1_Participer1_FK_idx` (`idÉquipeNationale_Participer1_FK`) /*!80000 INVISIBLE */,
  KEY `idÉquipeNationale2_Participer2_FK_idx` (`idÉquipeNationale_Participer2_FK`),
  CONSTRAINT `idArbitre_insister_FK` FOREIGN KEY (`idArbitre_insister_FK`) REFERENCES `Arbitre` (`idArbitre_PK`),
  CONSTRAINT `idStade_Jouer_FK` FOREIGN KEY (`idStade_Jouer_FK`) REFERENCES `Stade` (`idStade_PK`),
  CONSTRAINT `idÉquipeNationale1_FK` FOREIGN KEY (`idÉquipeNationale_Participer1_FK`) REFERENCES `ÉquipeNationale` (`idÉquipeNationale_PK`),
  CONSTRAINT `idÉquipeNationale2_FK` FOREIGN KEY (`idÉquipeNationale_Participer2_FK`) REFERENCES `ÉquipeNationale` (`idÉquipeNationale_PK`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MatchDeFoot`
--

LOCK TABLES `MatchDeFoot` WRITE;
/*!40000 ALTER TABLE `MatchDeFoot` DISABLE KEYS */;
INSERT INTO `MatchDeFoot` VALUES (1,'2022-12-09 16:00:00',2,7,17,4),(2,'2022-12-09 20:00:00',9,6,20,1),(3,'2022-12-10 16:00:00',3,10,18,22),(4,'2022-12-10 20:00:00',1,2,9,10),(5,'2022-12-13 20:00:00',9,8,1,17),(6,'2022-12-13 20:00:00',1,1,10,18),(7,'2022-12-17 16:00:00',5,12,17,18),(8,'2022-12-18 16:00:00',9,5,1,10);
/*!40000 ALTER TABLE `MatchDeFoot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Nationalité`
--

DROP TABLE IF EXISTS `Nationalité`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Nationalité` (
  `idNationalité_PK` int NOT NULL AUTO_INCREMENT,
  `libellé` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idNationalité_PK`),
  UNIQUE KEY `libellé_UNIQUE` (`libellé`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Nationalité`
--

LOCK TABLES `Nationalité` WRITE;
/*!40000 ALTER TABLE `Nationalité` DISABLE KEYS */;
INSERT INTO `Nationalité` VALUES (1,'Ägypten'),(2,'Algerien'),(3,'Angola'),(4,'Argentinien'),(5,'Australien'),(6,'Brasilien'),(7,'China'),(8,'Costa Rica'),(9,'Deutschland'),(10,'Dom. Republik'),(11,'El Salvador'),(12,'England'),(13,'Frankreich'),(14,'Gambia'),(15,'Guatemala'),(16,'Honduras'),(17,'Iran'),(18,'Italien'),(19,'Japan'),(20,'Kamerun'),(21,'Katar'),(22,'Lesotho'),(23,'Mexiko'),(24,'Mosambik'),(25,'Neuseeland'),(26,'Niederlande'),(27,'Peru'),(28,'Polen'),(29,'Ruanda'),(30,'Rumänien'),(31,'Sambia'),(32,'Sénégal'),(33,'Slowenien'),(34,'Spanien'),(35,'Südafrika'),(36,'Suriname'),(37,'Tonga'),(38,'Trinidad & Tobago'),(39,'Uruguay'),(40,'USA'),(41,'VA Emirate'),(42,'Venezuela');
/*!40000 ALTER TABLE `Nationalité` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Stade`
--

DROP TABLE IF EXISTS `Stade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Stade` (
  `idStade_PK` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(45) NOT NULL,
  `lieu` varchar(45) NOT NULL,
  `capacité` int NOT NULL,
  PRIMARY KEY (`idStade_PK`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Stade`
--

LOCK TABLES `Stade` WRITE;
/*!40000 ALTER TABLE `Stade` DISABLE KEYS */;
INSERT INTO `Stade` VALUES (1,'Al-Bayt Stadium','Al-Khor',68895),(2,'Education City Stadium','Al-Rayyan',44667),(3,'Al-Thumama Stadium','Doha',44400),(5,'Khalifa International Stadium','Al-Rayyan',45857),(6,'Ahmad-bin-Ali-Stadion','Al-Rayyan',45032),(7,'Al-Janoub Stadium','Al-Wakra',44325),(8,'Stadium 974','Ras Abu Aboud',44089),(9,'Lusail Stadium','Lusail City - Doha',88966);
/*!40000 ALTER TABLE `Stade` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ÉquipeNationale`
--

DROP TABLE IF EXISTS `ÉquipeNationale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ÉquipeNationale` (
  `idÉquipeNationale_PK` int NOT NULL,
  `pays` varchar(45) NOT NULL,
  `nombreDeTitreCoupeDuMonde` int NOT NULL DEFAULT '0',
  `année` int NOT NULL DEFAULT '2022',
  PRIMARY KEY (`idÉquipeNationale_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ÉquipeNationale`
--

LOCK TABLES `ÉquipeNationale` WRITE;
/*!40000 ALTER TABLE `ÉquipeNationale` DISABLE KEYS */;
INSERT INTO `ÉquipeNationale` VALUES (1,'Argentinien',0,2022),(2,'Australien',0,2022),(3,'Belgien',0,2022),(4,'Brasilien',0,2022),(5,'Costa Rica',0,2022),(6,'Dänemark',0,2022),(7,'Deutschland',0,2022),(8,'Ecuador',0,2022),(9,'England',0,2022),(10,'Frankreich',0,2022),(11,'Ghana',0,2022),(12,'Iran',0,2022),(13,'Japan',0,2022),(14,'Kamerun',0,2022),(15,'Kanada',0,2022),(16,'Katar',0,2022),(17,'Kroatien',0,2022),(18,'Marokko',0,2022),(19,'Mexiko',0,2022),(20,'Niederlande',0,2022),(21,'Polen',0,2022),(22,'Portugal',0,2022),(23,'Saudi-Arabien',0,2022),(24,'Schweiz',0,2022),(25,'Senegal',0,2022),(26,'Serbien',0,2022),(27,'Spanien',0,2022),(28,'Südkorea',0,2022),(29,'Tunesien',0,2022),(30,'Uruguay',0,2022),(31,'USA',0,2022),(32,'Wales',0,2022);
/*!40000 ALTER TABLE `ÉquipeNationale` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-02-14 11:57:07
