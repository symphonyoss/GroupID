#!/usr/bin/env bash
printf "Building java project [helpdesk-bot]\n\n"

printf "Get current project version and replace it inside the build manifest file\n"
cd ..
cp target/helpdesk-bot*.jar docker/helpdesk-bot.jar
VERSION=$(grep -Po -m 1 '<version>\K[^<]*' pom.xml)
cd -
printf "Project version: $(printf $VERSION)\n\n"

DEPLOYMENT_TEMPLATE_FILE=$1
if [ -z "$DEPLOYMENT_TEMPLATE_FILE" ]
then
  DEPLOYMENT_TEMPLATE_FILE = "k8s_deployment.yaml.template"
fi
printf "Using template file [$DEPLOYMENT_TEMPLATE_FILE].\n"
cp $DEPLOYMENT_TEMPLATE_FILE k8s_deployment.yaml
sed -i -- "s/<VERSION>/$(printf $VERSION)/g" k8s_deployment.yaml

printf "Pulling Docker Java8 base image\n\n"
docker pull openjdk:8-jdk-alpine

printf "Building docker image [helpdesk-bot] for GCE\n"
printf "docker build -t gcr.io/${PROJECT_ID}/helpdesk-bot:$VERSION .\n\n"
export PROJECT_ID="$(gcloud config get-value project -q)"
docker build -f Dockerfile -t gcr.io/${PROJECT_ID}/helpdesk-bot:$VERSION .

printf "Uploading docker image [helpdesk-bot] on GCE\n"
printf "gcloud docker -- push gcr.io/${PROJECT_ID}/helpdesk-bot:$VERSION\n"
gcloud docker -- push gcr.io/${PROJECT_ID}/helpdesk-bot:$VERSION

printf "\nDeleting previous service on kubernetes\n"
printf "kubectl delete service helpdesk-bot-service\n"
kubectl delete service helpdesk-bot-service

printf "\nDeleting previous deployment on kubernetes\n"
printf "kubectl delete deployment helpdesk-bot\n"
kubectl delete deployment helpdesk-bot

printf "\nCreating a new deployment on kubernetes\n"
printf "kubectl create -f k8s_deployment.yaml\n"
kubectl create -f k8s_deployment.yaml

printf "\nCreating a new service on kubernetes\n"
printf "kubectl create -f k8s_service.yaml\n"
kubectl create -f k8s_service.yaml

printf "\nClearing generated files\n"
rm -rf helpdesk-bot.jar
rm -rf k8s_deployment.yaml
