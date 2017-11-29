# Symphony HelpDesk

This document provides a brief overview of Symphony HelpDesk components and how to build them from scratch.

# Overview

Symphony HelpDesk is composed by three cloud-native applications built on top of Symphony Boot.

The applications are:
* HelpDesk API: Responsible for tickets and memberships management
* HelpDesk Bot: Proxy messages between client and agents
* HelpDesk Dynamic Rendering: Provide message enrichers to enable agents interacts with the bot

# Build instructions for the Java developer

## What you’ll build
You’ll build the three java applications described above.

## What you’ll need
* JDK 1.8
* Maven 3.0.5+
* Node 6.10+
* Webpack (globally installed)

### Build with maven
These applications are compatible with Apache Maven 3.0.5 or above. If you don’t already have Maven installed you can
follow the instructions at maven.apache.org.

To start from scratch, do the following:

1. Clone the source repository using Git: `git clone git@github.com:SymphonyOSF/HelpDeskBot.git`
2. cd into _HelpDeskBot_
3. Build using maven: `mvn clean install`

### Deploy manually on dev environment

- Download helpdesk-${version}-${build-number}.tar.gz from Artifactory and extract it into the host machine.
- Run the install.sh script
- Go to /data/symphony/helpdesk/bin/ directory and edit the startup.sh file pointing the correct java path and mongo host.
- Start the helpdesk-api service: ```service helpdesk-api start```
- Go to /data/symphony/helpdesk-bot/bin/ directory and edit the startup.sh file pointing the correct java.
- Edit the application.yaml to put the correct service endpoints and also the streamId to gather all tickets.
- Start the helpdesk-bot service: ```service helpdesk-bot start```
- Go to /data/symphony/helpdesk-renderer/bin/ directory and edit the startup.sh file pointing the correct java.
- Start the helpdesk-renderer service: ```service helpdesk-renderer start```