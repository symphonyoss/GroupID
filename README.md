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
* OpenSSL
* jq (https://stedolan.github.io/jq/)

### Build with maven
These applications are compatible with Apache Maven 3.0.5 or above. If you don’t already have Maven installed you can follow the instructions at maven.apache.org.

To start from scratch, do the following:

1. Clone the source repository using Git: `git clone git@github.com:symphonyoss/GroupId.git`
2. cd into _GroupId_
3. Build using maven: `mvn clean install -P symphony-app-auth`

### On-premise deployment

- Download helpdesk-${version}-${build-number}.tar.gz from Artifactory and extract it into the host machine.

- Edit helpdesk-api/application.yaml to specify the the service endpoint URLs.

- Edit helpdesk-bot/application.yaml to specify the cert path for the HelpDesk bot user, the service endpoint URLs, and the streamId of the agent room in which to display the tickets.

- Run the install.sh script providing the mongo host and java home directory as parameters: ```./install.sh sym-nexus2-dev-chat-glb-2-guse1-all.symphony.com /usr/lib/jvm/java-8-oracle```

- Check if all helpdesk services are running properly
1. ```service helpdesk-api status```
2. ```service helpdesk-bot status```
3. ```service helpdesk-renderer status```

### Getting Started

#### Prepare environment

This process should be executed only once in the developer machine.

- Go to the 'scripts/local-run' directory
- Run **prepare_environment.sh** script to generate a self-signed keystore to run applications using SSL

**Important**: You'll be asked to provide the root password. It's required because we need to import the generated certificate into
the JDK keystore.

#### POD provisioning

This process should be executed to create the required artifacts to run the application and also to setup the POD properly.

If you're an admin user in the POD, please follow these steps:
 * Go to AC Portal
 * Create new service account for your bot
 * Run **generate_bot_keystore.sh** script to generate bot p12 file. You must provide service account username and environment as script parameters
```
./generate_bot_keystore.sh --env nexus1 --user helpdesk
```
 * Import 'certs/${env}/helpdesk-root.pem' file into the POD. You can make it in the AC Portal -> Manage Certificates

Otherwise, you need to ask the administrator to create new service account and give you the certificate for this
account. Then, you should copy this file to 'certs/${env}' directory.

After that, you must run the **create_agent_room.sh** script providing the environment, room name, room description and  your user id. This script will create the agent queue room and configure the stream ID in the YAML config file
```
./create_agent_room.sh --env nexus1 --name "Room Name" --description "Room Description" --agent userID
```

## Execute applications

### Scripts

- Build project using maven: `mvn clean install -P symphony-app-auth`
- Go to 'scripts/local-run/service' directory. Executes the **startup.sh** script to run the helpdesk service application
- Go to 'scripts/local-run/bot' directory. Executes the **startup.sh** script to run the helpdesk bot application
- Go to 'scripts/local-run/app' directory. Executes the **startup.sh** script to run the helpdesk renderer application

**Important:** All applications have their own shutdown scripts.

### IntelliJ IDEA

- Copy run configurations from 'scripts/local-run/idea' directory to '.idea/runConfigurations'
- Execute HelpDeskService configuration
- Execute HelpDeskBot configuration

### Validate applications

- Access URL: 'https://localhost.symphony.com:8100/helpdesk-renderer/bundle.json'. Make sure this URL is reachable
- Access Symphony Client providing the bundle JSON as query string.

Example:
```
https://nexus4-2.symphony.com/client/?bundle=https://localhost.symphony.com:8100/helpdesk-renderer/bundle.json
```

- Make sure your user is member of the queue room
- Go to the Symphony Market and add 'Help Desk' application
- Open a new browser and sign in with a different user (ask the administrator if required). Start a direct chat
between you and the bot
- New ticket should be created and posted in the queue. Your user should be able to claim this ticket