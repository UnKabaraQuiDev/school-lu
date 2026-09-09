CREATE DATABASE IF NOT EXISTS `RaceGame`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE `RaceGame`;

-- MySQL 8.x setup script for the racing database model
-- Re-runnable: drops and recreates all tables in the RaceGame schema.

SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `unlocks`;
DROP TABLE IF EXISTS `participates`;
DROP TABLE IF EXISTS `includes`;
DROP TABLE IF EXISTS `reservation`;
DROP TABLE IF EXISTS `race`;
DROP TABLE IF EXISTS `racefield`;
DROP TABLE IF EXISTS `achievement`;
DROP TABLE IF EXISTS `player`;

SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;

-- -----------------------------------------------------
-- Table `player`
-- -----------------------------------------------------
CREATE TABLE `player` (
  `pk_player` INT NOT NULL AUTO_INCREMENT,
  `nickname` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`pk_player`),
  UNIQUE KEY `uk_player_nickname` (`nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `racefield`
-- -----------------------------------------------------
CREATE TABLE `racefield` (
  `pk_racefield` INT NOT NULL AUTO_INCREMENT,
  `label` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`pk_racefield`),
  UNIQUE KEY `uk_racefield_label` (`label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `achievement`
-- -----------------------------------------------------
CREATE TABLE `achievement` (
  `pk_achievement` INT NOT NULL AUTO_INCREMENT,
  `label` VARCHAR(100) NOT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  `icon` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`pk_achievement`),
  UNIQUE KEY `uk_achievement_label` (`label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `race`
-- -----------------------------------------------------
CREATE TABLE `race` (
  `pk_race` INT NOT NULL AUTO_INCREMENT,
  `minplayers` INT NOT NULL,
  `maxplayers` INT NOT NULL,
  `fk_racefield_isplayedon` INT NOT NULL,
  `fk_race_links_parent` INT DEFAULT NULL,
  PRIMARY KEY (`pk_race`),
  KEY `idx_race_racefield` (`fk_racefield_isplayedon`),
  KEY `idx_race_parent` (`fk_race_links_parent`),
  CONSTRAINT `chk_race_min_max_players`
    CHECK (`minplayers` > 0 AND `maxplayers` >= `minplayers`),
  CONSTRAINT `fk_race_racefield`
    FOREIGN KEY (`fk_racefield_isplayedon`)
    REFERENCES `racefield` (`pk_racefield`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_race_parent`
    FOREIGN KEY (`fk_race_links_parent`)
    REFERENCES `race` (`pk_race`)
    ON DELETE SET NULL
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `reservation`
-- -----------------------------------------------------
CREATE TABLE `reservation` (
  `pk_reservation` INT NOT NULL AUTO_INCREMENT,
  `datetime` DATETIME NOT NULL,
  `status` VARCHAR(30) NOT NULL,
  `fk_player_books` INT NOT NULL,
  PRIMARY KEY (`pk_reservation`),
  KEY `idx_reservation_player` (`fk_player_books`),
  CONSTRAINT `chk_reservation_status`
    CHECK (`status` IN ('booked', 'completed', 'cancelled')),
  CONSTRAINT `fk_reservation_player`
    FOREIGN KEY (`fk_player_books`)
    REFERENCES `player` (`pk_player`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `includes`
-- -----------------------------------------------------
CREATE TABLE `includes` (
  `pkfk_race` INT NOT NULL,
  `pkfk_reservation` INT NOT NULL,
  `price` DECIMAL(8,2) NOT NULL,
  `duration` INT NOT NULL,
  PRIMARY KEY (`pkfk_race`, `pkfk_reservation`),
  KEY `idx_includes_reservation` (`pkfk_reservation`),
  CONSTRAINT `chk_includes_price`
    CHECK (`price` >= 0),
  CONSTRAINT `chk_includes_duration`
    CHECK (`duration` > 0),
  CONSTRAINT `fk_includes_race`
    FOREIGN KEY (`pkfk_race`)
    REFERENCES `race` (`pk_race`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_includes_reservation`
    FOREIGN KEY (`pkfk_reservation`)
    REFERENCES `reservation` (`pk_reservation`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `participates`
-- -----------------------------------------------------
CREATE TABLE `participates` (
  `pkfk_player` INT NOT NULL,
  `pkfk_race` INT NOT NULL,
  `position` INT DEFAULT NULL,
  `totallaps` INT NOT NULL DEFAULT 0,
  `totalpoints` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`pkfk_player`, `pkfk_race`),
  KEY `idx_participates_race` (`pkfk_race`),
  CONSTRAINT `chk_participates_position`
    CHECK (`position` IS NULL OR `position` > 0),
  CONSTRAINT `chk_participates_totallaps`
    CHECK (`totallaps` >= 0),
  CONSTRAINT `chk_participates_totalpoints`
    CHECK (`totalpoints` >= 0),
  CONSTRAINT `fk_participates_player`
    FOREIGN KEY (`pkfk_player`)
    REFERENCES `player` (`pk_player`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_participates_race`
    FOREIGN KEY (`pkfk_race`)
    REFERENCES `race` (`pk_race`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `unlocks`
-- -----------------------------------------------------
CREATE TABLE `unlocks` (
  `pkfk_player` INT NOT NULL,
  `pkfk_achievement` INT NOT NULL,
  `date` DATE NOT NULL,
  PRIMARY KEY (`pkfk_player`, `pkfk_achievement`),
  KEY `idx_unlocks_achievement` (`pkfk_achievement`),
  CONSTRAINT `fk_unlocks_player`
    FOREIGN KEY (`pkfk_player`)
    REFERENCES `player` (`pk_player`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_unlocks_achievement`
    FOREIGN KEY (`pkfk_achievement`)
    REFERENCES `achievement` (`pk_achievement`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Default testing data
-- -----------------------------------------------------

INSERT INTO `player` (`pk_player`, `nickname`) VALUES
(1, 'KriRo'),
(2, 'PiSy'),
(3, 'ToBEr'),
(4, 'ThiFr'),
(5, 'GasDr'),
(6, 'GrebJ'),
(7, 'HaaLa'),
(8, 'WeBRe'),
(9, 'Ana'),
(10, 'ZedNoRes'),
(11, 'Space Rider'),
(12, 'NoPoints');

INSERT INTO `racefield` (`pk_racefield`, `label`) VALUES
(1, 'Desert Circuit'),
(2, 'City Sprint'),
(3, 'Forest Track'),
(4, 'Ice Arena'),
(5, 'Volcano Loop');

INSERT INTO `achievement` (`pk_achievement`, `label`, `description`, `icon`) VALUES
(1, 'Top Scorer', 'Awarded to players with exceptional points.', 'top_scorer.png'),
(2, 'Lap Leader', 'Awarded to players with strong lap performance.', 'lap_leader.png'),
(3, 'Gold', 'Special gold achievement.', 'gold.png'),
(4, 'Fast Starter', 'Awarded to players with quick race starts.', 'fast_starter.png'),
(5, 'Endurance Racer', 'Awarded to players who perform well in long races.', 'endurance.png');

INSERT INTO `race` (`pk_race`, `minplayers`, `maxplayers`, `fk_racefield_isplayedon`, `fk_race_links_parent`) VALUES
(1, 2, 6, 1, NULL),
(2, 3, 6, 2, NULL),
(3, 4, 8, 3, NULL),
(4, 2, 4, 4, 1),
(5, 3, 5, 5, NULL),
(6, 2, 6, 1, 2);

INSERT INTO `reservation` (`pk_reservation`, `datetime`, `status`, `fk_player_books`) VALUES
(1, '2025-04-01 12:00:00', 'booked', 1),
(2, '2025-04-01 13:00:00', 'completed', 2),
(3, '2025-04-02 15:00:00', 'booked', 3),
(4, '2025-04-03 17:30:00', 'cancelled', 4),
(5, '2025-04-04 11:00:00', 'booked', 7),
(6, '2025-04-04 12:30:00', 'completed', 8),
(7, '2025-04-05 14:00:00', 'booked', 11);

INSERT INTO `includes` (`pkfk_race`, `pkfk_reservation`, `price`, `duration`) VALUES
(1, 1, 25.00, 30),
(1, 2, 25.00, 30),
(1, 5, 25.00, 30),
(2, 3, 20.00, 25),
(3, 4, 30.00, 40),
(4, 2, 35.00, 45),
(4, 6, 35.00, 45),
(6, 7, 22.50, 30);

INSERT INTO `participates` (`pkfk_player`, `pkfk_race`, `position`, `totallaps`, `totalpoints`) VALUES
-- Race 1: enough participants, minplayers = 2, actual = 5
(1, 1, 1, 10, 120),
(2, 1, 2, 10, 110),
(7, 1, 3, 10, 95),
(8, 1, 4, 10, 90),
(11, 1, 5, 9, 75),

-- Race 2: insufficient participants, minplayers = 3, actual = 2
(5, 2, 1, 8, 65),
(6, 2, 2, 8, 60),

-- Race 3: insufficient participants, minplayers = 4, actual = 1
(3, 3, 1, 12, 85),

-- Race 4: enough participants, minplayers = 2, actual = 2
(4, 4, 1, 15, 100),
(3, 4, 2, 15, 95),

-- Race 6: enough participants, minplayers = 2, actual = 3
(1, 6, 1, 9, 100),
(2, 6, 2, 9, 90),
(5, 6, 3, 9, 70);

-- Race 5 intentionally has zero participants.
-- It should appear in Q4 as an insufficient-participant race.

INSERT INTO `unlocks` (`pkfk_player`, `pkfk_achievement`, `date`) VALUES
(1, 1, '2025-04-10'),
(2, 1, '2025-04-10'),
(3, 2, '2025-04-11'),
(4, 2, '2025-04-11'),
-- Achievement 3, Gold, intentionally has no unlocked players.
(5, 4, '2025-04-12'),
(6, 4, '2025-04-12'),
(7, 5, '2025-04-13'),
(8, 5, '2025-04-13');

-- Optional sanity-check queries for the exercise prompts

-- Q1
-- SELECT p.nickname
-- FROM player p
-- WHERE NOT EXISTS (
--   SELECT 1
--   FROM reservation r
--   WHERE r.fk_player_books = p.pk_player
-- )
-- ORDER BY p.nickname;

-- Q3 attached query should work because table `player` exists.
-- SELECT nickname FROM player WHERE nickname NOT LIKE '% %';

-- Q4
-- SELECT
--   r.pk_race AS `Race id.`,
--   rf.label AS `Race Field`,
--   r.minplayers AS `Minimum Players`,
--   COUNT(p.pkfk_player) AS `# Participants`
-- FROM race r
-- JOIN racefield rf
--   ON rf.pk_racefield = r.fk_racefield_isplayedon
-- LEFT JOIN participates p
--   ON p.pkfk_race = r.pk_race
-- GROUP BY r.pk_race, rf.label, r.minplayers
-- HAVING COUNT(p.pkfk_player) < r.minplayers
-- ORDER BY r.pk_race;

-- Q5
-- SELECT
--   p.nickname,
--   SUM(pa.totalpoints) AS accumulatedPoints
-- FROM player p
-- JOIN participates pa
--   ON pa.pkfk_player = p.pk_player
-- GROUP BY p.pk_player, p.nickname
-- ORDER BY accumulatedPoints DESC, p.nickname ASC
-- LIMIT 3;

-- Q6
-- SELECT
--   a.label AS `Achievement`,
--   GROUP_CONCAT(p.nickname ORDER BY p.nickname SEPARATOR ',') AS `Players`
-- FROM achievement a
-- LEFT JOIN unlocks u
--   ON u.pkfk_achievement = a.pk_achievement
-- LEFT JOIN player p
--   ON p.pk_player = u.pkfk_player
-- GROUP BY a.pk_achievement, a.label
-- ORDER BY a.pk_achievement;
