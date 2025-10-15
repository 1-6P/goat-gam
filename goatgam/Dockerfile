# 1️⃣ Java 17 기반 슬림 이미지 사용
FROM openjdk:17-jdk-slim

# 2️⃣ 작업 디렉토리 지정
WORKDIR /app

# 3️⃣ 빌드 결과물 복사 (Gradle 빌드 후 build/libs에 생성됨)
COPY build/libs/*.jar app.jar

# 4️⃣ 8080 포트 노출
EXPOSE 8080

# 5️⃣ Spring Boot 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
