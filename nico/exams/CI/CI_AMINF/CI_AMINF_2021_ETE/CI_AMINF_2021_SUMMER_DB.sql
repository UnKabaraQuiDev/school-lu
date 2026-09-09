-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema examen
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `examen` ;

-- -----------------------------------------------------
-- Schema examen
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `examen` DEFAULT CHARACTER SET utf8 ;
USE `examen` ;

-- -----------------------------------------------------
-- Table `Accès`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Accès` (
  `code_PK` VARCHAR(16) NOT NULL,
  `niveau` INT DEFAULT 0,
  `descriptif` VARCHAR(250) NOT NULL,  
  PRIMARY KEY (`code_PK`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Data for table `Accès`
-- -----------------------------------------------------
START TRANSACTION;
USE `examen`;
INSERT INTO `Accès` (`code_PK`, `niveau`, `descriptif`)
			VALUES ('Aucun', 0, 'Niveau d\'accès le plus bas!');
INSERT INTO `Accès` (`code_PK`, `niveau`, `descriptif`)
			VALUES ('Bas', 1, 'Niveau d\'accès bas!');
INSERT INTO `Accès` (`code_PK`, `niveau`, `descriptif`)
			VALUES ('Moyen', 2, 'Niveau d\'accès moyen!');
INSERT INTO `Accès` (`code_PK`, `niveau`, `descriptif`)
			VALUES ('Secret', 3, 'Niveau d\'accès secret!');
INSERT INTO `Accès` (`code_PK`, `niveau`, `descriptif`)
			VALUES ('Top Secret', 4, 'Niveau d\'accès le plus élevé!');			
COMMIT;

-- -----------------------------------------------------
-- Table `Rôle`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Rôle` (
  `nom_PK` VARCHAR(32) NOT NULL,
  `code_DonnerDroit_FK` VARCHAR(16) NOT NULL,  
  `raccourci` VARCHAR(3) DEFAULT 'NON',
  `description` VARCHAR(250) NOT NULL,  
  PRIMARY KEY (`nom_PK`),
  FOREIGN KEY (`code_DonnerDroit_FK`) REFERENCES `Accès`(`code_PK`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Data for table `Rôle`
-- -----------------------------------------------------
START TRANSACTION;
USE `examen`;
INSERT INTO `Rôle` (`nom_PK`, `code_DonnerDroit_FK`, `raccourci`, `description`)
			VALUES ('Amiral', "Top Secret", 'ADM', 'Rôle d\'amiral.');
INSERT INTO `Rôle` (`nom_PK`, `code_DonnerDroit_FK`, `raccourci`, `description`)
			VALUES ('Capitaine', "Secret", 'CAP', 'Rôle de capitaine.');
INSERT INTO `Rôle` (`nom_PK`, `code_DonnerDroit_FK`, `raccourci`, `description`)
			VALUES ('Ingénieur', "Moyen", 'ING', 'Rôle d\'ingénieur.');
INSERT INTO `Rôle` (`nom_PK`, `code_DonnerDroit_FK`, `raccourci`, `description`)
			VALUES ('Matelot', "Aucun", 'MAT', 'Rôle de matelot.');			
COMMIT;

-- -----------------------------------------------------
-- Table `Personnel`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Personnel` (
  `matricule_PK` INT NOT NULL,
  `nom` VARCHAR(32) NOT NULL,  
  `prénom` VARCHAR(32) NOT NULL,
  `sexe` VARCHAR(1) NOT NULL,  
  `dateNaissance` DATE NOT NULL,
  PRIMARY KEY (`matricule_PK`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Data for table `Personnel`
-- -----------------------------------------------------
START TRANSACTION;
USE `examen`;
INSERT INTO `Personnel` (`matricule_PK`, `nom`, `prénom`, `sexe`, `dateNaissance`)
			VALUES ('52641432', 'Janeway', 'Catherine', 'm', '1983-08-05');
INSERT INTO `Personnel` (`matricule_PK`, `nom`, `prénom`, `sexe`, `dateNaissance`)
			VALUES ('59472156', 'Smith', 'Maggy', 'f', '1959-06-23');
INSERT INTO `Personnel` (`matricule_PK`, `nom`, `prénom`, `sexe`, `dateNaissance`)
			VALUES ('95847215', 'Piccard', 'Jean-Luc', 'm', '1963-12-23');            
INSERT INTO `Personnel` (`matricule_PK`, `nom`, `prénom`, `sexe`, `dateNaissance`)
			VALUES ('95147896', 'Pederson', 'Samantha', 'f', '1968-11-14');            
INSERT INTO `Personnel` (`matricule_PK`, `nom`, `prénom`, `sexe`, `dateNaissance`)
			VALUES ('11589931', 'Michelsky', 'Ziggy', 'm', '1979-01-02');
INSERT INTO `Personnel` (`matricule_PK`, `nom`, `prénom`, `sexe`, `dateNaissance`)
			VALUES ('54697365', 'Kirk', 'James', 'm', '1973-10-14');
COMMIT;

-- -----------------------------------------------------
-- Table `Assumer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Assumer` (
  `matricule_Assumer_PKFK` INT NOT NULL,
  `nom_Assumer_PKFK` VARCHAR(32) NOT NULL,  
  `dateDébut_PK` DATE NOT NULL,
  `dateFin` DATE,
  PRIMARY KEY (`matricule_Assumer_PKFK`, `nom_Assumer_PKFK`, dateDébut_PK),
  FOREIGN KEY (`matricule_Assumer_PKFK`) REFERENCES Personnel(matricule_PK),
  FOREIGN KEY (`nom_Assumer_PKFK`) REFERENCES Rôle(nom_PK))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Data for table `Assumer`
-- -----------------------------------------------------
START TRANSACTION;
USE `examen`;
INSERT INTO `Assumer` (`matricule_Assumer_PKFK`, `nom_Assumer_PKFK`, `dateDébut_PK`, `dateFin`)
			VALUES ('95847215', 'Amiral', '1996-05-09', NULL);
INSERT INTO `Assumer` (`matricule_Assumer_PKFK`, `nom_Assumer_PKFK`, `dateDébut_PK`, `dateFin`)
			VALUES ('54697365', 'Amiral', '1990-01-02', NULL);
INSERT INTO `Assumer` (`matricule_Assumer_PKFK`, `nom_Assumer_PKFK`, `dateDébut_PK`, `dateFin`)
			VALUES ('95847215', 'Capitaine', '1981-04-07', '1996-05-09');
INSERT INTO `Assumer` (`matricule_Assumer_PKFK`, `nom_Assumer_PKFK`, `dateDébut_PK`, `dateFin`)
			VALUES ('52641432', 'Capitaine', '1994-11-06', NULL);            
INSERT INTO `Assumer` (`matricule_Assumer_PKFK`, `nom_Assumer_PKFK`, `dateDébut_PK`, `dateFin`)
			VALUES ('59472156', 'Ingénieur', '1993-09-13', NULL);
INSERT INTO `Assumer` (`matricule_Assumer_PKFK`, `nom_Assumer_PKFK`, `dateDébut_PK`, `dateFin`)
			VALUES ('95147896', 'Ingénieur', '2004-03-25', NULL);
INSERT INTO `Assumer` (`matricule_Assumer_PKFK`, `nom_Assumer_PKFK`, `dateDébut_PK`, `dateFin`)
			VALUES ('11589931', 'Matelot', '2001-05-28', NULL);
COMMIT;

-- -----------------------------------------------------
-- Table `Mission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Mission` (
  `identifiant_PK` INT AUTO_INCREMENT NOT NULL,
  `code_Attribuer_FK` VARCHAR(16) NOT NULL,  
  `matricule_DonnerOrdre_FK` INT NOT NULL,
  `nom` VARCHAR(32) NOT NULL,
  `dateDébut` DATE,
  `dateFin` DATE,
  `priorité` TINYINT DEFAULT 0,
  PRIMARY KEY (`identifiant_PK`),
  FOREIGN KEY (`code_Attribuer_FK`) REFERENCES Accès(code_PK),
  FOREIGN KEY (`matricule_DonnerOrdre_FK`) REFERENCES Personnel(matricule_PK))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Data for table `Mission`
-- -----------------------------------------------------
START TRANSACTION;
USE `examen`;
INSERT INTO `Mission` (`identifiant_PK`, `code_Attribuer_FK`, `matricule_DonnerOrdre_FK`, `nom`, `dateDébut`, `dateFin`, `priorité`)
			VALUES (DEFAULT, 'Aucun', 95847215, 'Wolf 359', NULL, NULL, 1);
INSERT INTO `Mission` (`identifiant_PK`, `code_Attribuer_FK`, `matricule_DonnerOrdre_FK`, `nom`, `dateDébut`, `dateFin`, `priorité`)
			VALUES (DEFAULT, 'Secret', 54697365, 'Hammerhead', '1983-08-15', '1983-10-02', 0);
INSERT INTO `Mission` (`identifiant_PK`, `code_Attribuer_FK`, `matricule_DonnerOrdre_FK`, `nom`, `dateDébut`, `dateFin`, `priorité`)
			VALUES (DEFAULT, 'Top Secret', 95847215, 'Plentitude', '1998-04-22', NULL, 0);
INSERT INTO `Mission` (`identifiant_PK`, `code_Attribuer_FK`, `matricule_DonnerOrdre_FK`, `nom`, `dateDébut`, `dateFin`, `priorité`)
			VALUES (DEFAULT, 'Bas', 54697365, 'Relief', '1999-11-03', '2002-05-13', 1);
INSERT INTO `Mission` (`identifiant_PK`, `code_Attribuer_FK`, `matricule_DonnerOrdre_FK`, `nom`, `dateDébut`, `dateFin`, `priorité`)
			VALUES (DEFAULT, 'Bas', 52641432, 'Explore', '1998-09-14', NULL, 0);            
INSERT INTO `Mission` (`identifiant_PK`, `code_Attribuer_FK`, `matricule_DonnerOrdre_FK`, `nom`, `dateDébut`, `dateFin`, `priorité`)
			VALUES (DEFAULT, 'Secret', 95847215, 'Plethora', '2001-11-03', '2002-08-11', 0);
COMMIT;

-- -----------------------------------------------------
-- Table `Effectuer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Effectuer` (
  `matricule_Effectuer_PKFK` INT NOT NULL,
  `identifiant_Effectuer_PKFK` INT NOT NULL,
  PRIMARY KEY (`matricule_Effectuer_PKFK`,`identifiant_Effectuer_PKFK`),
  FOREIGN KEY (`matricule_Effectuer_PKFK`) REFERENCES Personnel(matricule_PK),
  FOREIGN KEY (`identifiant_Effectuer_PKFK`) REFERENCES Mission(identifiant_PK))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Data for table `Effectuer`
-- -----------------------------------------------------
START TRANSACTION;
USE `examen`;
INSERT INTO `Effectuer` (`matricule_Effectuer_PKFK`, `identifiant_Effectuer_PKFK`)
			VALUES ('95847215', 1);
INSERT INTO `Effectuer` (`matricule_Effectuer_PKFK`, `identifiant_Effectuer_PKFK`)
			VALUES ('95847215', 2);
INSERT INTO `Effectuer` (`matricule_Effectuer_PKFK`, `identifiant_Effectuer_PKFK`)
			VALUES ('95847215', 3);
INSERT INTO `Effectuer` (`matricule_Effectuer_PKFK`, `identifiant_Effectuer_PKFK`)
			VALUES ('52641432', 4);
INSERT INTO `Effectuer` (`matricule_Effectuer_PKFK`, `identifiant_Effectuer_PKFK`)
			VALUES ('52641432', 5);
COMMIT;

-- -----------------------------------------------------
-- Table `Vaisseau`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Vaisseau` (
  `numéroDeRegistre_PK` VARCHAR(32) NOT NULL,
  `équipageMaximal` INT NOT NULL,
  `boucliers` TINYINT DEFAULT TRUE,
  `armement` TINYINT DEFAULT FALSE,
  `dateDeConstruction` DATE DEFAULT '1970-01-01',
  PRIMARY KEY (`numéroDeRegistre_PK`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Data for table `Vaisseau`
-- -----------------------------------------------------
START TRANSACTION;
USE `examen`;
INSERT INTO `Vaisseau` (`numéroDeRegistre_PK`, `équipageMaximal`, `boucliers`, `armement`, `dateDeConstruction`)
			VALUES ("NCC-1701E", 140, 7, 7, "1983-08-30");
INSERT INTO `Vaisseau` (`numéroDeRegistre_PK`, `équipageMaximal`, `boucliers`, `armement`, `dateDeConstruction`)
			VALUES ("NCC-1764", 128, 7, 10, "1988-11-25");
INSERT INTO `Vaisseau` (`numéroDeRegistre_PK`, `équipageMaximal`, `boucliers`, `armement`, `dateDeConstruction`)
			VALUES ("NCC-1031", 35, 4, 3, "1993-06-14");
INSERT INTO `Vaisseau` (`numéroDeRegistre_PK`, `équipageMaximal`, `boucliers`, `armement`, `dateDeConstruction`)
			VALUES ("NCC-74656", 165, 10, 8, "1996-02-13");
INSERT INTO `Vaisseau` (`numéroDeRegistre_PK`, `équipageMaximal`, `boucliers`, `armement`, `dateDeConstruction`)
			VALUES ("NCC-1887", 146, 3, 3, "1975-01-25");
COMMIT;

-- -----------------------------------------------------
-- Table `Résoudre`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Résoudre` (
  `numéroDeRegistre_Résoudre_PKFK` VARCHAR(32) NOT NULL,
  `identifiant_Résoudre_PKFK` INT NOT NULL,
  PRIMARY KEY (`numéroDeRegistre_Résoudre_PKFK`,`identifiant_Résoudre_PKFK`),
  FOREIGN KEY (`numéroDeRegistre_Résoudre_PKFK`) REFERENCES Vaisseau(numéroDeRegistre_PK),
  FOREIGN KEY (`identifiant_Résoudre_PKFK`) REFERENCES Mission(identifiant_PK))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Data for table `Résoudre`
-- -----------------------------------------------------
START TRANSACTION;
USE `examen`;
INSERT INTO `Résoudre` (`numéroDeRegistre_Résoudre_PKFK`, `identifiant_Résoudre_PKFK`)
			VALUES ("NCC-1701E", 1);            
INSERT INTO `Résoudre` (`numéroDeRegistre_Résoudre_PKFK`, `identifiant_Résoudre_PKFK`)
			VALUES ("NCC-1764", 1);
INSERT INTO `Résoudre` (`numéroDeRegistre_Résoudre_PKFK`, `identifiant_Résoudre_PKFK`)
			VALUES ("NCC-1031", 2);
INSERT INTO `Résoudre` (`numéroDeRegistre_Résoudre_PKFK`, `identifiant_Résoudre_PKFK`)
			VALUES ("NCC-1031", 4);
INSERT INTO `Résoudre` (`numéroDeRegistre_Résoudre_PKFK`, `identifiant_Résoudre_PKFK`)
			VALUES ("NCC-74656", 5);            
COMMIT;

-- -----------------------------------------------------
-- Table `Objectif`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Objectif` (
  `numéroDObjectif_PK` INT AUTO_INCREMENT NOT NULL,
  `identifiant_Avoir_FK` INT NOT NULL,
  `nom` VARCHAR(250) NOT NULL,
  PRIMARY KEY (`numéroDObjectif_PK`),
  FOREIGN KEY (`identifiant_Avoir_FK`) REFERENCES Mission(identifiant_PK))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Data for table `Objectif`
-- -----------------------------------------------------
START TRANSACTION;
USE `examen`;
INSERT INTO `Objectif` (`numéroDObjectif_PK`, `identifiant_Avoir_FK`, `nom`)
			VALUES (DEFAULT, 1, "Defeat Borg!");   
INSERT INTO `Objectif` (`numéroDObjectif_PK`, `identifiant_Avoir_FK`, `nom`)
			VALUES (DEFAULT, 1, "Save colony!"); 
INSERT INTO `Objectif` (`numéroDObjectif_PK`, `identifiant_Avoir_FK`, `nom`)
			VALUES (DEFAULT, 1, "Minimize casualities!"); 
INSERT INTO `Objectif` (`numéroDObjectif_PK`, `identifiant_Avoir_FK`, `nom`)
			VALUES (DEFAULT, 2, "Break through blockade!"); 
INSERT INTO `Objectif` (`numéroDObjectif_PK`, `identifiant_Avoir_FK`, `nom`)
			VALUES (DEFAULT, 2, "Dock and resupply!");
INSERT INTO `Objectif` (`numéroDObjectif_PK`, `identifiant_Avoir_FK`, `nom`)
			VALUES (DEFAULT, 3, "Deliver materials!"); 
INSERT INTO `Objectif` (`numéroDObjectif_PK`, `identifiant_Avoir_FK`, `nom`)
			VALUES (DEFAULT, 4, "Deliver medecine!"); 
INSERT INTO `Objectif` (`numéroDObjectif_PK`, `identifiant_Avoir_FK`, `nom`)
			VALUES (DEFAULT, 4, "Make the delivery within 48 hours!"); 
INSERT INTO `Objectif` (`numéroDObjectif_PK`, `identifiant_Avoir_FK`, `nom`)
			VALUES (DEFAULT, 5, "Explore the Delta Quadrant!"); 
INSERT INTO `Objectif` (`numéroDObjectif_PK`, `identifiant_Avoir_FK`, `nom`)
			VALUES (DEFAULT, 5, "Survive!"); 
INSERT INTO `Objectif` (`numéroDObjectif_PK`, `identifiant_Avoir_FK`, `nom`)
			VALUES (DEFAULT, 5, "Make it home!");           
COMMIT;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;