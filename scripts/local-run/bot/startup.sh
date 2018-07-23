#!/usr/bin/env bash


##
# Displays help text on how to use this script.
#
function usage {
    echo
    echo "This script run the helpdesk bot"
    echo
    echo "Usage: startup.sh <configName>"
    echo "Options:"
    echo "<configName>      Folder where the certificates/config files are located: e.g.: <scriptfolder>/certs/<configName>"
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
    CERTS_PATH=${CERTS_DIR}/${CONFIG_NAME}/helpdesk.p12
    if [ ! -e ${CERTS_PATH} ]
    then
        echo "[ERROR] Missing helpdesk certificate file. $CERTS_PATH"
        exit 1
    fi
}

function validateCustomProfile {
    CONFIG_LOCATION=${SCRIPT_DIRECTORY}/../configs/${CONFIG_NAME}
    CUSTOM_PROFILE=${CONFIG_LOCATION}/application-custom.yaml

    if [ ! -e ${CUSTOM_PROFILE} ]
    then
        echo "[ERROR] Missing application custom file."
        exit 1
    fi
}

function configureEnvVariables {
    validateCertsDirectory
    validateCustomProfile

    export SPRING_CONFIG_LOCATION=${SCRIPT_DIRECTORY}/../configs/${CONFIG_NAME}/
    export CERTS_DIR=${CERTS_DIR}
    export SPRING_PROFILES_ACTIVE=custom
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

    CONFIG_NAME=$1

    copyAppBinary
    createLogsDirectory
    configureSSL
    configureEnvVariables
    execApplication
}

main "$@"
