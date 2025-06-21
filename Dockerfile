# 第一階段：使用 Maven 編譯並產生 fat-jar
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 第二階段：僅用 JRE 執行
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 從 builder 階段複製包含所有依賴的 Jar，並重新命名為 app.jar
COPY --from=builder /build/target/Backend_side_project-1.0-SNAPSHOT-jar-with-dependencies.jar ./app.jar

# 創建圖片存儲目錄
RUN mkdir -p /app/images

# 複製原有圖片到容器
COPY src/main/resources/images/* /app/images/

# 對外開放應用程式監聽的埠號（與 SERVER_PORT 一致）
EXPOSE 8080

# 啟動指令
ENTRYPOINT ["java", "-jar", "/app/app.jar"]