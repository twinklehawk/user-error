FROM openjdk:11

ENV BAD_USERS_VERSION 0.0.2

ADD build/libs/users-service-${BAD_USERS_VERSION}.jar ./users-service.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","users-service.jar"]
