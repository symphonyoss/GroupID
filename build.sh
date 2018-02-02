#!/usr/bin/env bash
  
echo "Building group-id + helpdesk"
mvn clean install
  
echo "Building GroupID docker image"
echo "docker build -f docker/Dockerfile -t group-id ."
docker build -f docker/Dockerfile -t group-id .
