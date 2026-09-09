-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema exercise05
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `exercise05` ;

-- -----------------------------------------------------
-- Schema exercise05
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `exercise05` DEFAULT CHARACTER SET utf8 ;
USE `exercise05` ;
-- -----------------------------------------------------
-- Table `Residence`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Residence` (
  `pk_idResidence` INT NOT NULL AUTO_INCREMENT COMMENT 'Identifier of the residence',
  `name` VARCHAR(250) NOT NULL COMMENT 'Name of the residence',
  `constructionDate` INT NOT NULL COMMENT 'Year of construction',
  `undergroundParking` TINYINT NOT NULL DEFAULT 0 COMMENT 'Is underground parking available?',
  PRIMARY KEY (`pk_idResidence`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Apartment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Apartment` (
  `pk_idApartment` INT NOT NULL AUTO_INCREMENT COMMENT 'Identifier of the apartment',
  `fk_residence_contains` INT NOT NULL COMMENT 'Foreign key for the residence',
  `apartmentNumber` INT NULL COMMENT 'The apartment\'s number',
  `floor` VARCHAR(5) NOT NULL COMMENT 'What floor the apartment is located on.',
  `surface` INT NOT NULL COMMENT 'Surface area in m2',
  `numberRooms` INT NOT NULL COMMENT 'Number of rooms in the apartment',
  `balcony` TINYINT NOT NULL DEFAULT 0 COMMENT 'Does the apartment have a balcony?',
  `firstRentalDate` DATETIME NULL COMMENT 'Date the apartment was first rented on.',
  PRIMARY KEY (`pk_idApartment`),
  INDEX `fk_ApartmentContainsResidence_idx` (`fk_residence_contains` ASC),
  CONSTRAINT `fk_ApartmentContainsResidence`
    FOREIGN KEY (`fk_residence_contains`)
    REFERENCES `Residence` (`pk_idResidence`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Person`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Person` (
  `name` VARCHAR(45) NOT NULL COMMENT 'Nom de la Person',
  `firstName` VARCHAR(45) NOT NULL COMMENT 'Prénom de la Person',
  `pk_ssn` VARCHAR(11) NOT NULL COMMENT 'Unique social security number.',
  PRIMARY KEY (`pk_ssn`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Rents`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Rents` (
  `pkfk_apartment` INT NOT NULL,
  `pkfk_person` VARCHAR(11) NOT NULL,
  `pk_entryDate` DATETIME NOT NULL,
  `exitDate` DATETIME NULL,
  INDEX `prim` (`pkfk_apartment` ASC, `pkfk_person` ASC, `pk_entryDate` ASC),
  INDEX `fk_person_idx` (`pkfk_person` ASC),
  CONSTRAINT `fk_person`
    FOREIGN KEY (`pkfk_person`)
    REFERENCES `Person` (`pk_ssn`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_apartment`
    FOREIGN KEY (`pkfk_apartment`)
    REFERENCES `Apartment` (`pk_idApartment`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  PRIMARY KEY (`pkfk_apartment`,`pkfk_person`,`pk_entryDate`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Data for table `Residence`
-- -----------------------------------------------------
START TRANSACTION;
USE `exercise05`;
INSERT INTO `Residence` (`pk_idResidence`, `name`, `constructionDate`, `undergroundParking`) VALUES (DEFAULT, 'Beau soleil', 2017, 0);
INSERT INTO `Residence` (`pk_idResidence`, `name`, `constructionDate`, `undergroundParking`) VALUES (DEFAULT, 'Beau regard', 2004, 0);
INSERT INTO `Residence` (`pk_idResidence`, `name`, `constructionDate`, `undergroundParking`) VALUES (DEFAULT, 'Azure', 2009, 1);
INSERT INTO `Residence` (`pk_idResidence`, `name`, `constructionDate`, `undergroundParking`) VALUES (DEFAULT, 'Mon plaisir', 1992, 1);

COMMIT;


-- -----------------------------------------------------
-- Data for table `Apartment`
-- -----------------------------------------------------
START TRANSACTION;
USE `exercise05`;
INSERT INTO `Apartment` (`pk_idApartment`, `fk_residence_contains`, `apartmentNumber`, `floor`, `surface`, `numberRooms`, `balcony`, `firstRentalDate`) VALUES (DEFAULT, 1, 1, 'rdc', 80, 1, 0, '2017-09-01');
INSERT INTO `Apartment` (`pk_idApartment`, `fk_residence_contains`, `apartmentNumber`, `floor`, `surface`, `numberRooms`, `balcony`, `firstRentalDate`) VALUES (DEFAULT, 1, 2, 'rdc', 90, 1, 0, '2017-10-01');
INSERT INTO `Apartment` (`pk_idApartment`, `fk_residence_contains`, `apartmentNumber`, `floor`, `surface`, `numberRooms`, `balcony`, `firstRentalDate`) VALUES (DEFAULT, 1, 3, '1', 89, 2, 1, NULL);
INSERT INTO `Apartment` (`pk_idApartment`, `fk_residence_contains`, `apartmentNumber`, `floor`, `surface`, `numberRooms`, `balcony`, `firstRentalDate`) VALUES (DEFAULT, 1, 4, '1', 99, 2, 1, NULL);
INSERT INTO `Apartment` (`pk_idApartment`, `fk_residence_contains`, `apartmentNumber`, `floor`, `surface`, `numberRooms`, `balcony`, `firstRentalDate`) VALUES (DEFAULT, 2, 1, 'rdc', 102, 2, 0, '2004-01-01');
INSERT INTO `Apartment` (`pk_idApartment`, `fk_residence_contains`, `apartmentNumber`, `floor`, `surface`, `numberRooms`, `balcony`, `firstRentalDate`) VALUES (DEFAULT, 2, 2, '1', 110, 3, 1, '2004-02-01');
INSERT INTO `Apartment` (`pk_idApartment`, `fk_residence_contains`, `apartmentNumber`, `floor`, `surface`, `numberRooms`, `balcony`, `firstRentalDate`) VALUES (DEFAULT, 3, 1, 'rdc', 185, 4, 0, NULL);
INSERT INTO `Apartment` (`pk_idApartment`, `fk_residence_contains`, `apartmentNumber`, `floor`, `surface`, `numberRooms`, `balcony`, `firstRentalDate`) VALUES (DEFAULT, 3, 2, '1', 96, 2, 1, '2009-07-01');
INSERT INTO `Apartment` (`pk_idApartment`, `fk_residence_contains`, `apartmentNumber`, `floor`, `surface`, `numberRooms`, `balcony`, `firstRentalDate`) VALUES (DEFAULT, 3, 3, '1', 104, 2, 0, '2011-01-01');
INSERT INTO `Apartment` (`pk_idApartment`, `fk_residence_contains`, `apartmentNumber`, `floor`, `surface`, `numberRooms`, `balcony`, `firstRentalDate`) VALUES (DEFAULT, 3, 4, '2', 95, 2, 1, NULL);
INSERT INTO `Apartment` (`pk_idApartment`, `fk_residence_contains`, `apartmentNumber`, `floor`, `surface`, `numberRooms`, `balcony`, `firstRentalDate`) VALUES (DEFAULT, 3, 5, '2', 106, 2, 0, NULL);
INSERT INTO `Apartment` (`pk_idApartment`, `fk_residence_contains`, `apartmentNumber`, `floor`, `surface`, `numberRooms`, `balcony`, `firstRentalDate`) VALUES (DEFAULT, 3, 6, '3', 201, 4, 1, NULL);

COMMIT;


-- -----------------------------------------------------
-- Data for table `Person`
-- -----------------------------------------------------
START TRANSACTION;
USE `exercise05`;
INSERT INTO `Person` (`name`, `firstName`, `pk_ssn`) VALUES ('Dupont', 'Jean', '19520802');
INSERT INTO `Person` (`name`, `firstName`, `pk_ssn`) VALUES ('Dupont', 'Marcelle', '19520801');
INSERT INTO `Person` (`name`, `firstName`, `pk_ssn`) VALUES ('Durant', 'Pierre', '19700325');
INSERT INTO `Person` (`name`, `firstName`, `pk_ssn`) VALUES ('Noe', 'Janne', '19780605');
INSERT INTO `Person` (`name`, `firstName`, `pk_ssn`) VALUES ('Martin', 'Jaques', '19321220');
INSERT INTO `Person` (`name`, `firstName`, `pk_ssn`) VALUES ('Petit', 'Emanuelle', '19650922');

COMMIT;


-- -----------------------------------------------------
-- Data for table `Rents`
-- -----------------------------------------------------
START TRANSACTION;
USE `exercise05`;
INSERT INTO `Rents` (`pkfk_apartment`, `pkfk_person`, `pk_entryDate`, `exitDate`) VALUES (1, 19520802, '2017-09-01', NULL);
INSERT INTO `Rents` (`pkfk_apartment`, `pkfk_person`, `pk_entryDate`, `exitDate`) VALUES (2, 19520801, '2017-10-01', NULL);
INSERT INTO `Rents` (`pkfk_apartment`, `pkfk_person`, `pk_entryDate`, `exitDate`) VALUES (5, 19700325, '2004-01-01', '2008-07-31');
INSERT INTO `Rents` (`pkfk_apartment`, `pkfk_person`, `pk_entryDate`, `exitDate`) VALUES (6, 19700325, '2004-02-01', NULL);
INSERT INTO `Rents` (`pkfk_apartment`, `pkfk_person`, `pk_entryDate`, `exitDate`) VALUES (8, 19321220, '2009-07-01', '2019-03-31');
INSERT INTO `Rents` (`pkfk_apartment`, `pkfk_person`, `pk_entryDate`, `exitDate`) VALUES (9, 19650922, '2011-01-01', '2013-05-31');
INSERT INTO `Rents` (`pkfk_apartment`, `pkfk_person`, `pk_entryDate`, `exitDate`) VALUES (6, 19650922, '2014-09-01', NULL);

COMMIT;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
