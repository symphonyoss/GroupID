# Symphony HelpDesk

This document provides a brief overview of the Symphony HelpDesk components and how to build them from scratch.

# Overview

Symphony HelpDesk is composed of three cloud-native applications built on top of Symphony Boot.

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
* A cert for the HelpDesk application
* HelpDesk App installed on pod ([Bundle File](/helpdesk-dynamic-rendering/src/main/webapp/bundle.json))
* OpenSSL
* jq (https://stedolan.github.io/jq/)
* Java Cryptography Extension Unlimited Strength Jurisdiction Policy ([JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html))

### Build with maven
These applications are compatible with Apache Maven 3.0.5 or above. If you don’t already have Maven installed you can follow the instructions at maven.apache.org.

To start from scratch, do the following:

1. Clone the source repository using Git: `git clone git@github.com:symphonyoss/GroupId.git`
2. cd into _GroupId_
3. Build using maven: `mvn clean install`

### On-premise deployment

- After executing the [build steps](#build-with-maven) go to directory helpdesk-distribution/target directory and copy the file helpdesk-${version}.tar.gz to a new folder. After this, extract it.

- After the extration three directories will be created: helpdesk-api, helpdesk-bot and helpdesk-renderer.

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
 * Run **generate_keystores.sh** script to generate bot and application p12 files. You must provide service account username and environment as script parameters
```
./generate_keystores.sh --configName myPodConfig --user helpdesk
```
 * Import 'certs/${env}/helpdesk-root.pem' file into the POD. You can make it in the AC Portal -> Manage Certificates

Otherwise, you need to ask the administrator to create new service account and give you the certificate for this
account. Then, you should copy this file to 'certs/${env}' directory.

After that, you must run the **create_agent_room.sh** script providing the environment, room name, room description and  your user id. This script will create the agent queue room and configure the stream ID in the YAML config file
```
./create_agent_room.sh --podAddr pod.symphony.com --sessionAuthFQDN session.auth.fullyq.domain.name.symphony.com --sessionAuthPort 8444 --configName myPodConfig --name "Room Name" --description "Room Description" --agent userID
```
## Execute applications

### Scripts

- Build project using maven: `mvn clean install`
- Go to 'scripts/local-run/service' directory. Executes the **startup.sh** script to run the helpdesk service application
- Go to 'scripts/local-run/bot' directory. Executes the **startup.sh** script to run the helpdesk bot application
- Go to 'scripts/local-run/app' directory. Executes the **startup.sh** script to run the helpdesk renderer application

**Important:** All applications have their own shutdown scripts.

### IntelliJ IDEA

- Copy run configurations from 'scripts/local-run/idea' directory to '.idea/runConfigurations'
- Execute HelpDeskService configuration
- Execute HelpDeskBot configuration
- Execute HelpDeskRenderer configuration

### Command Line
- It's possible to run the Helpdesk Renderer app in watch mode:
1. Go to the 'helpdesk-dynamic-rendering' directory.
2. Execute ```npm run watch```.
3. Access the helpdesk-renderer URL to validate if it's served properly as described below.

### Validate applications

- Access this in your Chrome browser: 'chrome://flags/#allow-insecure-localhost'. You should see highlighted
text saying: Allow invalid certificates for resources loaded from localhost. Click 'Enable'
- Access URL: 'https://localhost:8100/helpdesk-renderer/bundle.json'. Make sure this URL is reachable
- Access Symphony Client providing the bundle JSON as query string.

Example:
```
https://nexus4-2.symphony.com/client/?bundle=https://localhost:8100/helpdesk-renderer/bundle.json
```

- Make sure your user is member of the queue room
- Go to the Symphony Market and add 'Help Desk' application
- Open a new browser and sign in with a different user (ask the administrator if required). Start a direct chat
between you and the bot
- New ticket should be created and posted in the queue. Your user should be able to claim this ticket

## Running integration tests

In order to validate provided endpoints, we have created a set of BDD tests using JBehave. The integration tests for
HelpDesk API are always executed during the build process (in the integration-test phase) because we don't need
external dependencies (there is an embedded mongo to validate Mongo DAO's).

To run the integration tests for HelpDesk bot you must follow above instructions in order to know how to setup the
dependencies the application requires.

**Important**: You must have a trusted certificate that have already imported on POD (see POD provisioning).

### Nexus environments

You should execute the build process using the parameters:

```
mvn -DcaKeyPath=./scripts/local-run/certs/${env}/helpdesk-root-key.pem -DcaCertPath=
./scripts/local-run/certs/${env}/helpdesk-root.pem -Dspring.profiles.active=${env} clean install
```

You must replace ${env} variable with the environment name you wish.

### Other POD's

You should create a new YAML file to specify the service endpoint URLs.

Example:

```
agent:
  host: foundation-dev.symphony.com
  port: 443

session_auth:
  host: foundation-dev-api.symphony.com
  port: 443

key_auth:
  host: foundation-dev-api.symphony.com
  port: 443

pod:
  host: foundation-dev.symphony.com
  port: 443

helpdesk-service:
  host: localhost
  port: 8081
```

Then you should execute the build process using the parameters:

```
mvn -DcaKeyPath=${PATH_TO_CERTS}/helpdesk-root-key.pem -DcaCertPath=${PATH_TO_CERTS}/helpdesk-root.pem -Dspring.config.location=${PATH_TO_YAML} clean install
```

You must replace ${PATH_TO_CERTS} variable with the certificate directory and ${PATH_TO_YAML} with the YAML absolute path.


## Containerized Deployment

### Docker and Kubernetes

This application is setup to be deployed via Docker and is divided into four containers:

1. [helpdesk-mongodb](https://github.com/symphonyoss/GroupID/tree/dev/helpdesk-mongodb/docker)
2. [helpdesk-api](https://github.com/symphonyoss/GroupID/tree/dev/helpdesk-service/docker)
3. [helpdesk-renderer](https://github.com/symphonyoss/GroupID/tree/dev/helpdesk-application/docker)
4. [helpdesk-bot](https://github.com/symphonyoss/GroupID/tree/dev/helpdesk-bot/docker)

Each project contains the following structure:
```
docker/
  Dockerfile
  k8s_build_push_run.sh
  k8s_deployment.yaml.template
  k8s_service.yaml
```
The ```Dockerfile``` contains the base image, some necessary applications and the entry point to run the application. The ```k8s_deployment.yaml.template``` is used to create a [deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/) on Kubernetes, and the ```k8s_service.yaml``` a [service](https://kubernetes.io/docs/concepts/services-networking/service/) on Kubernetes.

There is also a file named ```k8s_build_push_run.sh```, used to perform the operations defined above.


#### How to start

First of all, you must install [Google Cloud](https://cloud.google.com/sdk/docs/quickstarts) and, after that, [Kubernetes CTL](https://kubernetes.io/docs/tasks/tools/install-kubectl/#install-kubectl-binary-via-curl).

The following command must be executed ```gcloud init``` and the needed project selected. Then the following command is needed to configure GCloud: ```gcloud container clusters get-credentials <cluster_name> --zone us-central1-b --project <project_name>```.


#### Create a service account in your POD

You must go to AC Portal on your POD and create a service account, which will be used by ```helpdesk-bot``` to authenticate. Please record the chosen username - it will be used bellow.


#### Fill the .yaml.template files

Each project has a ```.yaml.template``` file (except the mongoDB container) that must be edited. Some considerations about this file:

1. Never edit the ```<VERSION>``` information. It will be replaced by the version number located inside ```pom.xml``` when ```k8s_build_push_run.sh``` is executed.

2. The following entries are already configured to use the [Kubernetes Service DNS](https://kubernetes.io/docs/concepts/services-networking/dns-pod-service/), so probably they need not be changed:
  - ```SERVER_PORT```
  - ```HELPDESK_BOT_HOST```
  - ```HELPDESK_BOT_PORT```
  - ```HELPDESK_SERVICE_HOST```
  - ```HELPDESK_SERVICE_PORT```
  - ```MONGO_HOST```
  - ```MONGO_PORT```
  - (```MONGO_HOST``` and ```MONGO_PORT``` are configured to use ```helpdesk-mongodb``` service and must be changed if another MongoDB will be used)

3. When editing the entry ```PROVISIONING_SERVICE_ACCOUNT_NAME```, you must input the username for the previously created service account. It also must be the name of the ```.p12``` file contained in the ```AUTHENTICATION_KEYSTORE_FILE``` entry. For example, if username is ```mybot123``` the keystore file must be ```mybot123.p12```.

4. Other entries must be updated to reflect your environment.


#### The provisioning process

Inside ```helpdesk-bot```'s ```k8s_deployment.yaml.template```, there are some entries related to the provisioning process that must be executed at least once. Set flags to ```TRUE``` and input the credentials (a user with PROVISIONING role), and the provisioning will be performed during the bootstrap:
  - ``` PROVISIONING_USER_NAME```
  - ``` PROVISIONING_USER_PASSWORD```
  - ``` PROVISIONING_CA_GENERATE_KEYSTORE```
  - ``` PROVISIONING_CA_OVERWRITE```
  - ``` PROVISIONING_SERVICE_ACCOUNT_NAME```
  - ``` PROVISIONING_SERVICE_ACCOUNT_GENERATE_KEYSTORE```
  - ``` PROVISIONING_SERVICE_ACCOUNT_OVERWRITE```


#### Deploying it!

Go to the root [docker/](https://github.com/symphonyoss/GroupID/tree/dev/docker) dir and execute ```k8s_build_push_run.sh```. It will perform a ```mvn clean install``` and call each ```k8s_build_push_run.sh``` located inside the required projects.

After this process is done, it is time to check if everything is working. Execute the command ```kubectl proxy```, open a browser and go to http://127.0.0.1:8001/ui. These are some example of dashboards, showing deployments, pods and services:

![deployments](https://raw.githubusercontent.com/symphonyoss/GroupID/dev/docs/deployments.png)
![pods](https://raw.githubusercontent.com/symphonyoss/GroupID/dev/docs/pods.png)
![services](https://raw.githubusercontent.com/symphonyoss/GroupID/dev/docs/services.png)

## Contributing

1. Fork it (<https://github.com/symphonyoss/GroupID/fork>)
2. Create your feature branch (`git checkout -b feature/fooBar`)
3. Read our [contribution guidelines](.github/CONTRIBUTING.md) and [Community Code of Conduct](https://www.finos.org/code-of-conduct)
4. Commit your changes (`git commit -am 'Add some fooBar'`)
5. Push to the branch (`git push origin feature/fooBar`)
6. Create a new Pull Request

## License

The code in this repository is distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

Copyright 2016-2019 Symphony LLC

#### Configuring your APP

TBD


