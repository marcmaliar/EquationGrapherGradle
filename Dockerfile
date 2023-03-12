# NOT WORKING

FROM gradle:latest

WORKDIR /usr/src/app

COPY . .

#RUN gradle build

#FROM eclipse-temurin:8u362-b09-jre-jammy
#FROM openjdk:8-jre-slim
CMD ["gradle", "run"]
#CMD [ "java", "-jar", "/usr/src/app/app/build/libs/app.jar" ]
