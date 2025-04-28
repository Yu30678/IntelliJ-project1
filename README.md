##專案概要
- 提供完整的購物車網站後端
  - 會員：註冊、登入
  - 商品：瀏覽商品列表
  - 購物車：加入、查詢、更新、刪除
  - 訂單：建立、查詢
  - 管理員：會員、商品、商品類別、訂單的新增修改刪除查詢
- JAVA 21、原生httpserver
- 資料庫：MySQL 9.2.0，使用JDBC驅動


＃＃技術棧

| 語言      | Java 21                  |

| HTTP     | JAVA 原生httpserver       |

| JSON     | Gson                     |

| 資料庫    | MySQL 9.2.0、JDBC Driver  |

| 構建管理   | Maven                    |

##專案架構
Backend_side_project/
Backend_side_project/
├── pom.xml
├── src/main/java/
│   │   └── Main.java           # 啟動 HttpServer
│   ├── controller/
│   │   ├── memberController.java
│   │   ├── productController.java
│   │   ├── cartController.java
│   │   ├── orderController.java
│   │   └── userController.java
│   ├── dao/
│   │   ├── memberDAO.java
│   │   ├── productDAO.java
│   │   ├── categoryDAO.java
│   │   ├── cartDAO.java
│   │   └── orderDAO.java
│   ├── model/
│   │   ├── cart.java
│   │   ├── Member.java
│   │   ├── product.java
│   │   ├── Category.java
│   │   ├── user.java
│   │   ├── order.java
│   │   └── Order_detail.java
│   └── server/
│   │   ├──WebServer.java
    └── util/
│       ├── DBUtil.java               # 連線池與連線關閉工具
│       ├── LocalDateTimeAdapter.java # 處理時間序列化與反序列化操作
│       └── GsonUtil.java             # Gson 共用

##環境變數與啟動
1.建立資料庫：已完成
2.編譯&啟動
  -打包
      mvn clean package
  -執行jar
      java -jar target/Backend_side_project.jar
  -開發模式
      mvn compile ##編譯原始碼
      mvn exec:java\
        -Dexec.mainClass="main.Main" ##(這邊我不太確定是不是這樣寫)
 
 ##資料庫設計
 ER Diagram：詳Notion

 ##核心流程
 1.會員註冊/登入
   ．POST/member > 建立會員
   ．POST/member/login > 會員登入
 2.商品瀏覽
   ．GET/product >瀏覽商品列表
 3.加入購物車
   ．POST/cart >
     a.檢查商品是否已存在、is_active、庫存
     b.若已存在購物車提示更新商品
 4.查看/移除購物車
   ．GET/cart?member_id={id} >取得清單
   ．DELET/cart member_id = ? AND product_id = ? >移除購物車商品
 5.建立訂單
   ．POST/order
     a.從cart撈取商品
     b.檢查商品狀態＆庫存
     c.建立order&order_drtail
     d.從該會員購物車移除該商品
 6.訂單查詢
   ．GET/order/{member_id}
 7.管理員功能
   ．GET/member >瀏覽會員資訊(可by id)
   ．PUT/member >更新會員資訊
   ．POST/member >新增會員
   ．DELET/member >刪除會員
   ．GET/category >瀏覽商品類別
   ．PUT/category >更新商品類別
   ．POST/category >新增商品類別
   ．DELET/category >移除商品類別
   ．GET/product >瀏覽商品
   ．PUT/product >更新商品
   ．POST/product >新增商品
   ．DELET/product >移除商品
