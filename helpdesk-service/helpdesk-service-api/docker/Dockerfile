FROM openjdk:8u131-jdk-alpine
  
RUN apk add --no-cache bash
  
COPY ./helpdesk-service/helpdesk-service-server/target/helpdesk-api.jar /opt/symphony/helpdesk/helpdesk-api.jar
  
ENV SERVER_ADDRESS 0.0.0.0
  
EXPOSE 8081
  
WORKDIR /opt/symphony/helpdesk
  
ENTRYPOINT [ "java", "-Xms512m", "-Xmx2g", "-jar", "/opt/symphony/helpdesk/helpdesk-api.jar" ]
