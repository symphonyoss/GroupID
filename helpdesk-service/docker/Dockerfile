FROM openjdk:8-jdk-alpine

RUN apk add --no-cache bash && \
        rm -rf /tmp/* \
        /var/cache/*

COPY ./helpdesk-api.jar /opt/symphony/helpdesk-api/helpdesk-api.jar

WORKDIR /opt/symphony/helpdesk-api

ENTRYPOINT ["java", "-jar", "/opt/symphony/helpdesk-api/helpdesk-api.jar"]

EXPOSE 8080
