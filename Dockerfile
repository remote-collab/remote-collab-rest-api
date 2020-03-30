FROM openjdk:8-jre-alpine

ENV ENABLE_DATADOG=true

ENV SERVICE_NAME remote-collab-api

ADD target/${SERVICE_NAME}*.jar /app.jar

COPY ./docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]