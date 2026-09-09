CREATE DATABASE  IF NOT EXISTS `exam_aminf_2324` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `exam_aminf_2324`;
-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: localhost    Database: exam_aminf_2324
-- ------------------------------------------------------
-- Server version	8.0.33

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
-- Table structure for table `Accident`
--

DROP TABLE IF EXISTS `Accident`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Accident` (
  `pk_accident` int NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `date` varchar(255) DEFAULT NULL,
  `fk_examLesson_associates` int DEFAULT NULL,
  `fk_lesson_associates` int DEFAULT NULL,
  `fk_accidentType_corresponds` int DEFAULT NULL,
  PRIMARY KEY (`pk_accident`),
  KEY `fk_examLesson_associates` (`fk_examLesson_associates`),
  KEY `fk_lesson_associates` (`fk_lesson_associates`),
  KEY `fk_accidentType_corresponds` (`fk_accidentType_corresponds`),
  CONSTRAINT `fk_accidentType_corresponds` FOREIGN KEY (`fk_accidentType_corresponds`) REFERENCES `AccidentType` (`pk_type`),
  CONSTRAINT `fk_examLesson_associates` FOREIGN KEY (`fk_examLesson_associates`) REFERENCES `ExamLesson` (`pk_lesson`),
  CONSTRAINT `fk_lesson_associates` FOREIGN KEY (`fk_lesson_associates`) REFERENCES `Lesson` (`pk_lesson`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Accident`
--

