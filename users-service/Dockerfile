FROM adoptopenjdk:11-jre-hotspot

RUN mkdir /app
WORKDIR /app
ADD build/libs/users-service-*.jar users-service.jar

EXPOSE 8080

RUN groupadd -r badusers && useradd -r -g badusers badusers
USER badusers

ENTRYPOINT ["java","-jar","users-service.jar"]
