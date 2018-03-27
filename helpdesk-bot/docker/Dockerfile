FROM openjdk:8-jdk-alpine

#install bash and curl
RUN apk add --no-cache bash && \
         apk add --no-cache curl && \
         rm -rf /tmp/* \
         /var/cache/*

# download Java Cryptography Extension
RUN cd /tmp/ && \
    curl -LO "http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip" -H 'Cookie: oraclelicense=accept-securebackup-cookie' && \
    unzip jce_policy-8.zip && \
    rm jce_policy-8.zip && \
    yes |cp -v /tmp/UnlimitedJCEPolicyJDK8/*.jar /usr/lib/jvm/java-1.8-openjdk/jre/lib/security

COPY ./helpdesk-bot.jar /opt/symphony/helpdesk-bot/helpdesk-bot.jar

WORKDIR /opt/symphony/helpdesk-bot

ENTRYPOINT ["java", "-jar", "/opt/symphony/helpdesk-bot/helpdesk-bot.jar"]

EXPOSE 8080
