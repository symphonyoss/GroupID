#!/usr/bin/env bash

JRE_HOME=/usr/java/jdk1.8.0_73/jre

$JRE_HOME/bin/java -jar /opt/symphony/helpdesk-bot/helpdesk.jar \
    --spring.config.location=/data/symphony/helpdesk-bot/application.yaml \
    --logging.file=/data/symphony/helpdesk-bot/logs/helpdesk-bot.log
