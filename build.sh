#!/usr/bin/env bash
  
WORKSPACE="$(pwd)"
  
echo "Building group-id + helpdesk"
mvn clean install
  
echo "Building Helpdesk-api docker image"
echo "docker build -f helpdesk_Api/docker/Dockerfile -t helpdesk_api ."
docker build -f helpdesk_api/docker/Dockerfile -t helpdesk-api  .
 
echo "Building Helpdesk-bot docker image"
echo "docker build -f helpdesk_Bot/docker/Dockerfile -t helpdesk_bot ."
docker build -f helpdesk_Bot/docker/Dockerfile -t helpdesk_bot  .
 
echo "Building Helpdesk-renderer docker image"
echo "docker build -f helpdesk_Renderer/docker/Dockerfile -t helpdesk-renderer ."
docker build -f helpdesk_Renderer/docker/Dockerfile -t helpdesk-renderer  .