LOCK TABLES `Accident` WRITE;
/*!40000 ALTER TABLE `Accident` DISABLE KEYS */;
INSERT INTO `Accident` VALUES (1,'Minor collision at an intersection','2023-08-15',1,2,1),(2,'Vehicle skidded on wet road','2023-09-05',3,4,2),(3,'Parking lot fender bender','2023-10-20',5,6,3),(4,'Side-swipe during lane change','2023-11-10',7,8,1),(5,'Pedestrian involved in a crosswalk accident','2023-12-02',9,10,4),(6,'Collision while merging onto highway','2023-12-18',9,12,2),(7,'Accident due to slippery road conditions','2024-01-05',10,14,3);
/*!40000 ALTER TABLE `Accident` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AccidentType`
--

DROP TABLE IF EXISTS `AccidentType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AccidentType` (
  `pk_type` int NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pk_type`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AccidentType`
--

LOCK TABLES `AccidentType` WRITE;
/*!40000 ALTER TABLE `AccidentType` DISABLE KEYS */;
INSERT INTO `AccidentType` VALUES (1,'Minor collision'),(2,'Major collision'),(3,'Vehicle rollover'),(4,'Pedestrian involved'),(5,'Multiple vehicle collision'),(6,'Cyclist involved'),(7,'Parking lot accident');
/*!40000 ALTER TABLE `AccidentType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Examinator`
--

DROP TABLE IF EXISTS `Examinator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Examinator` (
  `pk_examinator` int NOT NULL AUTO_INCREMENT,
  `surname` varchar(255) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `birthDate` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pk_examinator`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Examinator`
--

LOCK TABLES `Examinator` WRITE;
/*!40000 ALTER TABLE `Examinator` DISABLE KEYS */;
INSERT INTO `Examinator` VALUES (1,'Koch','Michael','1994-12-03'),(2,'Fuchs','Sophie','1985-07-18'),(3,'Schwarz','Max','1990-03-12'),(4,'Bauer','Julia','1989-09-05'),(5,'Graf','Paul','1983-06-17'),(6,'Léon','Léa','1992-11-08');
/*!40000 ALTER TABLE `Examinator` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ExamLesson`
--

DROP TABLE IF EXISTS `ExamLesson`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ExamLesson` (
  `pk_lesson` int NOT NULL AUTO_INCREMENT,
  `dateTimeStartPlanned` varchar(255) DEFAULT NULL,
  `dateTimeEndPlanned` varchar(255) DEFAULT NULL,
  `grade` int DEFAULT NULL,
  `fk_licenseCategory_belongsTo` varchar(255) DEFAULT NULL,
  `fk_examinator_evaluates` int DEFAULT NULL,
  `fk_instructor_supervises` int DEFAULT NULL,
  `fk_student_undergoes` int DEFAULT NULL,
  PRIMARY KEY (`pk_lesson`),
  KEY `fk_licenseCategory_belongsTo` (`fk_licenseCategory_belongsTo`),
  KEY `fk_examinator_evaluates` (`fk_examinator_evaluates`),
  KEY `fk_instructor_supervises` (`fk_instructor_supervises`),
  KEY `fk_student_exam` (`fk_student_undergoes`),
  CONSTRAINT `fk_examinator_evaluates` FOREIGN KEY (`fk_examinator_evaluates`) REFERENCES `Examinator` (`pk_examinator`),
  CONSTRAINT `fk_instructor_supervises` FOREIGN KEY (`fk_instructor_supervises`) REFERENCES `Instructor` (`pk_instructor`),
  CONSTRAINT `fk_licenseCategory_belongsTo` FOREIGN KEY (`fk_licenseCategory_belongsTo`) REFERENCES `LicenseCategory` (`pk_category`),
  CONSTRAINT `fk_student_exam` FOREIGN KEY (`fk_student_undergoes`) REFERENCES `Student` (`pk_student`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ExamLesson`
--

LOCK TABLES `ExamLesson` WRITE;
/*!40000 ALTER TABLE `ExamLesson` DISABLE KEYS */;
INSERT INTO `ExamLesson` VALUES (1,'2023-11-15 09:30:00','2023-11-15 11:30:00',45,'B',1,4,7),(2,'2023-10-22 14:00:00','2023-10-22 16:00:00',32,'A',6,5,8),(3,'2023-09-18 11:00:00','2023-09-18 13:00:00',58,'C',3,6,7),(4,'2023-08-25 13:30:00','2023-08-25 15:30:00',25,'D',5,4,10),(5,'2023-07-10 10:30:00','2023-07-10 12:30:00',40,'A',2,8,1),(6,'2023-06-05 14:00:00','2023-06-05 16:00:00',55,'C',2,9,2),(7,'2023-05-12 09:00:00','2023-05-12 11:00:00',28,'B',1,4,3),(8,'2023-04-20 11:30:00','2023-04-20 13:30:00',50,'D',4,1,7),(9,'2023-03-15 13:00:00','2023-03-15 15:00:00',36,'C',6,4,5),(10,'2023-02-08 10:00:00','2023-02-08 12:00:00',59,'A',5,3,7);
/*!40000 ALTER TABLE `ExamLesson` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Instructor`
--

DROP TABLE IF EXISTS `Instructor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Instructor` (
  `pk_instructor` int NOT NULL AUTO_INCREMENT,
  `surname` varchar(255) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `birthDate` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pk_instructor`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Instructor`
--

LOCK TABLES `Instructor` WRITE;
/*!40000 ALTER TABLE `Instructor` DISABLE KEYS */;
INSERT INTO `Instructor` VALUES (1,'Schneider','Michael','1980-05-18'),(2,'Wagner','Christine','1975-09-30'),(3,'Meyer','Peter','1985-02-12'),(4,'Becker','Laura','1988-12-08'),(5,'Klein','Thomas','1972-08-25'),(6,'Fischer','Sarah','1965-11-14'),(7,'Huber','Martin','1978-04-03'),(8,'Weber','Jennifer','1982-06-20'),(9,'Müller','Anna','1987-10-15'),(10,'Schmit','Tom','1983-01-22');
/*!40000 ALTER TABLE `Instructor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Lesson`
--

DROP TABLE IF EXISTS `Lesson`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Lesson` (
  `pk_lesson` int NOT NULL AUTO_INCREMENT,
  `dateTimeStartPlanned` varchar(255) DEFAULT NULL,
  `dateTimeEndPlanned` varchar(255) DEFAULT NULL,
  `feedbackInstructor` varchar(255) DEFAULT NULL,
  `fk_student_participates` int DEFAULT NULL,
  `fk_instructor_participates` int DEFAULT NULL,
  PRIMARY KEY (`pk_lesson`),
  KEY `fk_instructor_participates` (`fk_instructor_participates`),
  CONSTRAINT `fk_instructor_participates` FOREIGN KEY (`fk_instructor_participates`) REFERENCES `Instructor` (`pk_instructor`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Lesson`
--

LOCK TABLES `Lesson` WRITE;
/*!40000 ALTER TABLE `Lesson` DISABLE KEYS */;
INSERT INTO `Lesson` VALUES (1,'2023-11-15 09:30:00','2023-11-15 11:30:00','Good progress',1,4),(2,'2023-10-22 14:00:00','2023-10-22 16:00:00','Satisfactory',2,5),(3,'2023-09-18 11:00:00','2023-09-18 13:00:00','Excellent',3,5),(4,'2023-08-25 13:30:00','2023-08-25 15:30:00','Needs improvement',4,2),(5,'2023-07-10 10:30:00','2023-07-10 12:30:00','Good progress',5,2),(6,'2023-06-05 14:00:00','2023-06-05 16:00:00','Satisfactory',6,1),(7,'2023-05-12 09:00:00','2023-05-12 11:00:00','Excellent',7,6),(8,'2023-04-20 11:30:00','2023-04-20 13:30:00','Needs improvement',8,1),(9,'2023-03-15 13:00:00','2023-03-15 15:00:00','Good progress',9,2),(10,'2023-02-08 10:00:00','2023-02-08 12:00:00','Satisfactory',10,4),(11,'2023-01-05 14:30:00','2023-01-05 16:30:00','Excellent',1,4),(12,'2023-12-12 09:30:00','2023-12-12 11:30:00','Needs improvement',2,7),(13,'2023-11-18 11:00:00','2023-11-18 13:00:00','Good progress',3,9),(14,'2023-10-25 13:30:00','2023-10-25 15:30:00','Satisfactory',4,7),(15,'2023-09-11 10:30:00','2023-09-11 12:30:00','Excellent',5,8),(16,'2023-08-06 14:00:00','2023-08-06 16:00:00','Needs improvement',6,9),(17,'2023-07-14 09:00:00','2023-07-14 11:00:00','Good progress',7,6),(18,'2023-06-22 11:30:00','2023-06-22 13:30:00','Satisfactory',8,1),(19,'2023-05-29 13:00:00','2023-05-29 15:00:00','Excellent',9,3),(20,'2023-04-03 10:00:00','2023-04-03 12:00:00','Needs improvement',10,2),(21,'2023-03-01 10:00:00','2023-03-01 12:00:00','Good progress',1,5);
/*!40000 ALTER TABLE `Lesson` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LicenseCategory`
--

DROP TABLE IF EXISTS `LicenseCategory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LicenseCategory` (
  `pk_category` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pk_category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LicenseCategory`
--

LOCK TABLES `LicenseCategory` WRITE;
/*!40000 ALTER TABLE `LicenseCategory` DISABLE KEYS */;
INSERT INTO `LicenseCategory` VALUES ('A','Motorcycle and tricycle'),('B','Personal cars with at most 9 seats'),('C','Bus'),('D','Vehicle that carries more than 9 persons');
/*!40000 ALTER TABLE `LicenseCategory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Student`
--

DROP TABLE IF EXISTS `Student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Student` (
  `pk_student` int NOT NULL AUTO_INCREMENT,
  `surname` varchar(255) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `birthDate` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pk_student`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Student`
--

LOCK TABLES `Student` WRITE;
/*!40000 ALTER TABLE `Student` DISABLE KEYS */;
INSERT INTO `Student` VALUES (1,'Müller','Anna','1995-03-15'),(2,'Schmit','Tom','1998-07-22'),(3,'Wagner','Sophie','1996-11-10'),(4,'Jacobs','Kevin','1999-04-05'),(5,'Becker','Laura','1997-09-18'),(6,'Klein','Michael','1994-12-03'),(7,'Fisch','Jennifer','1993-06-25'),(8,'Huber','Martin','1992-01-08'),(9,'Weber','Christine','1998-03-12'),(10,'Schneider','Peter','1991-07-29');
/*!40000 ALTER TABLE `Student` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-03-22 12:15:28
