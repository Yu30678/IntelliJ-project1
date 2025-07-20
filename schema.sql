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
INSERT INTO `cart` VALUES (50711,2,5,'2025-04-22 16:16:02'),(30678,3,1,'2025-04-26 21:22:47');
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
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (8,'TestCat'),(10,'TestCat_1659309'),(13,'TestCat_24069'),(25,'TestCat_26049_upd'),(15,'TestCat_30972'),(23,'TestCat_31861_upd'),(19,'TestCat_68788'),(21,'TestCat_75033'),(16,'TestCat_82105'),(14,'TestCat_88345'),(11,'TestCat_90049'),(2,'手機_upd'),(3,'手錶_1'),(22,'汽車'),(5,'雜項'),(6,'電腦');
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
) ENGINE=InnoDB AUTO_INCREMENT=50750 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member`
--

LOCK TABLES `member` WRITE;
/*!40000 ALTER TABLE `member` DISABLE KEYS */;
INSERT INTO `member` VALUES (9778,'阿明','09778','098746372','台中市南屯區','2025-04-15 17:12:21','uyyjdk@gmail.com'),(30678,'yuu','30678','10','10','2025-03-26 21:28:57','11'),(30699,'新姓名9','新密碼9','新電話9','新地址9','2025-04-13 16:01:42','new@email9.com'),(30701,'測試小漲','66758309','0965498347','台北市永和區','2025-04-13 17:31:01','chamg@example.com'),(40678,'poiu','poiu','090909090','sasdwe','2025-04-15 22:40:23','sasawecfew'),(40679,'測試小老','99758309','09345498347','台北市萬華區','2025-04-17 15:37:31','chamg90876@example.com'),(50678,'50678','50678','50678','50678','2025-04-18 21:14:01','50678'),(50680,'TestUser','123456','093254997','Test Address','2025-04-20 11:07:44','testuser@example.com'),(50681,'Testpser','pasd123','091887452','Test Adtrress','2025-04-20 11:12:16','testuser@ex897ple.com'),(50682,'Te909er','pas472123','0800987452','Test Adtrefcss','2025-04-20 11:29:21','testu7832r@ex897ple.com'),(50683,'Te509er','pas47gt23','08009452','Test Adtfcss','2025-04-20 11:30:23','testu32r@ex897ple.com'),(50684,'Te5879er','pas473672f23','080sajnc52','Test Adsaiosss','2025-04-20 12:02:54','testu32r@escuue97ple.com'),(50685,'Te5879pr','pas54f23','080s0ijnc52','Test Adsaisiucss','2025-04-20 12:10:31','testu32r@s3cuue97ple.com'),(50686,'Te5743849pr','pa347723','08238840ijnc52','Test Ads439fucss','2025-04-20 12:21:04','testu32r@s3firueue97ple.com'),(50687,'Te509379pr','pd88723','0823jnc5832','Test Ads876fucss','2025-04-20 12:22:45','testu32r@sfjvhdrueue97ple.com'),(50689,'Test56ser59702_upd','pa5623','015659702','Test Ad5ess','2025-04-20 17:17:40','te56ser59702@example.com'),(50690,'Test561223','pa56','01571223','Test Ad6ess','2025-04-20 17:20:01','te56sqr1223@example.com'),(50691,'Test5695108_upd','pa57','015795108','Test Ad9ess','2025-04-20 20:18:15','te66sqr95108@example.com'),(50692,'user131','pass131','0912131','Address131','2025-04-21 21:06:40','user131@example.com'),(50693,'user26164','pass26164','091226164','Address26164','2025-04-21 21:27:06','user26164@example.com'),(50694,'user60108','pass60108','091260108','Address60108','2025-04-21 21:46:00','user60108@example.com'),(50695,'user64350','pass64350','091264350','Address64350','2025-04-21 21:51:04','user64350@example.com'),(50697,'user7113','pass7113','09127113','Address7113','2025-04-21 21:58:27','user7113@example.com'),(50698,'user54073','pass54073','091254073','Address54073','2025-04-21 22:15:54','user54073@example.com'),(50699,'user49907','pass49907','091249907','Address49907','2025-04-21 22:20:50','user49907@example.com'),(50700,'user8429','pass8429','09128429','Address8429','2025-04-21 22:50:08','user8429@example.com'),(50701,'user50408','pass50408','091250408','Address50408','2025-04-22 09:49:10','user50408@example.com'),(50702,'user96252','pass96252','091296252','Address96252','2025-04-22 09:49:56','user96252@example.com'),(50703,'user11605','pass11605','091211605','Address11605','2025-04-22 10:00:12','user11605@example.com'),(50704,'user54458','pass54458','091254458','Address54458','2025-04-22 10:07:34','user54458@example.com'),(50705,'user79595','pass79595','091279595','Address79595','2025-04-22 15:06:20','user79595@example.com'),(50706,'user7351','pass7351','09127351','Address7351','2025-04-22 15:33:27','user7351@example.com'),(50707,'user36154','pass36154','091236154','Address36154','2025-04-22 15:33:56','user36154@example.com'),(50708,'user73432','pass73432','091273432','Address73432','2025-04-22 15:36:13','user73432@example.com'),(50709,'user27300','pass27300','091227300','Address27300','2025-04-22 16:13:47','user27300@example.com'),(50710,'user24848','pass24848','091224848','Address24848','2025-04-22 16:15:25','user24848@example.com'),(50711,'user62088','pass62088','091262088','Address62088','2025-04-22 16:16:02','user62088@example.com'),(50712,'user97480','pass97480','091297480','Address97480','2025-04-22 16:16:37','user97480@example.com'),(50713,'pocUser8463','pass8463','09128463','Addr8463','2025-04-23 15:36:18','poc8463@e.com'),(50714,'pocUser9776','pass9776','09129776','Addr9776','2025-04-23 15:39:00','poc9776@e.com'),(50715,'user1745413033975','pass','092427933','Test Address','2025-04-23 20:57:14','email1745413033978@test.com'),(50716,'user1745413308436','pass123','09124561','AddressTest','2025-04-23 21:01:48','test1745413308439@example.com'),(50717,'user66773','pass66773','091266773','Address66773','2025-04-26 21:24:27','user66773@example.com'),(50719,'user12966','pass12966','091212966','Address12966','2025-05-01 21:20:13','user12966@example.com'),(50720,'user27762','pass27762','091227762','Address27762','2025-05-02 17:03:48','user27762@example.com'),(50721,'dkosioj','dkosioj','0987655678','dkosioj','2025-05-03 16:18:00','dkosioj@test.com'),(50722,'0620_upd','0620','0620_upd','0620','2025-05-03 16:24:00','0620@test.com_upd'),(50723,'test678ssss','p328f666','09fdiv00999','Taoeei','2025-05-26 12:00:00','test0user@du8mple.com'),(50725,'testssss','p3666','09fd00999','Taoeei','2025-05-03 22:30:50','test0user@dple.com'),(50727,'tes517ss','pa51766','095170999','Ta517ei','2025-05-18 20:19:27','testuse5178mple.com'),(50728,'今晚打老虎','0518','5018','5018','2025-05-18 21:04:17','5018'),(50729,'0607','0607','0607','0607','2025-06-07 10:33:25','0607@mail'),(50730,'tes609ss','pa60966','096090999','Ta609ei','2025-06-09 17:22:55','testuse6099mple.com'),(50731,'tes6099ss','pa609966','0960990999','Ta6099ei','2025-06-09 20:37:40','testuse60999mple.com'),(50733,'tes611ss','pa61166','096110999','Ta611ei','2025-06-11 17:12:02','testuse6111mple.com'),(50734,'tes6112ss','pa611266','0961120999','Ta6112ei','2025-06-11 17:23:53','testuse61112mple.com'),(50735,'tes613ss','pa611366','0961130999','Ta613ei','2025-06-13 16:44:19','testuse613mple.com'),(50736,'tes6131ss','pa6113166','09611310999','Ta6131ei','2025-06-13 16:48:04','testuse6131mple.com'),(50738,'tes6131ss','pa6113166','09611320999','Ta6131ei','2025-06-13 16:52:43','testuse61312mple.com'),(50739,'foi618jrj','fo618rj','09618398','fo618jrj','2025-06-18 21:52:04','foie618st.com'),(50740,'tes619ss','pa61966','096190999','Ta619ei','2025-06-19 17:20:33','testuse619mple.com'),(50741,'tes6192ss','pa619266','0951809999','Ta6192ei','2025-06-19 21:16:51','testuse6192mple.com'),(50742,'馬英９','9999999','09999','台北市','2025-06-21 10:35:29','99@gmail.com'),(50743,'test20250623','test20250623','test20250623','test20250623','2025-06-23 19:17:08','test20250623'),(50744,'20250623test','000000','20250623test','20250623test','2025-06-23 20:19:26','20250623test@gmail.com'),(50745,'202506301','000000','093232','093232','2025-06-30 23:05:13','20250630@mail.com'),(50748,'20250710','093254','093254','093232','2025-07-10 22:36:34','0710@mail.com'),(50749,'張煜','he753951','0932542139','彰化市大埔路485巷119號','2025-07-11 00:33:58','mop753951@gmail.com');
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
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (13,30678,'2025-03-26 21:30:53'),(79,50716,'2025-05-18 12:25:53'),(80,50710,'2025-05-18 12:27:36'),(81,50698,'2025-06-20 12:57:00'),(83,50709,'2025-06-20 13:28:00'),(84,50744,'2025-06-23 23:24:34'),(85,50744,'2025-06-24 07:55:39'),(89,50744,'2025-07-10 14:25:51'),(90,50748,'2025-07-10 16:31:51'),(91,50680,'2025-07-17 14:36:06');
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
INSERT INTO `order_detail` VALUES (13,2,2,72300.00),(79,25,1,99.99),(80,2,5,72300.00),(81,2,2,72300.00),(83,2,5,72300.00),(84,2,1,72300.00),(85,13,1,599.99),(89,3,1,3500.00),(90,3,1,3500.00),(91,7,1,36999.00);
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
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (2,'MacBook pro 14\"',72300.00,7,6,1,'http://localhost:8080/images/macbook14.jpeg'),(3,'飛利浦全自動咖啡機22',3500.00,2,22,1,'http://localhost:8080/images/ccoffee.jpeg'),(4,'巧克力',50.00,2,5,1,'http://localhost:8080/images/choo.jpeg'),(5,'古早味紅茶_upt',30.00,2,22,1,'http://localhost:8081/images/img_bea5c085-72e2-4f99-9302-2ff6843634fd.jpg'),(7,'iPhone 15',36999.00,1,2,1,'http://localhost:8080/images/iphone15.jpeg'),(8,'ASUS ROG',3500.00,4,5,1,'http://localhost:8080/images/ROG.jpeg'),(13,'藍牙滑鼠',599.99,15,5,1,'http://localhost:8080/images/mouse.jpeg'),(14,'irock藍牙滑鼠',879.00,30,5,1,'http://localhost:8080/images/irock.jpeg'),(25,'TestProduct_71',99.99,99,6,1,'http://localhost:8080/images/test.png'),(29,'30678_110',99.99,100,6,1,'http://localhost:8080/images/test.png'),(30,'30678_260',99.00,100,14,1,'http://localhost:8081/images/img_dacd681b-e6e5-40a4-8fd9-4b901935cb3b.jpg'),(31,'30678_32',99.99,100,6,1,'http://localhost:8080/images/test.png'),(35,'30678_980_upd',199.99,100,6,1,'http://localhost:8080/images/test.png'),(38,'noStock_9776',100.00,0,8,0,'http://localhost:8080/images/test.png'),(45,'超屌電視_upd',987.00,10,5,1,NULL),(46,'30678_50_upd',199.99,100,6,1,'http://localhost:8080/images/test.png'),(47,'超屌手機',987.00,567,11,1,'http://localhost:8080/images/phone.png'),(48,'超屌音響',969.00,20,5,1,'http://localhost:8080/images/12.png'),(49,'超屌螢幕',16888.00,5,5,1,NULL),(54,'超屌螢幕2',168888.00,5,5,1,'1232123'),(55,'超屌螢幕23',1688888.00,5,5,1,'1232123'),(56,'超屌電視-456',987456.00,20,5,1,NULL),(57,'超屌電視-789',9856.00,20,5,1,NULL),(58,'超屌電視-000',9856.00,20,5,1,NULL),(60,'超屌電視-002',9856.00,20,5,1,NULL),(61,'超屌電視-0003',987.00,10,5,1,NULL),(62,'20250702',720.00,2,22,1,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (2,'yuu2','30678','30678',0),(3,'kobe','111111','111111',3),(4,'trump','222222','222222',0),(5,'30678','123456','123456',2),(7,'0705','070500','070500',2),(8,'張煜','he753951','mop753951',0);
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

-- Dump completed on 2025-07-19 17:24:47
