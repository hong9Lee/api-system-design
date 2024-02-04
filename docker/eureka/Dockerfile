FROM openjdk:17

COPY ../../web-eureka/build/libs/eureka.jar eureka-server.jar

ENTRYPOINT ["java", "-Xmx4g", "-jar", "/eureka-server.jar"]
