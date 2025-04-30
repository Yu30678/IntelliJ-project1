##  專案概要


- 會員管理：註冊、登入
- 商品管理：瀏覽商品列表
- 購物車功能：加入、查詢、更新、刪除
- 訂單管理：建立、查詢
- 後台管理：會員、商品、商品類別、訂單的CRUD操作

## 技術棧

- 程式語言Java 21
- HTTP 服務Java 原生 httpserver
- JSON 處理Gson
- 資料庫MySQL 9.2.0、JDBC Driver
- 專案管理Maven

## 專案架構
```
Backend_side_project/
├── pom.xml
├── src/main/java/
│   ├── Main.java           # 啟動 HttpServer
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
│   │   ├── Cart.java
│   │   ├── Member.java
│   │   ├── Product.java
│   │   ├── Category.java
│   │   ├── User.java
│   │   ├── Order.java
│   │   └── OrderDetail.java
│   ├── server/
│   │   └── WebServer.java
│   └── util/
│       ├── DBUtil.java               # 連線池與連線關閉工具
│       ├── LocalDateTimeAdapter.java # 處理時間序列化與反序列化操作
│       └── GsonUtil.java             # Gson 共用工具
```

## 環境設置與啟動

1. 資料庫建立：已完成
2. 編譯與啟動
- 打包
  bashmvn clean package

- 執行 JAR 檔
  java -jar target/Backend_side_project.jar

- 開發模式
  mvn compile #編譯原始碼

  mvn exec:java\
     Dexec.mainClass="main.Main" #(這邊我不太確定是不是這樣寫)



## 資料庫設計

ER Diagram：詳見 Notion 文件

## 核心流程

1. 會員註冊/登入

- POST /member - 建立會員
- POST /member/login - 會員登入

2. 商品瀏覽

- GET /product - 瀏覽商品列表

3. 加入購物車

- POST /cart

  檢查商品是否已存在、is_active 狀態、庫存數量
  若商品已存在購物車，提示更新商品



4. 查看/移除購物車

- GET /cart?member_id={id} - 取得購物車清單
- DELETE /cart (參數：member_id、product_id) - 移除購物車商品

5. 建立訂單

- POST /order

  從購物車撈取商品
  檢查商品狀態與庫存
  建立 order 與 order_detail
  從該會員購物車移除該商品
 


6. 訂單查詢

- GET /order/{member_id} - 查詢會員訂單

7. 管理員功能

* 會員管理

  1. GET /member - 瀏覽會員資訊 (可依 ID 查詢)
  2. PUT /member - 更新會員資訊
  3. POST /member - 新增會員
  4. DELETE /member - 刪除會員


* 商品類別管理

  1. GET /category - 瀏覽商品類別
  2. PUT /category - 更新商品類別
  3. POST /category - 新增商品類別
  4. DELETE /category - 移除商品類別


* 商品管理

  1. GET /product - 瀏覽商品
  2. PUT /product - 更新商品
  3. POST /product - 新增商品
  4. DELETE /product - 移除商品


* 訂單管理

  1. GET /order - 瀏覽訂單資訊
