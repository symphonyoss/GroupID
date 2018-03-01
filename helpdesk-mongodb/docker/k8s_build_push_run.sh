#!/usr/bin/env bash
printf "Building MongoDB project [helpdesk-mongodb]\n\n"
VERSION=1.0.0
printf "Project version: $(printf $VERSION)\n\n"

printf "Pulling Docker Ubuntu base image\n\n"
docker pull ubuntu

printf "Building docker image [helpdesk-mongodb] for GCE\n"
printf "docker build -t gcr.io/${PROJECT_ID}/helpdesk-mongodb:$VERSION .\n\n"
export PROJECT_ID="$(gcloud config get-value project -q)"
docker build -f Dockerfile -t gcr.io/${PROJECT_ID}/helpdesk-mongodb:$VERSION .

printf "Uploading docker image [helpdesk-mongodb] on GCE\n"
printf "gcloud docker -- push gcr.io/${PROJECT_ID}/helpdesk-mongodb:$VERSION\n"
gcloud docker -- push gcr.io/${PROJECT_ID}/helpdesk-mongodb:$VERSION

printf "\nDeleting previous service on kubernetes\n"
printf "kubectl delete service helpdesk-mongodb-service\n"
kubectl delete service helpdesk-mongodb-service

printf "\nDeleting previous deployment on kubernetes\n"
printf "kubectl delete deployment helpdesk-mongodb\n"
kubectl delete deployment helpdesk-mongodb

printf "\nCreating a new deployment on kubernetes\n"
printf "kubectl create -f k8s_deployment.yaml\n"
kubectl create -f k8s_deployment.yaml

printf "\nCreating a new service on kubernetes\n"
printf "kubectl create -f k8s_service.yaml\n"
kubectl create -f k8s_service.yaml
