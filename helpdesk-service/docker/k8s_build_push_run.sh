#!/usr/bin/env bash
printf "Building java project [helpdesk-api]\n\n"
mvn clean install

printf "Get current project version and replace it inside the build manifest file\n"
VERSION=$(grep -Po -m 1 '<version>\K[^<]*' pom.xml)
printf "Project version: $(printf $VERSION)\n\n"

cp k8s_deployment.yaml.template k8s_deployment.yaml
sed -i -- "s/<VERSION>/$(printf $VERSION)/g" k8s_deployment.yaml

printf "Pulling Docker Java8 base image\n\n"
docker pull openjdk:8-jdk-alpine

printf "Building docker image [helpdesk-api] for GCE\n"
printf "docker build -t gcr.io/${PROJECT_ID}/helpdesk-api:$VERSION .\n\n"
export PROJECT_ID="$(gcloud config get-value project -q)"
docker build -f Dockerfile -t gcr.io/${PROJECT_ID}/helpdesk-api:$VERSION .

printf "Uploading docker image [helpdesk-api] on GCE\n"
printf "gcloud docker -- push gcr.io/${PROJECT_ID}/helpdesk-api:$VERSION\n"
gcloud docker -- push gcr.io/${PROJECT_ID}/helpdesk-api:$VERSION

printf "\nDeleting previous service on kubernetes\n"
printf "kubectl delete service helpdesk-api-service\n"
kubectl delete service helpdesk-api-service

printf "\nDeleting previous deployment on kubernetes\n"
printf "kubectl delete deployment helpdesk-api\n"
kubectl delete deployment helpdesk-api

printf "\nCreating a new deployment on kubernetes\n"
printf "kubectl create -f k8s_deployment.yaml\n"
kubectl create -f k8s_deployment.yaml

printf "\nCreating a new service on kubernetes\n"
printf "kubectl create -f k8s_service.yaml\n"
kubectl create -f k8s_service.yaml
