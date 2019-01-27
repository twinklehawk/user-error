FROM openjdk:8

ENV BAD_USERS_VERSION 0.0.2

ADD build/libs/users-server-${BAD_USERS_VERSION}.jar ./users-server.jar

EXPOSE 8080

CMD ["java","-jar","users-server.jar"]
