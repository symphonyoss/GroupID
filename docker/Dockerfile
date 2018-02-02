FROM 189141687483.dkr.ecr.us-east-1.amazonaws.com/symphony-es/base-java8:latest

ARG JAR_VERSION

# set a more usable prompt - [username@container_name] cwd
RUN echo 'export PS1="[\u@\h] \W # "' >> /root/.bashrc

# openssl is needed to convert the PEM stored in consul to PKCS12
RUN apk add --no-cache openssl

COPY ./helpdesk-distribution/target/apps/helpdesk-api.jar      /opt/symphony/helpdesk/
COPY ./helpdesk-distribution/target/apps/helpdesk-bot.jar      /opt/symphony/helpdesk/
COPY ./helpdesk-distribution/target/apps/helpdesk-renderer.jar /opt/symphony/helpdesk/
COPY helpdesk-api.jinja      /opt/symphony/helpdesk/
COPY helpdesk-bot.jinja      /opt/symphony/helpdesk/
COPY helpdesk-renderer.jinja /opt/symphony/helpdesk/
COPY startup.sh              /opt/symphony/helpdesk/

RUN chmod +x /opt/symphony/helpdesk/startup.sh

WORKDIR /opt/symphony/helpdesk

EXPOSE 9443

ENTRYPOINT [ "/opt/symphony/helpdesk/startup.sh", "run" ]
