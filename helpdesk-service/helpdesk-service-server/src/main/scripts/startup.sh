#!/usr/bin/env bash

JRE_HOME=/usr/java/jdk1.8.0_73/jre
MONGO_HOST=sym-nexus2-dev-chat-glb-2-guse1-all.symphony.com

$JRE_HOME/bin/java -jar /opt/symphony/helpdesk/helpdesk-api.jar \
    --helpdesk.mongo.host=$MONGO_HOST \
    --logging.file=/data/symphony/helpdesk/logs/helpdesk-api.log