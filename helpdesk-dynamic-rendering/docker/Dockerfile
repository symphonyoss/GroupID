FROM openjdk:8-jdk-alpine

RUN apk add --no-cache bash && \
        rm -rf /tmp/* \
        /var/cache/*

COPY ./target/helpdesk-dynamic-rendering*.jar /opt/symphony/helpdesk-renderer/helpdesk-renderer.jar

WORKDIR /opt/symphony/helpdesk-renderer

ENTRYPOINT ["java", "-jar", "/opt/symphony/helpdesk-renderer/helpdesk-renderer.jar"]

EXPOSE 8080
