#!/usr/bin/env bash
<<<<<<< HEAD
  
=======

>>>>>>> df2d4f1aa832629c5d79f8f394ea0cf8e209bac4
echo "Building group-id + helpdesk"
mvn clean install
  
echo "Building GroupID docker image"
echo "docker build -f docker/Dockerfile -t group-id ."
docker build -f docker/Dockerfile -t group-id .
