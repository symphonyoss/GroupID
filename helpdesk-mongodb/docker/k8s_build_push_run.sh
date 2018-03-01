#!/usr/bin/env bash
printf "Building MongoDB project [helpdesk-mongodb]\n"

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
