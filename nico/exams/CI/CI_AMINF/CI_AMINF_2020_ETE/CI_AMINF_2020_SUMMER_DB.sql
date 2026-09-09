CREATE DATABASE  IF NOT EXISTS `AMINF_Question3`;
USE `AMINF_Question3`;

-- ---------------------------------------------------------------------------------------

--
-- Table structure for table `Aéroport`
--
CREATE TABLE `Aéroport` (
  `idAéroport` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(10) NOT NULL,
  `nom` varchar(45) NOT NULL,
  PRIMARY KEY (`idAéroport`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Aéroport`
--
LOCK TABLES `Aéroport` WRITE;
/*!40000 ALTER TABLE `Aéroport` DISABLE KEYS */;
INSERT INTO `Aéroport` VALUES (1,'FRCDG','Aéroport Charles de Gaulle'),(2,'DEFAM','Flughafen Frankfurt am Main'),(3,'DEMUN','Flughafen München'),(4,'FRORL','Aéroport d\'Orly'),(5,'LULUX','Aéroport de Luxembourg'),(6,'GPCIT','London City Airport');
/*!40000 ALTER TABLE `Aéroport` ENABLE KEYS */;
UNLOCK TABLES;

-- ---------------------------------------------------------------------------------------

--
-- Table structure for table `RaisonAnnulation`
--
CREATE TABLE `RaisonAnnulation` (
  `idRaisonAnnulation` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(10) NOT NULL,
  `libellé` varchar(45) NOT NULL,
  `description` varchar(450) DEFAULT NULL,
  PRIMARY KEY (`idRaisonAnnulation`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `RaisonAnnulation`
--
LOCK TABLES `RaisonAnnulation` WRITE;
/*!40000 ALTER TABLE `RaisonAnnulation` DISABLE KEYS */;
INSERT INTO `RaisonAnnulation` VALUES (1,'IW','Météo inclémente',NULL),(2,'SE','Sécurité',NULL),(3,'MI','Problème mécanique',NULL),(4,'CG','Problème ordinateur',NULL),(5,'MA','Avion manquant',NULL),(6,'MC','Equipage manquant',NULL),(7,'FO','Brouillard',NULL),(8,'RA','Pluie',NULL),(9,'SN','Neige',NULL),(10,'HA','Grêle',NULL);
/*!40000 ALTER TABLE `RaisonAnnulation` ENABLE KEYS */;
UNLOCK TABLES;

-- ---------------------------------------------------------------------------------------

--
-- Table structure for table `Constructeur`
--
CREATE TABLE `Constructeur` (
  `idConstructeur` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(20) NOT NULL,
  `nom` varchar(45) NOT NULL,
  PRIMARY KEY (`idConstructeur`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Constructeur`
--
LOCK TABLES `Constructeur` WRITE;
/*!40000 ALTER TABLE `Constructeur` DISABLE KEYS */;
INSERT INTO `Constructeur` VALUES (1,'BO','Boeing'),(2,'DH','De Havilland Aircraft of Canada'),(3,'BA','Bombardier Aviation');
/*!40000 ALTER TABLE `Constructeur` ENABLE KEYS */;
UNLOCK TABLES;

-- ---------------------------------------------------------------------------------------

--
-- Table structure for table `Pays`
--
CREATE TABLE `Pays` (
  `iso2code` varchar(2) NOT NULL,
  `nom` varchar(45) NOT NULL,
  PRIMARY KEY (`iso2code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Pays`
--
LOCK TABLES `Pays` WRITE;
/*!40000 ALTER TABLE `Pays` DISABLE KEYS */;
INSERT INTO `Pays` VALUES ('DE','Deutschland'),('FR','France'),('LU','Luxembourg'),('UK','Great Britain');
/*!40000 ALTER TABLE `Pays` ENABLE KEYS */;
UNLOCK TABLES;

-- ---------------------------------------------------------------------------------------

--
-- Table structure for table `TypeAvion`
--
CREATE TABLE `TypeAvion` (
  `idTypeAvion` int(11) NOT NULL AUTO_INCREMENT,
  `libellé` varchar(45) NOT NULL,
  `distance` int(11) NOT NULL,
  `longueur` decimal(2,0) NOT NULL,
  `hauteur` decimal(2,0) NOT NULL,
  `envergure` decimal(2,0) NOT NULL,
  `vitesseMaximale` int(11) NOT NULL,
  `volumeCarburant` int(11) NOT NULL,
  PRIMARY KEY (`idTypeAvion`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `TypeAvion`
--
LOCK TABLES `TypeAvion` WRITE;
/*!40000 ALTER TABLE `TypeAvion` DISABLE KEYS */;
INSERT INTO `TypeAvion` VALUES (1,'Boeing 737-700',5570,34,13,36,834,26022),(2,'De Havilland Q400',2522,33,8,28,667,6526),(3,'Boeing 737-800',5436,39,13,36,842,5436),(4,'Airbus A380',15700,72,24,56,901,320000);
/*!40000 ALTER TABLE `TypeAvion` ENABLE KEYS */;
UNLOCK TABLES;

-- ---------------------------------------------------------------------------------------

--
-- Table structure for table `Construire`
--
CREATE TABLE `Construire` (
  `fiTypeAvion` int(11) NOT NULL,
  `fiConstructeur` int(11) NOT NULL,
  PRIMARY KEY (`fiTypeAvion`,`fiConstructeur`),
  KEY `FK_BuildsManufacturer_idx` (`fiConstructeur`),
  CONSTRAINT `FK_Construire_Constructeur` FOREIGN KEY (`fiConstructeur`) REFERENCES `constructeur` (`idConstructeur`),
  CONSTRAINT `FK_Construire_TypeAvion` FOREIGN KEY (`fiTypeAvion`) REFERENCES `typeavion` (`idTypeAvion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Construire`
--
LOCK TABLES `Construire` WRITE;
/*!40000 ALTER TABLE `Construire` DISABLE KEYS */;
INSERT INTO `Construire` VALUES (1,1),(3,1),(2,2),(2,3);
/*!40000 ALTER TABLE `Construire` ENABLE KEYS */;
UNLOCK TABLES;

-- ---------------------------------------------------------------------------------------

--
-- Table structure for table `CompanieAérienne`
--
CREATE TABLE `CompanieAérienne` (
  `idCompanieAérienne` int(11) NOT NULL AUTO_INCREMENT,
  `nom` varchar(45) NOT NULL,
  `fiPays` varchar(2) NOT NULL,
  PRIMARY KEY (`idCompanieAérienne`),
  KEY `FK_AirlineCountry_idx` (`fiPays`),
  CONSTRAINT `FK_CompanieAérienne_Pays` FOREIGN KEY (`fiPays`) REFERENCES `pays` (`iso2code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `CompanieAérienne`
--
LOCK TABLES `CompanieAérienne` WRITE;
/*!40000 ALTER TABLE `CompanieAérienne` DISABLE KEYS */;
INSERT INTO `CompanieAérienne` VALUES (1,'Luxair','LU'),(2,'Cargolux','LU'),(3,'Air France','FR'),(4,'Lufthansa','DE');
/*!40000 ALTER TABLE `CompanieAérienne` ENABLE KEYS */;
UNLOCK TABLES;

-- ---------------------------------------------------------------------------------------

--
-- Table structure for table `Avion`
--
CREATE TABLE `Avion` (
  `idAvion` int(11) NOT NULL AUTO_INCREMENT,
  `noSérie` varchar(20) NOT NULL,
  `noAileron` varchar(10) NOT NULL,
  `nom` varchar(45) NOT NULL,
  `dateFabrication` date NOT NULL,
  `capacité` int(11) NOT NULL,
  `fiCompanieAérienne` int(11) NOT NULL,
  `fiTypeAvion` int(11) NOT NULL,
  PRIMARY KEY (`idAvion`),
  KEY `FKAircraftAirline_idx` (`fiCompanieAérienne`),
  KEY `FK_AircraftModelAircraft_idx` (`fiTypeAvion`),
  CONSTRAINT `FK_Avion_CompanieAérienne` FOREIGN KEY (`fiCompanieAérienne`) REFERENCES `companieaérienne` (`idCompanieAérienne`),
  CONSTRAINT `FK_Avion_TypeAvion` FOREIGN KEY (`fiTypeAvion`) REFERENCES `typeavion` (`idTypeAvion`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;


--
-- Dumping data for table `Avion`
--
LOCK TABLES `Avion` WRITE;
/*!40000 ALTER TABLE `Avion` DISABLE KEYS */;
INSERT INTO `Avion` VALUES (1,'66524104','LG-6652','Château de Vianden','2008-05-01',79,1,2),(2,'38515238','LG-3851','Château de Bourscheid','2010-08-01',230,1,1),(3,'99484317','FA-9948','Victor Hugo','2011-02-15',136,3,1),(4,'58019371','LH-5801','Berlin','2010-05-30',428,4,3);
/*!40000 ALTER TABLE `Avion` ENABLE KEYS */;
UNLOCK TABLES;

-- ---------------------------------------------------------------------------------------

--
-- Table structure for table `Vol`
--
CREATE TABLE `Vol` (
  `idVol` int(11) NOT NULL AUTO_INCREMENT,
  `numéro` varchar(45) NOT NULL,
  `dateHeureDépartPrévue` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `dateHeureDépartRéelle` datetime DEFAULT NULL,
  `fuseauHorairDépart` int(11) NOT NULL DEFAULT '1',
  `dateHeureArrivéePrévue` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `dateHeureArrivéeRéelle` datetime DEFAULT NULL,
  `fuseauHorairArrivée` int(11) NOT NULL DEFAULT '1',
  `distance` decimal(5,1) NOT NULL DEFAULT '100.0',
  `taxiIn` int(11) NOT NULL DEFAULT '8',
  `taxiOut` int(11) NOT NULL DEFAULT '22',
  `estAnnulé` tinyint(4) NOT NULL DEFAULT '0',
  `fiAvion` int(11) NOT NULL,
  `fiAéroportDécoller` int(11) NOT NULL,
  `fiAéroportAtterrir` int(11) NOT NULL,
  PRIMARY KEY (`idVol`),
  KEY `FK_FlightAircraft_idx` (`fiAvion`),
  KEY `FKFlightTravelsFrom_idx` (`fiAéroportDécoller`),
  KEY `FK_FlightTravelsTo_idx` (`fiAéroportAtterrir`),
  CONSTRAINT `FK_Vol_Avion` FOREIGN KEY (`fiAvion`) REFERENCES `avion` (`idAvion`),
  CONSTRAINT `FK_Vol_AéroportDécoller` FOREIGN KEY (`fiAéroportDécoller`) REFERENCES `aéroport` (`idAéroport`),
  CONSTRAINT `FK_Vol_AéroportAtterrir` FOREIGN KEY (`fiAéroportAtterrir`) REFERENCES `aéroport` (`idAéroport`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Vol`
--
LOCK TABLES `Vol` WRITE;
/*!40000 ALTER TABLE `Vol` DISABLE KEYS */;
INSERT INTO `Vol` VALUES (1,'111','2020-03-27 10:51:00','2020-03-27 10:55:21',1,'2020-03-27 11:55:00','2020-03-27 11:59:34',1,273.0,8,22,0,1,1,5),(2,'235','2020-03-27 13:05:00','2020-03-27 13:14:41',1,'2020-03-27 14:00:00','2020-03-27 14:00:00',1,273.0,22,8,0,1,5,1),(3,'444','2020-03-27 11:05:00','2020-03-27 11:07:00',1,'2020-03-27 11:00:00','2020-03-27 11:05:00',0,484.9,8,22,0,2,5,6),(4,'478','2020-03-27 11:45:00','2020-03-27 11:50:00',0,'2020-03-27 13:55:00','2020-03-27 13:59:44',1,484.9,22,8,0,2,6,5),(5,'521','2020-03-27 12:05:00',NULL,1,'2020-03-27 12:00:00',NULL,0,484.9,8,22,1,3,5,6),(6,'188','2020-03-27 15:00:00','2020-03-27 14:59:25',1,'2020-03-27 15:50:00','2020-03-27 15:48:00',1,273.0,8,22,0,1,1,5),(7,'571','2020-03-12 11:22:45',NULL,1,'2020-03-27 13:00:00',NULL,1,304.0,12,24,1,4,2,3);
/*!40000 ALTER TABLE `Vol` ENABLE KEYS */;
UNLOCK TABLES;

-- ---------------------------------------------------------------------------------------

--
-- Table structure for table `Annuler`
--
CREATE TABLE `Annuler` (
  `fiVol` int(11) NOT NULL,
  `fiRaisonAnnulation` int(11) NOT NULL,
  PRIMARY KEY (`fiVol`,`fiRaisonAnnulation`),
  KEY `FK_ReasonsCancellationReason_idx` (`fiRaisonAnnulation`),
  CONSTRAINT `FK_Annuler_RaisonAnnulation` FOREIGN KEY (`fiRaisonAnnulation`) REFERENCES `raisonannulation` (`idRaisonAnnulation`),
  CONSTRAINT `FK_Annuler_Vol` FOREIGN KEY (`fiVol`) REFERENCES `vol` (`idVol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Annuler`
--
LOCK TABLES `Annuler` WRITE;
/*!40000 ALTER TABLE `Annuler` DISABLE KEYS */;
INSERT INTO `Annuler` VALUES (5,1),(7,3),(5,7),(5,8);
/*!40000 ALTER TABLE `Annuler` ENABLE KEYS */;
UNLOCK TABLES;
















