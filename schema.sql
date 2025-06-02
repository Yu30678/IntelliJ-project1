-- MySQL dump 10.13  Distrib 9.3.0, for macos15.2 (arm64)
--
-- Host: localhost    Database: Backend_side_project
-- ------------------------------------------------------
-- Server version	9.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `member_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `create_at` datetime DEFAULT CURRENT_TIMESTAMP,
  KEY `cart_ibfk_1_idx` (`member_id`),
  KEY `cart_ibfk_2` (`product_id`),
  CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (50698,2,1,'2025-04-21 22:15:54'),(50699,2,1,'2025-04-21 22:20:50'),(50709,2,1,'2025-04-22 16:13:48'),(50711,2,5,'2025-04-22 16:16:02'),(30678,3,1,'2025-04-26 21:22:47'),(30678,8,1,'2025-04-24 12:01:00'),(50690,7,1,'2025-04-24 12:01:00'),(50690,13,1,'2025-05-02 17:04:59'),(50699,3,1,'2025-04-26 11:53:09'),(50690,6,9,'2025-01-01 00:00:00'),(50717,23,2,'2025-04-26 12:26:00'),(50717,25,2,'2025-05-18 13:21:31');
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (26,'blabla'),(27,'test0519'),(8,'TestCat'),(10,'TestCat_16593'),(12,'TestCat_21919'),(13,'TestCat_24069'),(25,'TestCat_26049_upd'),(15,'TestCat_30972'),(23,'TestCat_31861_upd'),(19,'TestCat_68788'),(21,'TestCat_75033'),(16,'TestCat_82105'),(14,'TestCat_8834'),(11,'TestCat_90049'),(2,'手機_upd'),(3,'手錶_upd'),(22,'汽車'),(5,'雜項'),(6,'電腦');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member`
--

