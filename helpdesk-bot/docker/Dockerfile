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

# Install Trusted CA Certs
RUN set -x \
    && curl -L -o /tmp/gdig2.crt https://s3.amazonaws.com/sym-infra-public/certs/gdig2.crt \
    && curl -L -o /tmp/ES_Root_CA_Dev.crt https://s3.amazonaws.com/sym-infra-public/certs/ES_Root_CA_Dev.crt \
    && curl -L -o /tmp/ES_Root_CA_Prod.crt https://s3.amazonaws.com/sym-infra-public/certs/ES_Root_CA_Prod.crt \
    && curl -L -o /tmp/symphony-ca-dev-qa.crt https://s3.amazonaws.com/sym-infra-public/certs/symphony-ca-dev-qa.crt \
    && curl -L -o /tmp/symphony-ca-prod-uat.crt https://s3.amazonaws.com/sym-infra-public/certs/symphony-ca-prod-uat.crt \
    && curl -L -o /tmp/symphony-intermediate-infra1-dev-qa.crt https://s3.amazonaws.com/sym-infra-public/certs/symphony-intermediate-infra1-dev-qa.crt \
    && curl -L -o /tmp/symphony-intermediate-infra2-dev-qa.crt https://s3.amazonaws.com/sym-infra-public/certs/symphony-intermediate-infra2-dev-qa.crt \
    && curl -L -o /tmp/symphony-intermediate-infra1-prod-uat.crt https://s3.amazonaws.com/sym-infra-public/certs/symphony-intermediate-infra1-prod-uat.crt \
    && curl -L -o /tmp/symphony-intermediate-infra2-prod-uat.crt https://s3.amazonaws.com/sym-infra-public/certs/symphony-intermediate-infra2-prod-uat.crt \
    && /usr/lib/jvm/java-1.8-openjdk/bin/keytool -importcert -file /tmp/gdig2.crt -alias godaddy_gdig2 -keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts -storepass changeit -trustcacerts -noprompt \
    && /usr/lib/jvm/java-1.8-openjdk/bin/keytool -importcert -file /tmp/ES_Root_CA_Dev.crt -alias es_root_ca_dev -keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts -storepass changeit -trustcacerts -noprompt \
    && /usr/lib/jvm/java-1.8-openjdk/bin/keytool -importcert -file /tmp/ES_Root_CA_Prod.crt -alias es_root_ca_prod -keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts -storepass changeit -trustcacerts -noprompt \
    && /usr/lib/jvm/java-1.8-openjdk/bin/keytool -importcert -file /tmp/symphony-ca-dev-qa.crt -alias sym_ca_dev_qa -keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts -storepass changeit -trustcacerts -noprompt \
    && /usr/lib/jvm/java-1.8-openjdk/bin/keytool -importcert -file /tmp/symphony-ca-prod-uat.crt -alias sym_ca_prod_uat -keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts -storepass changeit -trustcacerts -noprompt \
    && /usr/lib/jvm/java-1.8-openjdk/bin/keytool -importcert -file /tmp/symphony-intermediate-infra1-dev-qa.crt -alias sym_infra1_dev_qa -keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts -storepass changeit -trustcacerts -noprompt \
    && /usr/lib/jvm/java-1.8-openjdk/bin/keytool -importcert -file /tmp/symphony-intermediate-infra2-dev-qa.crt -alias sym_infra2_dev_qa -keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts -storepass changeit -trustcacerts -noprompt \
    && /usr/lib/jvm/java-1.8-openjdk/bin/keytool -importcert -file /tmp/symphony-intermediate-infra1-prod-uat.crt -alias sym_infra1_prod_uat -keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts -storepass changeit -trustcacerts -noprompt \
    && /usr/lib/jvm/java-1.8-openjdk/bin/keytool -importcert -file /tmp/symphony-intermediate-infra2-prod-uat.crt -alias sym_infra2_prod_uat -keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts -storepass changeit -trustcacerts -noprompt \
    && rm -rf /tmp/*

COPY ./helpdesk-bot.jar /opt/symphony/helpdesk-bot/helpdesk-bot.jar

WORKDIR /opt/symphony/helpdesk-bot

ENTRYPOINT ["java", "-jar", "/opt/symphony/helpdesk-bot/helpdesk-bot.jar"]

EXPOSE 8080
