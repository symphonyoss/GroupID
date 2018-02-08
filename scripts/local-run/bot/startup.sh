#!/usr/bin/env bash

##
# Displays help text on how to use this script.
#
function usage {
    echo
    echo "This script run the helpdesk bot"
    echo
    echo "Usage: startup.sh <env> [<kv_enabled>]"
    echo "          --env <nexus1 | nexus2 | nexus3 | nexus4>"
    echo "          --kv_enabled <true | false>"
    echo
}

function copyAppBinary {
    if [ ! -e ${APP_BINARY_FILE} ]
    then
        # do something if the file provides a non existent ${JAVA_HOME}
        echo "File helpdesk-bot.jar not found. Please compile the application"
        exit 1
    else
        cp -f ${APP_BINARY_FILE} ${SCRIPT_DIRECTORY}/helpdesk-bot.jar
    fi
}

function createLogsDirectory {
    LOGS_DIR=${SCRIPT_DIRECTORY}/logs
    if [ ! -e ${LOGS_DIR} ]
    then
        mkdir ${SCRIPT_DIRECTORY}/logs
    else
        rm -f ${SCRIPT_DIRECTORY}/logs/*
    fi

    export LOG4J2_BASE_DIR=${SCRIPT_DIRECTORY}
}

function configureSSL {
    KEYSTORE_FILE=${SCRIPT_DIRECTORY}/../server.jks
    if [ ! -e ${KEYSTORE_FILE} ]
    then
        echo "Keystore file not found. Please run the prepare_environment.sh script"
        exit 1
    fi

    export SERVER_SSL_ENABLED=true
    export SERVER_SSL_KEY_STORE=${KEYSTORE_FILE}
    export SERVER_SSL_KEY_STORE_PASSWORD=changeit
}

function validateCertsDirectory {
    CERTS_PATH=${CERTS_DIR}/helpdesk-${ENV}.p12
    if [ ! -e ${CERTS_PATH} ]
    then
        echo "[ERROR] Missing helpdesk certificate file."
        exit 1
    fi
}

function validateCustomProfile {
    CONFIG_LOCATION=${SCRIPT_DIRECTORY}/../configs/${ENV}
    CUSTOM_PROFILE=${CONFIG_LOCATION}/application-custom.yaml

    if [ ! -e ${CUSTOM_PROFILE} ]
    then
        echo "[ERROR] Missing application custom file."
        exit 1
    fi
}

function configureEnvVariables {
    declare -A pod_envs
    pod_envs[nexus1]='sym-nexus1-dev-chat-glb-3'
    pod_envs[nexus2]='sym-nexus2-dev-chat-glb-3'
    pod_envs[nexus3]='sym-nexus3-dev-chat-glb-3'
    pod_envs[nexus4]='sym-nexus4-dev-chat-glb-3'

    INFRA_NAME=${pod_envs[$ENV]}

    if [[ -z "$INFRA_NAME" ]]
    then
        echo "[ERROR] Missing environment. Possible options: nexus1, nexus2, nexus3 and nexus4"
        exit 1
    fi

    if [[ $KV_ENABLED == "true" ]]
    then
#        export POD_ENV=dev
#        export CONSUL_ENABLED=true
#        export INFRA_NAME=${INFRA_NAME}
        echo "Not implemented yet"
        exit 1
    else
        validateCertsDirectory
        validateCustomProfile

        export SPRING_CONFIG_LOCATION=${SCRIPT_DIRECTORY}/../configs/${ENV}/
        export CERTS_DIR=${CERTS_DIR}
        export SPRING_PROFILES_ACTIVE=custom
    fi
}

function execApplication {
    ${JAVA_HOME}/bin/java -jar ${SCRIPT_DIRECTORY}/helpdesk-bot.jar &> /dev/null &
    echo $! > ${SCRIPT_DIRECTORY}/helpdesk-bot.pid

    echo "Application helpdesk-bot starting"
}

function main {
    SCRIPT_DIRECTORY=$(cd `dirname $0` && pwd)
    APP_BINARY_FILE=${SCRIPT_DIRECTORY}/../../../helpdesk-bot/target/helpdesk-bot.jar
    CERTS_DIR=${SCRIPT_DIRECTORY}/../certs

    if [[ $# -lt 1 ]]
    then
        usage
        exit
    fi

    ENV=$1
    KV_ENABLED=$2

    copyAppBinary
    createLogsDirectory
    configureSSL
    configureEnvVariables
    execApplication
}

main "$@"