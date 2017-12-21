# Symphony HelpDesk

This document provides a brief overview of Symphony HelpDesk components and how to build them from scratch.

# Overview

Symphony HelpDesk is composed by three cloud-native applications built on top of Symphony Boot.

The applications are:
* HelpDesk API: Manages tickets and stream membership
* HelpDesk Bot: Proxies messages between clients and agents
* HelpDesk Dynamic Rendering: Provides message enrichers to enable agents to interact with the bot

# Build instructions for the Java developer

## What you’ll build
You’ll build the three java applications described above.

## What you’ll need
* JDK 1.8
* Maven 3.0.5+
* Node 6.10+
* MongoDB
* Webpack (globally installed)
* A service user on the pod that will act as the HelpDesk bot
* A cert for the HelpDesk bot user
* HelpDesk App installed on pod ([Bundle File](/helpdesk-dynamic-rendering/src/main/webapp/bundle.json))

### Build with maven
These applications are compatible with Apache Maven 3.0.5 or above. If you don’t already have Maven installed you can follow the instructions at maven.apache.org.

To start from scratch, do the following:

1. Clone the source repository using Git: `git clone git@github.com:SymphonyOSF/HelpDeskBot.git`
2. cd into _HelpDeskBot_
3. Build using maven: `mvn clean install`

### Deploy manually on dev environment

- Download helpdesk-${version}-${build-number}.tar.gz from Artifactory and extract it into the host machine.

- Edit helpdesk-bot/application.yaml to specify the cert path for the HelpDesk bot user, the service endpoint URLs, and the streamId of the agent room in which to display the tickets.

- Run the install.sh script providing the mongo host and java home directory as parameters: ```./install.sh sym-nexus2-dev-chat-glb-2-guse1-all.symphony.com /usr/lib/jvm/java-8-oracle```

- Check if all helpdesk services are running properly
1. ```service helpdesk-api status```
2. ```service helpdesk-bot status```
3. ```service helpdesk-renderer status```
