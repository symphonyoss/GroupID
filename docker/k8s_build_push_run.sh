#!/usr/bin/env bash
printf "\n\n******** Bulding Java Projects of GroupID ********\n\n"
pwd
cd ..
#mvn clean install
pwd

printf "\n\n******** Bulding Helpdesk MongoDB ********\n\n"
cd helpdesk-mongodb/docker
pwd
./k8s_build_push_run.sh

printf "\n\n******** Bulding Helpdesk API ********\n\n"
cd ../../helpdesk-service/docker
pwd
./k8s_build_push_run.sh

printf "\n\n******** Bulding Helpdesk Renderer ********\n\n"
cd ../../helpdesk-dynamic-rendering/docker
pwd
./k8s_build_push_run.sh

printf "\n\n******** Bulding Helpdesk BOT ********\n\n"
cd ../../helpdesk-bot/docker
pwd
./k8s_build_push_run.sh

cd ../../
pwd
