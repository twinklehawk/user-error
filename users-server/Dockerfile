FROM openjdk:11

ENV BAD_USERS_VERSION 0.0.2

ADD build/libs/users-server-${BAD_USERS_VERSION}.jar ./users-server.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","users-server.jar"]
