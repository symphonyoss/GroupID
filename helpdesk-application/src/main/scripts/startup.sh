#!/usr/bin/env bash

JRE_HOME=/usr/java/jdk1.8.0_73/jre

$JRE_HOME/bin/java -jar /opt/symphony/helpdesk-renderer/helpdesk-renderer.jar \
    --logging.file=/data/symphony/helpdesk-renderer/logs/helpdesk-renderer.log
