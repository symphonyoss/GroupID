#!/usr/bin/env bash

if [ `whoami` != 'root' ]
  then
    echo "You must be root to do this."
    exit
fi

if [[ $# -lt 2 ]]
  then
    echo "Usage: install.sh <MONGO_HOST> <JAVA_HOME>"
    exit
fi

MONGO_HOST=$1
JAVA_HOME=$2

if [[ -f $JAVA_HOME/bin/java ]]
  then
    JAVA_PATH=$JAVA_HOME/bin/java
elif [[ -f $JAVA_HOME/jre/bin/java ]]
  then
    JAVA_PATH=$JAVA_HOME/jre/bin/java
else
  if [[ -d $JAVA_HOME ]]
  then
    echo "[ERROR] Cannot find JRE at file path '$JAVA_HOME/bin/java'"
  else
    echo "[ERROR] Missing JRE location path"
  fi
  exit 1
fi

echo "Create directories"
mkdir -p /opt/symphony/helpdesk
mkdir -p /data/symphony/helpdesk/bin
mkdir -p /data/symphony/helpdesk/logs

mkdir -p /opt/symphony/helpdesk-bot
mkdir -p /data/symphony/helpdesk-bot/bin
mkdir -p /data/symphony/helpdesk-bot/logs

mkdir -p /opt/symphony/helpdesk-renderer
mkdir -p /data/symphony/helpdesk-renderer/bin
mkdir -p /data/symphony/helpdesk-renderer/logs

echo "Stopping services"
service helpdesk-api stop
service helpdesk-bot stop
service helpdesk-renderer stop

echo "Copy application files"
cp -f helpdesk-api/helpdesk-api.jar /opt/symphony/helpdesk
cp -f helpdesk-bot/helpdesk-bot.jar /opt/symphony/helpdesk-bot
cp -f helpdesk-renderer/helpdesk-renderer.jar /opt/symphony/helpdesk-renderer

echo "Copy startup scripts"
cp -f helpdesk-api/startup.sh.template /data/symphony/helpdesk/bin/startup.sh
sed -i "s|{{ mongo_host }}|$MONGO_HOST|g" /data/symphony/helpdesk/bin/startup.sh
sed -i "s|{{ java_path }}|$JAVA_PATH|g" /data/symphony/helpdesk/bin/startup.sh

cp -f helpdesk-bot/startup.sh.template /data/symphony/helpdesk-bot/bin/startup.sh
sed -i "s|{{ java_path }}|$JAVA_PATH|g" /data/symphony/helpdesk-bot/bin/startup.sh

cp -f helpdesk-renderer/startup.sh.template /data/symphony/helpdesk-renderer/bin/startup.sh
sed -i "s|{{ java_path }}|$JAVA_PATH|g" /data/symphony/helpdesk-renderer/bin/startup.sh

yamlfile=/data/symphony/helpdesk-bot/application.yaml
if [ -f "$yamlfile" ]
then
    echo "YAML descriptor already exists."
else
    echo "Copy YAML descriptor"
    cp -f helpdesk-bot/application.yaml /data/symphony/helpdesk-bot
fi

echo "Copy service files"
cp -f helpdesk-api/helpdesk-api.service /etc/systemd/system
cp -f helpdesk-bot/helpdesk-bot.service /etc/systemd/system
cp -f helpdesk-renderer/helpdesk-renderer.service /etc/systemd/system

systemctl daemon-reload

sleep 5

echo "Starting services"
service helpdesk-api start
sleep 3
service helpdesk-renderer start
sleep 3
service helpdesk-bot start

echo "Installation complete"
