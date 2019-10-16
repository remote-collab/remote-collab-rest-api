FROM openjdk:8-jre-alpine

ENV SERVICE_NAME viper-service-admin

ADD target/${SERVICE_NAME}*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar" ]