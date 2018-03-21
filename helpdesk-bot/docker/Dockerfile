FROM openjdk:8-jdk-alpine

RUN apk add --no-cache bash && \
        rm -rf /tmp/* \
        /var/cache/*

COPY ./helpdesk-bot.jar /opt/symphony/helpdesk-bot/helpdesk-bot.jar

WORKDIR /opt/symphony/helpdesk-bot

ENTRYPOINT ["java", "-jar", "/opt/symphony/helpdesk-bot/helpdesk-bot.jar"]

EXPOSE 8080
