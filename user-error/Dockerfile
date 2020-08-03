FROM adoptopenjdk:11-jre-hotspot

RUN mkdir /app
WORKDIR /app
ADD build/libs/user-error-*.jar user-error.jar

EXPOSE 8080

RUN groupadd -r usererror && useradd -r -g usererror usererror
USER usererror

ENTRYPOINT ["java","-jar","user-error.jar"]
