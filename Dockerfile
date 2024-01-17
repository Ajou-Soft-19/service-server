FROM amazoncorretto:17
LABEL maintainer="jsm5315@ajou.ac.kr"

ARG JAR_FILE=build/libs/spring-0.0.1-SNAPSHOT.jar

WORKDIR /home/java/service

COPY ${JAR_FILE} /home/java/service/service-server.jar

EXPOSE 7001

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker","/home/java/service/service-server.jar"]