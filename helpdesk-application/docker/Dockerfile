FROM openjdk:8u131-jdk-alpine
  
RUN apk add --no-cache bash
  
COPY ./helpdesk-application/target/helpdesk-renderer.jar  /opt/symphony/helpdesk-renderer/helpdesk-renderer.jar
  
ENV SERVER_ADDRESS 0.0.0.0
  
EXPOSE 8100
  
WORKDIR /opt/symphony/helpdesk-bot
  
ENTRYPOINT [ "java", "-Xms512m", "-Xmx2g", "-jar", "/opt/symphony/helpdesk-renderer/helpdesk-renderer.jar" ]
