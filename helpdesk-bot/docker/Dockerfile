FROM openjdk:8u131-jdk-alpine
  
RUN apk add --no-cache bash
  
COPY ./helpdesk-bot/target/helpdesk-bot.jar  /opt/symphony/helpdesk-bot/helpdesk-bot.jar
  
ENV SERVER_ADDRESS 0.0.0.0
  
EXPOSE 8090
  
WORKDIR /opt/symphony/helpdesk-bot
  
ENTRYPOINT [ "java", "-Xms512m", "-Xmx2g", "-jar", "/opt/symphony/helpdesk-bot/helpdesk-bot.jar" ]
