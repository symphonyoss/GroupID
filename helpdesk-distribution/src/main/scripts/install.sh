#!/usr/bin/env bash

if [ `whoami` != 'root' ]
  then
    echo "You must be root to do this."
    exit
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
cp -f helpdesk-api/startup.sh /data/symphony/helpdesk/bin
cp -f helpdesk-bot/startup.sh /data/symphony/helpdesk-bot/bin
cp -f helpdesk-renderer/startup.sh /data/symphony/helpdesk-renderer/bin

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

sleep 5

echo "Starting services"
systemctl daemon-reload

service helpdesk-api start
sleep 3
service helpdesk-renderer start
sleep 3
service helpdesk-bot start

echo "Installation complete"