DROP TABLE IF EXISTS `member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member` (
  `member_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `password` varchar(30) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `address` varchar(50) DEFAULT NULL,
  `create_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `email` varchar(255) NOT NULL,
  PRIMARY KEY (`member_id`),
  UNIQUE KEY `phone_UNIQUE` (`phone`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=50729 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member`
--

LOCK TABLES `member` WRITE;
/*!40000 ALTER TABLE `member` DISABLE KEYS */;
INSERT INTO `member` VALUES (66,'66_upd_upd_upd_upd_upd_upd_upd','66','1','1','2025-03-25 21:08:12','1'),(111,'111','111','2','2','2025-03-25 09:44:09','2'),(123,'123','123','3','3','2025-03-24 21:14:19','3'),(124,'qa','qa','qa','qa','2025-03-25 09:43:23','4'),(222,'qa','222','4','4','2025-03-25 09:48:53','5'),(333,'小王_udp','333','5','5','2025-03-26 19:40:02','6'),(444,'阿智','444','6','6','2025-03-26 19:47:37','7'),(555,'555','555','7','7','2025-03-26 20:14:48','8'),(679,'679','679','8','8','2025-03-26 20:35:55','9'),(3434,'3434','3434','9','9','2025-03-26 20:45:34','10'),(9778,'阿明','09778','098746372','台中市南屯區','2025-04-15 17:12:21','uyyjdk@gmail'),(30678,'yuu','30678','10','10','2025-03-26 21:28:57','11'),(30679,'kevin','123456','0912345678','台北市','2025-04-11 22:11:09','kevin@example.com'),(30693,'測試小明','abc12389','0966649978','台北市中正區','2025-04-13 13:44:50','ming66@example.com'),(30699,'測試小王','abc12378','0966649987','台北市信義區','2025-04-13 16:01:42','ming˙˙@example.com'),(30701,'測試小漲','66758309','0965498347','台北市永和區','2025-04-13 17:31:01','chamg@example.com'),(40678,'poiu','poiu','090909090','sasdwe','2025-04-15 22:40:23','sasawecfew'),(40679,'測試小老','99758309','09345498347','台北市萬華區','2025-04-17 15:37:31','chamg90876@example.com'),(50678,'50678','50678','50678','50678','2025-04-18 21:14:01','50678'),(50680,'TestUser','pass123','09188746352','Test Address','2025-04-20 11:07:44','testuser@example.com'),(50681,'Testpser','pasd123','091887452','Test Adtrress','2025-04-20 11:12:16','testuser@ex897ple.com'),(50682,'Te909er','pas472123','0800987452','Test Adtrefcss','2025-04-20 11:29:21','testu7832r@ex897ple.com'),(50683,'Te509er','pas47gt23','08009452','Test Adtfcss','2025-04-20 11:30:23','testu32r@ex897ple.com'),(50684,'Te5879er','pas473672f23','080sajnc52','Test Adsaiosss','2025-04-20 12:02:54','testu32r@escuue97ple.com'),(50685,'Te5879pr','pas54f23','080s0ijnc52','Test Adsaisiucss','2025-04-20 12:10:31','testu32r@s3cuue97ple.com'),(50686,'Te5743849pr','pa347723','08238840ijnc52','Test Ads439fucss','2025-04-20 12:21:04','testu32r@s3firueue97ple.com'),(50687,'Te509379pr','pd88723','0823jnc5832','Test Ads876fucss','2025-04-20 12:22:45','testu32r@sfjvhdrueue97ple.com'),(50689,'Test56ser59702_upd','pa5623','015659702','Test Ad5ess','2025-04-20 17:17:40','te56ser59702@example.com'),(50690,'Test561223','pa56','01571223','Test Ad6ess','2025-04-20 17:20:01','te56sqr1223@example.com'),(50691,'Test5695108_upd','pa57','015795108','Test Ad9ess','2025-04-20 20:18:15','te66sqr95108@example.com'),(50692,'user131','pass131','0912131','Address131','2025-04-21 21:06:40','user131@example.com'),(50693,'user26164','pass26164','091226164','Address26164','2025-04-21 21:27:06','user26164@example.com'),(50694,'user60108','pass60108','091260108','Address60108','2025-04-21 21:46:00','user60108@example.com'),(50695,'user64350','pass64350','091264350','Address64350','2025-04-21 21:51:04','user64350@example.com'),(50697,'user7113','pass7113','09127113','Address7113','2025-04-21 21:58:27','user7113@example.com'),(50698,'user54073','pass54073','091254073','Address54073','2025-04-21 22:15:54','user54073@example.com'),(50699,'user49907','pass49907','091249907','Address49907','2025-04-21 22:20:50','user49907@example.com'),(50700,'user8429','pass8429','09128429','Address8429','2025-04-21 22:50:08','user8429@example.com'),(50701,'user50408','pass50408','091250408','Address50408','2025-04-22 09:49:10','user50408@example.com'),(50702,'user96252','pass96252','091296252','Address96252','2025-04-22 09:49:56','user96252@example.com'),(50703,'user11605','pass11605','091211605','Address11605','2025-04-22 10:00:12','user11605@example.com'),(50704,'user54458','pass54458','091254458','Address54458','2025-04-22 10:07:34','user54458@example.com'),(50705,'user79595','pass79595','091279595','Address79595','2025-04-22 15:06:20','user79595@example.com'),(50706,'user7351','pass7351','09127351','Address7351','2025-04-22 15:33:27','user7351@example.com'),(50707,'user36154','pass36154','091236154','Address36154','2025-04-22 15:33:56','user36154@example.com'),(50708,'user73432','pass73432','091273432','Address73432','2025-04-22 15:36:13','user73432@example.com'),(50709,'user27300','pass27300','091227300','Address27300','2025-04-22 16:13:47','user27300@example.com'),(50710,'user24848','pass24848','091224848','Address24848','2025-04-22 16:15:25','user24848@example.com'),(50711,'user62088','pass62088','091262088','Address62088','2025-04-22 16:16:02','user62088@example.com'),(50712,'user97480','pass97480','091297480','Address97480','2025-04-22 16:16:37','user97480@example.com'),(50713,'pocUser8463','pass8463','09128463','Addr8463','2025-04-23 15:36:18','poc8463@e.com'),(50714,'pocUser9776','pass9776','09129776','Addr9776','2025-04-23 15:39:00','poc9776@e.com'),(50715,'user1745413033975','pass','092427933','Test Address','2025-04-23 20:57:14','email1745413033978@test.com'),(50716,'user1745413308436','pass123','09124561','AddressTest','2025-04-23 21:01:48','test1745413308439@example.com'),(50717,'user66773','pass66773','091266773','Address66773','2025-04-26 21:24:27','user66773@example.com'),(50719,'user12966','pass12966','091212966','Address12966','2025-05-01 21:20:13','user12966@example.com'),(50720,'user27762','pass27762','091227762','Address27762','2025-05-02 17:03:48','user27762@example.com'),(50721,'dkosioj','dkosioj','0987655678','dkosioj','2025-05-03 16:18:00','dkosioj@test.com'),(50722,'foieujrj_upd','foieujrj','098790398_upd','foieujrj','2025-05-03 16:24:00','foieujrj@test.com_upd'),(50723,'test678ssss','p328f666','09fdiv00999','Taoeei','2025-05-26 12:00:00','test0user@du8mple.com'),(50725,'testssss','p3666','09fd00999','Taoeei','2025-05-03 22:30:50','test0user@dple.com'),(50727,'tes517ss','pa51766','095170999','Ta517ei','2025-05-18 20:19:27','testuse5178mple.com'),(50728,'今晚打老虎','0518','5018','5018','2025-05-18 21:04:17','5018');
/*!40000 ALTER TABLE `member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `member_id` int NOT NULL,
  `create_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`),
  KEY `order_ibfk_1_idx` (`member_id`),
  CONSTRAINT `order_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (1,123,'2025-03-24 21:14:56'),(2,123,'2025-03-24 21:14:56'),(3,123,'2025-03-24 22:02:46'),(4,123,'2025-03-24 22:02:46'),(5,66,'2025-03-25 21:08:47'),(6,66,'2025-03-25 21:08:47'),(7,123,'2025-03-26 17:08:53'),(8,123,'2025-03-26 17:08:53'),(9,222,'2025-03-26 20:00:26'),(10,222,'2025-03-26 20:00:26'),(11,444,'2025-03-26 20:18:09'),(12,444,'2025-03-26 20:18:09'),(13,30678,'2025-03-26 21:30:53'),(14,30678,'2025-03-26 21:30:53'),(15,123,'2025-03-26 21:35:04'),(16,123,'2025-03-26 21:35:04'),(17,123,'2025-03-26 21:35:04'),(18,123,'2025-03-29 11:34:43'),(19,123,'2025-03-29 11:34:43'),(20,123,'2025-04-02 21:26:39'),(21,9778,'2025-04-15 17:17:53'),(22,111,'2025-04-15 22:44:39'),(23,50678,'2025-04-18 21:15:32'),(24,30678,'2025-04-19 17:59:05'),(25,30679,'2025-04-19 20:35:41'),(26,30679,'2025-04-19 20:39:11'),(27,30679,'2025-04-19 20:53:06'),(28,30679,'2025-04-19 20:55:16'),(29,30679,'2025-04-19 21:24:46'),(30,30679,'2025-04-19 21:26:58'),(32,30678,'2025-04-21 17:07:51'),(33,30678,'2025-04-21 17:08:58'),(36,50703,'2025-04-22 10:00:12'),(37,50704,'2025-04-22 10:07:35'),(39,50708,'2025-04-22 15:36:14'),(40,50711,'2025-04-22 16:16:02'),(41,50712,'2025-04-22 16:16:38'),(42,50713,'2025-04-23 15:36:19'),(43,50714,'2025-04-23 15:39:00'),(44,30678,'2025-04-23 15:52:22'),(45,30678,'2025-04-23 16:01:09'),(46,30678,'2025-04-23 16:12:25'),(47,30678,'2025-04-23 16:20:46'),(48,30678,'2025-04-23 22:04:16'),(49,30678,'2025-04-24 06:46:05'),(50,30678,'2025-04-24 17:25:26'),(51,30678,'2025-04-25 09:59:26'),(52,30678,'2025-04-25 10:02:35'),(53,30678,'2025-04-25 10:02:42'),(54,30678,'2025-04-25 10:07:10'),(57,30678,'2025-04-25 21:26:40'),(58,30678,'2025-04-25 21:28:00'),(59,30678,'2025-04-25 21:52:50'),(60,30678,'2025-04-25 21:56:05'),(62,50690,'2025-04-25 23:51:46'),(63,50690,'2025-04-26 00:25:19'),(64,50690,'2025-04-26 00:25:19'),(65,50690,'2025-04-26 00:25:19'),(66,50690,'2025-04-26 00:52:28'),(67,50690,'2025-04-26 00:52:28'),(68,50690,'2025-04-26 00:52:28'),(69,50690,'2025-04-26 00:55:54'),(70,50690,'2025-04-26 00:55:54'),(71,50690,'2025-04-26 00:55:54'),(72,30678,'2025-04-26 09:06:42'),(73,30678,'2025-04-26 10:44:10'),(74,50717,'2025-04-26 21:24:27'),(75,50719,'2025-05-01 21:20:13'),(76,50717,'2025-05-01 21:43:20'),(77,50717,'2025-05-01 22:27:18'),(78,50720,'2025-05-02 17:03:48'),(79,50716,'2025-05-18 12:25:53'),(80,50710,'2025-05-18 12:27:36');
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_detail`
--

DROP TABLE IF EXISTS `order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_detail` (
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  UNIQUE KEY `order_id` (`order_id`,`product_id`),
  KEY `order_detail_ibfk_1_idx` (`order_id`),
  KEY `order_detail_ibfk_2_idx` (`product_id`),
  CONSTRAINT `order_detail_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `order` (`order_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `order_detail_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_detail`
--

LOCK TABLES `order_detail` WRITE;
/*!40000 ALTER TABLE `order_detail` DISABLE KEYS */;
INSERT INTO `order_detail` VALUES (1,1,1,12000.00),(3,1,3,49800.00),(5,1,1,49800.00),(7,1,1,49800.00),(9,1,1,49800.00),(11,3,1,3500.00),(13,2,2,72300.00),(15,1,1,49800.00),(15,2,1,72300.00),(18,5,45,30.00),(20,4,1,50.00),(21,8,1,49999.00),(22,2,1,72300.00),(23,1,1,799.00),(24,1,1,799.00),(25,2,2,72300.00),(26,7,3,36999.00),(27,3,2,3500.00),(28,4,4,50.00),(29,4,4,50.00),(30,13,13,599.99),(78,1,5,199.99),(79,25,1,99.99),(80,2,5,72300.00);
/*!40000 ALTER TABLE `order_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(40) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `soh` int unsigned NOT NULL,
  `category_id` int DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `image_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `product_name_UNIQUE` (`name`),
  KEY `p001_idx` (`category_id`),
  CONSTRAINT `p001` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'30678_707_upd',199.99,95,6,1,'http://localhost:8080/images/test.png'),(2,'MacBook pro 14\"',72300.00,15,6,1,'http://localhost:8080/images/macbook14.jpeg'),(3,'飛利浦全自動咖啡機',3500.00,4,5,1,'http://localhost:8080/images/ccoffee.jpeg'),(4,'巧克力',50.00,0,5,0,'http://localhost:8080/images/choo.jpeg'),(5,'古早味紅茶',30.00,0,5,0,'http://localhost:8080/images/blacktea.jpeg'),(6,'MacBook Pro',59999.00,9,6,1,'http://localhost:8080/images/macbook14.jpeg'),(7,'iPhone 15',36999.00,0,2,0,'http://localhost:8080/images/iphone15.jpeg'),(8,'ASUS ROG',3500.00,5,5,1,'http://localhost:8080/images/ROG.jpeg'),(13,'藍牙滑鼠',599.99,17,5,1,'http://localhost:8080/images/mouse.jpeg'),(14,'irock藍牙滑鼠',879.99,30,5,0,'http://localhost:8080/images/irock.jpeg'),(23,'TestProduct',123.45,50,5,1,'http://localhost:8080/images/test.png'),(25,'TestProduct_71',99.99,99,6,1,'http://localhost:8080/images/test.png'),(29,'30678_110',99.99,100,6,1,'http://localhost:8080/images/test.png'),(30,'30678_260',99.99,100,6,1,'http://localhost:8080/images/test.png'),(31,'30678_32',99.99,100,6,1,'http://localhost:8080/images/test.png'),(35,'30678_980_upd',199.99,100,6,1,'http://localhost:8080/images/test.png'),(38,'noStock_9776',100.00,0,8,0,'http://localhost:8080/images/test.png'),(45,'超屌電視_',966.00,5,11,1,'http://localhost:8080/images/tv.png'),(46,'30678_50_upd',199.99,100,6,1,'http://localhost:8080/images/test.png'),(47,'超屌手機',987.00,567,11,1,'http://localhost:8080/images/phone.png'),(48,'超屌音響',969.00,20,5,1,'http://localhost:8080/images/12.png');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `password` varchar(30) NOT NULL,
  `account` varchar(40) NOT NULL,
  `level` tinyint NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `account` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'yu','000','000',1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-25 19:44:21
