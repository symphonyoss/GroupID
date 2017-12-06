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

echo "Copy application files"
cp -f helpdesk-api/helpdesk-api.jar /opt/symphony/helpdesk
cp -f helpdesk-bot/helpdesk.jar /opt/symphony/helpdesk-bot
cp -f helpdesk-renderer/helpdesk-renderer.jar /opt/symphony/helpdesk-renderer

echo "Copy startup scripts"
cp -f helpdesk-api/startup.sh /data/symphony/helpdesk/bin
cp -f helpdesk-bot/startup.sh /data/symphony/helpdesk-bot/bin
cp -f helpdesk-renderer/startup.sh /data/symphony/helpdesk-renderer/bin

echo "Copy YAML descriptor"
cp -f helpdesk-bot/application.yaml /data/symphony/helpdesk-bot

echo "Copy service files"
cp -f helpdesk-api/helpdesk-api.service /etc/systemd/system
cp -f helpdesk-bot/helpdesk-bot.service /etc/systemd/system
cp -f helpdesk-renderer/helpdesk-renderer.service /etc/systemd/system

echo "Installation complete"