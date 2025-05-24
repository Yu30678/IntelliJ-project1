FROM eclipse-temurin:21-jdk

# 設定工作目錄
WORKDIR /app

# 複製 pom.xml 和 src 檔案
COPY pom.xml .
COPY src ./src

# 使用 Maven Wrapper 建構專案（你必須有 mvnw）
COPY mvnw .
COPY .mvn .mvn

RUN chmod +x mvnw
RUN ./mvnw package -DskipTests

#將images 資料夾拷貝到容器內 /opt/images
COPY src/main/resources/images /opt/images

# 執行 fat jar（如果是純 HttpServer 專案沒 Spring，請用 shade plugin）
CMD ["java", "-jar", "target/Backend_side_project-1.0-SNAPSHOT.jar"]