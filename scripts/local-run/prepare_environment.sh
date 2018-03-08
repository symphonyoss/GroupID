#!/usr/bin/env bash

function checkResult {
    if [[ $? -ne 0 ]]
    then
        echo "[ERROR] $@"
        exit 1
    fi
}

function validateJavaHome {
    if [ -z ${JAVA_HOME} ]
    then
        echo "Please provide JAVA_HOME environment variable"
        exit 1
        # do something if the file doesn't provide ${JAVA_HOME}
    else
        if [ ! -e ${JAVA_HOME} ]
        then
            # do something if the file provides a non existent ${JAVA_HOME}
            echo "Please provide a valid JAVA_HOME path"
            exit 1
        fi
    fi

    echo "JAVA HOME: ${JAVA_HOME}"
}

function createSelfSignedCertificate {
    KEYSTORE_FILE=${SCRIPT_DIRECTORY}/server.jks
    KEYSTORE_PASSWORD=changeit
    CERT_FILE=${SCRIPT_DIRECTORY}/server.crt

    if [ -e ${KEYSTORE_FILE} ]
    then
        rm -rf ${KEYSTORE_FILE}
    fi

    echo "Generating self-signed keystore"

    ${JAVA_HOME}/bin/keytool -genkey -keyalg RSA -alias groupid -keystore ${KEYSTORE_FILE} \
        -keypass ${KEYSTORE_PASSWORD} -storepass ${KEYSTORE_PASSWORD} -validity 360 -keysize 2048 \
        -dname CN=localhost -storetype pkcs12 -noprompt -ext san=dns:localhost

    checkResult "[ERROR] Cannot generate self-signed keystore"

    echo "Extracting certificate from keystore"
    ${JAVA_HOME}/bin/keytool -exportcert -keystore ${KEYSTORE_FILE} -storetype pkcs12 -storepass \
        ${KEYSTORE_PASSWORD} -alias groupid -file ${CERT_FILE}

    checkResult "[ERROR] Cannot extract certificate from keystore"

    echo "Deleting existing 'groupid' alias from the JDK keystore"
    sudo ${JAVA_HOME}/bin/keytool -delete -noprompt -alias groupid -keystore \
        ${JAVA_HOME}/jre/lib/security/cacerts -storepass ${KEYSTORE_PASSWORD}

    echo "Importing certificate into the JDK keystore. Alias 'groupid'"
    sudo ${JAVA_HOME}/bin/keytool -import -alias groupid -keystore \
        ${JAVA_HOME}/jre/lib/security/cacerts -file ${CERT_FILE} -storepass ${KEYSTORE_PASSWORD} -noprompt

    checkResult "[ERROR] Cannot update JDK keystore"
}

SCRIPT_DIRECTORY=$(cd `dirname $0` && pwd)

validateJavaHome
createSelfSignedCertificate
