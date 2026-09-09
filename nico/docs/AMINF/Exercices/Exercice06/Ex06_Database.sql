CREATE DATABASE  IF NOT EXISTS `GameZone` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `GameZone`;
-- MySQL dump 10.13  Distrib 8.0.17, for Win64 (x86_64)
--
-- Host: localhost    Database: GameZone
-- ------------------------------------------------------
-- Server version	8.0.17

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
-- Table structure for table `Article`
--

DROP TABLE IF EXISTS `Article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Article` (
  `pk_article` int(11) NOT NULL AUTO_INCREMENT,
  `fk_author_writes` int(11) NOT NULL,
  `title` varchar(250) NOT NULL,
  `subTitle` varchar(250) NOT NULL,
  `abstract` varchar(500) DEFAULT NULL,
  `body` varchar(4000) NOT NULL,
  `publicationDate` date NOT NULL,
  `rating` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`pk_article`),
  KEY `fkc_Author_writes` (`fk_author_writes`),
  CONSTRAINT `fk_Article_Author` FOREIGN KEY (`fk_author_writes`) REFERENCES `Author` (`pk_author`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Article`
--

LOCK TABLES `Article` WRITE;
/*!40000 ALTER TABLE `Article` DISABLE KEYS */;
INSERT INTO `Article` VALUES (1,1,'World of Warcraft Patch 7.3.5 brings zone level scaling, personal loot, and more','That\'s a good way of getting people to protect their accounts.','World of Warcraft players can look forward to a game-changing patch that brings a number of new features to the game.','The biggest new feature in patch 7.3.5 is the addition of the new scaling system that was introduced in Legion. This scaling system will be effective in every zone in Kalimdor, Eastern Kingdoms, Outland, Northrend, Pandaria, and Draenor, as well as corresponding dungeons.\n\nIn addition to expanding the scaling system, the update brings a new 10v10 battleground, personal loot for players in all dungeons, an easier way to obtain the SELFIE camera, and more.\n\nOn top of all this, accounts that are protected by an Authenticator and SMS Protect gain four extra backpack slots.\n\nYou can check out all of the new features and changes below.\nNew Features\nA SCALING WORLD\n\nEvery zone in Kalimdor, Eastern Kingdoms, Outland, Northrend, Pandaria, and Draenor now use the level scaling system introduced in Legion. This new scaling system greatly increases the amount of options you have when deciding where to quest and when to move on to the next zone.\n\nAll corresponding dungeons and the rewards therein now scale as well.\nULDUAR TIMEWALKING\n\nA long-forgotten evil has awakened once more. Travel back in time with 10-30 other adventurers to cleanse Ulduar of Yogg-Saron’s evil influence. While you journey through this memorable Wrath of the Lich King raid, your character will be scaled to player level 80.\n\nYour fate is sealed. The end of days is finally upon you and ALL who inhabit this miserable little seedling. Uulwi ifis halahs gag erh’ongg w’ssh.\nSILITHUS: THE WOUND\n\nPlayers who have completed Antorus, The Burning Throne will be summoned to your capital cities, from which you’ll travel to Silithus to investigate the strange events surrounding the impact of Sargeras’ blade. Look for this new questline upon logging in.\nNEW BATTLEGROUND: SEETHING SHORE\n\nThe Alliance and the Horde fight over a precious new resource, Azerite, in this new 10v10 battleground. Seething Shore is a king of the hill battleground where the first faction to collect ten pieces of Azerite from randomly spawned nodes, wins.\n\nThe Seething Shore will become available as the story in Silithus unfolds.','2018-01-16','5.9'),(2,1,'World of Warcraft | Battle for Azeroth Cinematic Trailer','VIDEO SUMMARY',NULL,' Blizzard has revealed a brand new expansion for World of Warcraft that brings new subraces, a new level caps, new continents, raids, and dungeons! The absolutely gorgeous cinematic trailer is above and the features trailer can be seen below, as well as the details.\n\nTensions between the Alliance and Horde have erupted, and a new age of war has begun! https://youtu.be/QsZ9xkVQ_Vs','2017-11-03',NULL),(3,1,'Blizzard is developing a mobile MMO RTS that could be a WarCraft game','Job listings are full of fun info.','The mobile gaming market is undoubtedly a fruitful one, with many major developers like Nintendo and Ubisoft releasing games in an attempt to expand the reach of their respective brands. It is known that Blizzard is another major publisher that looks like it too will be joining the fray, but what exactly they are working on remains a mystery. ',' The mobile gaming market is undoubtedly a fruitful one, with many major developers like Nintendo and Ubisoft releasing games in an attempt to expand the reach of their respective brands. It is known that Blizzard is another major publisher that looks like it too will be joining the fray, but what exactly they are working on remains a mystery. \n\nA previous job listing referenced a \"passion for creating imagery synonymous with the Warcraft IP,\" and now a few more have popped up in regards to a mobile title. However, it\'s worth noting that it\'s not confirmed if the job postings are for the same game, but they do reveal that a game being worked on behind the scenes is an \"MMO RTS\" for mobile.','2017-11-29',NULL),(4,2,'Deus Ex and The Avengers developer to focus more on online games going forward','The upcoming Avengers game may be an online-only title.',NULL,' Over the last few years, a number of developers known for their rich single player games have begun shifting toward a more enticing future of on-going online games. Ubisoft moved towards The Division, Bioware went with Anthem, Rockstar incorporated GTA Online into GTA V, and many others have done similar things.\n\nOne other developer beginning to follow that trend is Deus Ex and Tomb Raider developer Eidos Montreal. The studio posted a blog post on the future of their company by stating that they are looking to focus more on online experiences in gaming in the near future. The studio has primarily focused on single-player games but after Thief and Deus Ex: Mankind Divided failed to perform super well financially, it seems the studio is looking at a much easier and profitable solution for the future.\n\n\"At Eidos-Montréal, we’re constantly working towards creating innovative and exciting experiences for gamers everywhere. In turn, we are placing an added emphasis on the online experiences in our games, striving to continually provide players with content that is memorable and impactful.\" said studio head David Anfossi. \"Through the inherent interactivity of online play, our universes will have the chance to thrive both now and into the future. To achieve this, we are building the teams and tools capable of supporting our ambitions. We hope you will join us on this journey!\"\n\nEidos Montreal is currently developing a game based on The Avengers, bringing together Marvel\'s biggest characters in video game form after seeing them stride on the big screen. This does make people wonder if The Avengers will be some sort of online game rather than a single-player title with co-op. The studio will likely divulge more info regarding the untitled Avengers game in the new year.\n\nIt does also beg the question of what happens to Deus Ex which has been in doubt since last year. The series has been critically praised but that hasn\'t backed it up commercially. Publisher Square Enix has stated that they will likely return to the series when the time is right but that may be PR speak for \"maybe, probably not\".','2017-12-15',NULL),(5,3,'Pokemon on Nintendo Switch currently being localized; Could launch in 2018','It\'s coming very soon!','Nintendo is currently making their best effort to bring all of their major IPs to the Nintendo Switch in a very big way.','Nintendo is currently making their best effort to bring all of their major IPs to the Nintendo Switch in a very big way. Zelda and Mario saw massive innovations with open worlds and overhauled gameplay and their efforts were rewarded with critical praise and award recognition. One other series making its way to the Switch is Pokemon.\n\nWhat is arguably one of the most iconic brands of all-time is looking to shake things up on the Switch with rumors indicating all sorts of new massive changes in gameplay. We have lots of questions about the game such as what the game actually is and when we can play it!\n\nLuckily, it doesn’t sound like it’s too far off and it could hit sometime this year. A job listing indicates that the untitled Pokemon console game is in the late stages of development as it is currently being localized and it will reportedly be finished by June based off of the contracts for the editors.\n\nTypically localization is done at the end when the script for a game is finalized or very close to being 100% completed so we could see a full reveal for the game at E3 or a Nintendo Direct in the next couple of months. If we’re lucky, this may be the big game that Nintendo plans to ship for the holidays and push to sell more Nintendo Switch units.\n\nWe’ll keep you posted if this story develops.','2018-02-12',NULL),(6,3,'[Watch] Three new Pokemon games for Nintendo Switch revealed; Two are coming in 2018','So much Pokemon!',NULL,'Pokemon is a massive part of Nintendo. It has been around since the days of the Gameboy and has jumped from various platforms as gaming has evolved. Now with the Nintendo Switch out on the market, people have been wondering how developer GameFreak will use that hardware to their advantage with the series.\n\nAt an event in Tokyo, Japan tonight, Nintendo revealed a bunch of new Pokemon games for Nintendo Switch. First, there was Pokemon Quest, a sort of appetizer for the main course which will come to smartphones and Nintendo Switch. Then we have Pokemon: Let’s Go, Pikachu! and Let’s Go, Eevee! These games are more traditional Pokemon games based on the Pokemon Yellow: Special Pikachu Edition from the 1990s. Both games feature very beautiful graphics and new systems made specifically for the Switch.\n\nYou can play in co-op, by yourself, and even connect it with the popular 2016 mobile game, Pokemon Go! It’s sort of unclear how it works at the moment but presumably, you can catch the critters with your phone and port them over to your Switch and vice versa while getting special benefits. You can get a taste of it in the trailer below.\n\nOn top of that, you have the Pokeball Plus device which you use to catch Pokemon in-game like a Joy-Con and even carry with you in real life, almost like you’re taking care of it as a pet. This appears to be an optional separate device so you won’t have to flashback to the days of letting your Tomagachi die. You’ll also be able to customize Pikachu and Eevee with different outfits if you chose to.\n\nOne other detail that came out of all of this was that a new entry in the core Pokemon RPG series is on the way in the second half of 2019. There’s no name or other details, it’s unclear how much of a difference there will be between it and the Let’s Go titles but we’ll likely find out more around this time next year.\n\nThe Pokemon: Let’s Go titles will release on Nintendo Switch on November 16th, 2018 along with the PokeBall Plus device. Pokemon Quest is out now on Switch for free with the mobile version coming at the end of June.','2018-02-28',NULL),(7,3,'Pokemon: Let’s Go, Pikachu/Eevee gets new details and trailer','A whole new way to experience the long-running series.',NULL,'Ever since the Nintendo Switch launched last year, fans have been clamoring for a new Pokemon game to take advantage of the hardware. After plenty of teasing, Nintendo will drop two new games at the end of the year and it looks like it’ll be worth the wait.\n\nCombining the traditional series mechanics with the hit mobile game Pokemon GO, Pokemon: Let’s Go, Pikachu! and Let’s Go, Eevee! will bring the acclaimed series to the Nintendo Switch with exciting new changes. A new trailer details just some of the things you’ll be able to do in the adventure game such as riding larger creatures and customizing your trainer and furry friend. You’ll even treat your Pokemon more as a friend or pet rather than a fighter by having it travel with you outside of its Pokeball and having the ability to pet/tickle it.\n\nFor the first time ever in a mainline game, players will also be able to play in co-op on the same console. You’ll be able to help each other catch new critters and participate in tag team battles! You’ll also be able to play online and battle and trade with each other as usual.\n\nYou can check out the new trailer for the game as well as a map of Kanto below.','2018-07-12',NULL),(8,2,'Review: Call of Duty: Modern Warfare hits and misses many of its marks','An exciting and frustrating mixed bag.','Call of Duty is a series that has always been criticized for being “the same” year over year. In 2014, we saw the series go into the future and since then, it has only embraced the future. Now, we’re back to Modern Warfare and while it’s familiar, it’s also very new and different.','Call of Duty: Modern Warfare places us in the shoes of various soldiers in October and November 2019. The world is tense following a horrific terrorist attack and a deadly chemical nerve agent has gone missing. It’s up to you to find out where it is and intercept it before it can be used in a catastrophic incident.\n\nIt’s a rather traditional story that does nothing particularly new on the surface but Infinity Ward’s execution is what elevates it above many other stories in the franchise and genre. They swap out the tried and true Michael Bay vibes for those reminiscent of a more contemporary war film like Zero Dark Thirty. The big threat and core plot is mere window dressing for what the game is actually about, it’s the themes that help convincingly sell this story into something far more than an action-packed popcorn blockbuster.\n\nCall of Duty: Modern Warfare\n\nCall of Duty: Modern Warfare is a story about soldiers in a war or world that is not black and white. This is a story about the morally right thing not always being the correct solution to a problem. This is a story about the mentality of a soldier.\n\nWith that comes a cast of both new and returning players to the Modern Warfare subfranchise. The oldies are as good as they’ve ever been but often the newbies fail to make an impression. They serve the story they’re here for but will I remember CIA agent Alex like I remembered Gaz, Ghost, or Soap? Unlikely. Where Call of Duty: Modern Warfare makes itself memorable is in the moments these characters find themselves.\n\nModern Warfare pushes a narrative that isn’t complex in its initial presentation but is layered with hard-hitting questions that weigh heavily on the player constantly.\n\nNot only are you shown traumatic things such as soul-shaking terrorist attacks that rattle you internally with the echoing gunshots, screaming, and streets covered in blood but you’re put in stressful situations. When I saw the game back at E3, I questioned if the game would be able to effectively execute its themes in gameplay. After playing it myself, I can say Modern Warfare made me feel things that few other games have ever felt.','2019-11-01','7.6'),(9,2,'5 tips to dominate in Call of Duty: Modern Warfare multiplayer','Get the jump on the opposition.',NULL,'Call of Duty: Modern Warfare is finally upon us! While many of us are incredibly familiar with the ins and outs of the famed shooter, there are a lot of new and important facets to Modern Warfare. It’s still early days but we’ve learned a bunch of tips that have helped us dominate the opposition and wanted to share five of them with you.\n\nWhether it’s how you equip yourself or how you handle yourself on the battlefield, we’ve got you covered.\n\n1. Know Your Weapon\n\nWhat do you think of when you think Call of Duty? Guns, of course! It’s important to understand that Modern Warfare is going to make you think much harder about what you’re using than other games in the series. Why? Gunsmith. We’ll get more into Gunsmith specifically shortly but this is specifically how Gunsmith affects the actual gameplay.\n\nCall of Duty: Modern Warfare\n\nA lot of people have complained about how fast you can move in Modern Warfare and that may be in part due to your gun. Depending on how you build your weapon, you can really weigh your character down and impact your sprint, general walking speed, and how quickly you can aim down sights. It’s incredibly important to pay attention to the pros and cons tab when adding attachments as it can determine how you need to play.\n\nIt’s also important to be aware of the functionalities of your gun. If you have an automatic weapon, it’s likely equipped with a single-fire rate as well (left on D-pad on controllers). If you’re in a long-range battle, you’ll likely want to toggle to single-fire to focus your shots and not having to worry about your gun moving all over the place. Just remember you will need to toggle it back to fully-auto when you’re done.\n\n2. Building Your Best Friend\n\nYour gun is your very best friend in Call of Duty: Modern Warfare. Lucky for you, you can make it incredibly personal and unique due to very specific and varied customization. Gunsmith allows you to build a gun in a way you never have before in Call of Duty.\n\nPreviously, you slapped on a red dot sight, maybe a grip or different muzzle, and you called it good. This allows you to customize every little detail from barrel to muzzle to stock to under rail, and everything in between. It goes so deep that you can create guns that technically aren’t in the game. Want an M16 carbine? It’s not available in the game but you can build one using the M4A1 as a base. You can swap out the barrel for an M16 barrel, add a three-round burst fire, and then build that bad boy out to have whatever other attachments you want on it.','2019-10-28',NULL),(10,1,'X019 starts tomorrow and Age of Empires 4 will be there','One day until Wololo',NULL,'General Manager of Xbox Games Marketing Aaron Greenberg has teased the appearance of Age of Empire 4 for tomorrow. That is when this year’s celebration of all things Xbox start with X019. Besides first-ever game footage, the event will also showcase games that are finally coming to the Xbox, new game infos and reveals. Well over two years ago, Microsoft initially announced the long-awaited fourth entry into the legendary historical real-time strategy series. After that, it got very quiet however, with very little details being shared. Tomorrow, this radio-silence is finally looking to come to an end, with Microsoft openly teasing that Age of Empires 4 will be at X019. Microsoft’s awkwardly named yearly celebration event is kicking off tomorrow and lasts until Saturday. This year, it takes place in London, UK. One of Xbox’s strongholds in Europe. You can follow X019 live on Mixer and not miss out on any new reveals. But be warned, unlike E3 or Gamescom, these events from Microsoft are rather geared towards showcasing already known and released titles. So, don’t expect a huge game unveiling. The secret highlight of the show could very well become the new Microsoft Flight Simulator which is poised to become one of the most realistic and large simulators available. It’s still a pretty enjoyable event for Xbox gamers and marks Microsoft’s answer to a more community-oriented approach to the marketing of likes of Nintendo Direct.','2019-11-13',NULL);
/*!40000 ALTER TABLE `Article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Links`
--

DROP TABLE IF EXISTS `Links`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Links` (
  `pkfk_article_from` int(11) NOT NULL,
  `pkfk_article_to` int(11) NOT NULL,
  PRIMARY KEY (`pkfk_article_to`,`pkfk_article_from`),
  KEY `fkc_Article_links_to_idx` (`pkfk_article_from`),
  CONSTRAINT `fkc_Article_links_to` FOREIGN KEY (`pkfk_article_from`) REFERENCES `Article` (`pk_article`),
  CONSTRAINT `fkc_Article_links_from` FOREIGN KEY (`pkfk_article_to`) REFERENCES `Article` (`pk_article`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Links`
--

LOCK TABLES `Links` WRITE;
/*!40000 ALTER TABLE `Links` DISABLE KEYS */;
INSERT INTO `Links` VALUES (2,1),(2,3),(5,6),(6,7);
/*!40000 ALTER TABLE `Links` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Author`
--

DROP TABLE IF EXISTS `Author`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Author` (
  `pk_author` int(11) NOT NULL AUTO_INCREMENT,
  `surname` varchar(250) NOT NULL,
  `firstName` varchar(250) NOT NULL,
  PRIMARY KEY (`pk_author`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Author`
--

LOCK TABLES `Author` WRITE;
/*!40000 ALTER TABLE `Author` DISABLE KEYS */;
INSERT INTO `Author` VALUES (1,'Illidan','Stormrage'),(2,'Adam','Jensen'),(3,'Ash','Ketchum'),(4,'Carl','Johnson');
/*!40000 ALTER TABLE `Author` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-11-13 23:37:46
