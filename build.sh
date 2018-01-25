#!/usr/bin/env bash

if [[ $# -lt 1 ]]
  then
    echo "Usage: build.sh <MONGO_HOST>"
    exit
fi

MONGO_HOST=$1
WORKSPACE="$(pwd)"
  
echo "Building group-id + helpdesk"
mvn clean install
  
echo "Building Helpdesk-api docker image"
echo "docker build -f helpdesk-service/helpdesk-service-api/docker/Dockerfile -t helpdesk-api  ."
docker build -f helpdesk-service/helpdesk-service-api/docker/Dockerfile -t helpdesk-api  .
 
echo "Building Helpdesk-bot docker image"
echo "docker build -f helpdesk-bot/docker/Dockerfile -t helpdesk_bot ."
docker build -f helpdesk-bot/docker/Dockerfile -t helpdesk-bot  .
 
echo "Building Helpdesk-renderer docker image"
echo "docker build -f helpdesk-application/docker/Dockerfile -t helpdesk-application ."
docker build -f helpdesk-application/docker/Dockerfile -t helpdesk-application  .
