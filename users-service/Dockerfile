FROM adoptopenjdk:11-jre-hotspot

ENV BAD_USERS_VERSION 0.3.1

RUN mkdir /app
WORKDIR /app
ADD build/libs/users-service-${BAD_USERS_VERSION}.jar users-service.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","users-service.jar"]
