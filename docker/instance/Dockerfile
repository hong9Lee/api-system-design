# Java 17 베이스 이미지 사용
FROM openjdk:17

# 애플리케이션 파일 복사
COPY ../../web-api/build/libs/api.jar api.jar

ENTRYPOINT ["java", "-jar", "/api.jar"]
