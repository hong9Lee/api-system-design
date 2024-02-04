FROM openjdk:17

COPY ../../web-api/build/libs/api.jar /api.jar
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-Xmx4g", "-jar", "/api.jar"]
