#!/usr/bin/env bash

##
# Parses the input and adds information to shell variables
#
function parseInput {
    while [[ $# -ge 1 ]]
    do
        option="$1"

        case $option in
            #
            # handles --configName option
            --configName)
                shift
                CONFIG_NAME=$1
                shift
                ;;
            #
            # handles --name option
            --user)
                shift
                USER_NAME=$1
                shift
                ;;
            #
            # handles -h and --help options
            -h|--help)
                help
                exit 0
                ;;
            #
            # handles unknown options
            *)
                echo "[ERROR] Unkown option $option"
                exit 1
                ;;
        esac
    done

    if [[ -z "$CONFIG_NAME" ]]
    then
        echo "[ERROR] Missing environment config folder."
        exit 1
    fi

    if [[ -z "$USER_NAME" ]]
    then
        echo "[ERROR] Missing username."
        exit 1
    fi
}

##
# Displays help text on how to use this script.
#
function help {
    echo
    echo "Create Bot Certificate."
    echo "This script create a self-signed root certificate and a bot certificate signed by this root certificate"
    echo
    echo "Usage: generate_bot_keystore.sh [-h|--help]"
    echo "               --configName <configName>"
    echo "               --user <username>"
    echo "Options:"
    echo "--configName      Folder where the certificates will be generated: <scriptfolder>/certs/<configName>"
    echo "-- user           Service account username"
    echo
}

##
# Checks the result of the previous process and aborts if it was not successful (exit status 0).
#
function checkResult {
    local previousProcessResult=$?
    local message="$@"
    if [[ $previousProcessResult -ne 0 ]]
    then
        abort "$message"
    fi
}

##
# Returns the list of applications that do not have certs created in the given directory.
#
function setupRootKeyAndCert {
    mkdir -p $CERTS_DIR/$CONFIG_NAME

    ROOT_KEY_PATH=$CERTS_DIR/$CONFIG_NAME/helpdesk-root-key.pem
    ROOT_CERT_PATH=$CERTS_DIR/$CONFIG_NAME/helpdesk-root.pem

    echo "Checking root key and cert."

    if [[ -f "$ROOT_KEY_PATH" && -f "$ROOT_CERT_PATH" ]]
    then
        echo "Root key and cert already exist."
    else
        echo "Creating root key and cert at $CERTS_DIR."
        openssl req -x509 -newkey rsa:4096 -passout pass:changeit \
          -subj "/CN=groupid-provisioning/O=Symphony Communications LLC/C=US" \
          -keyout $ROOT_KEY_PATH -out $ROOT_CERT_PATH -days 3650
        checkResult "Failed to create root key and cert at $CERTS_DIR."
        echo "Root key and cert created at $CERTS_DIR."
    fi
}

function createCertificate {
    echo "Generating certificate for the bot user"

    BOT_KEY_FILENAME=helpdesk-key.pem
    BOT_REQ_FILENAME=helpdesk-req.pem
    BOT_CERT_FILENAME=helpdesk.pem
    BOT_P12_FILENAME=helpdesk.p12

    openssl genrsa -aes256 -passout pass:changeit \
      -out $CERTS_DIR/$CONFIG_NAME/$BOT_KEY_FILENAME 2048
    checkResult "Failed to create private key."

    openssl req -new -key $CERTS_DIR/$CONFIG_NAME/$BOT_KEY_FILENAME \
      -passin pass:changeit \
      -subj "/CN=$USER_NAME/O=Symphony Communications LLC/OU=NOT FOR PRODUCTION USE/C=US" \
      -out $CERTS_DIR/$CONFIG_NAME/$BOT_REQ_FILENAME
    checkResult "Failed to create certificate request."

    openssl x509 -req -sha256 -days 2922 -in $CERTS_DIR/$CONFIG_NAME/$BOT_REQ_FILENAME \
      -CA $ROOT_CERT_PATH -CAkey $ROOT_KEY_PATH -passin pass:changeit \
      -out $CERTS_DIR/$CONFIG_NAME/$BOT_CERT_FILENAME -set_serial 0x1
    checkResult "Failed to create certificate."

    openssl pkcs12 -export -out $CERTS_DIR/$CONFIG_NAME/$BOT_P12_FILENAME \
      -aes256 -in $CERTS_DIR/$CONFIG_NAME/$BOT_CERT_FILENAME -inkey $CERTS_DIR/$CONFIG_NAME/$BOT_KEY_FILENAME \
      -passin pass:changeit -passout pass:changeit
    checkResult "Failed to create p12 file."

    echo "Cert generated at $CERTS_DIR/$CONFIG_NAME/."

    rm -rf $CERTS_DIR/$CONFIG_NAME/$BOT_KEY_FILENAME
    rm -rf $CERTS_DIR/$CONFIG_NAME/$BOT_REQ_FILENAME
    rm -rf $CERTS_DIR/$CONFIG_NAME/$BOT_CERT_FILENAME
}

function createAppCertificate {
    echo "Generating certificate for the helpdesk application"

    APP_KEY_FILENAME=helpdesk-app-key.pem
    APP_REQ_FILENAME=helpdesk-app-req.pem
    APP_CERT_FILENAME=helpdesk-app.pem
    APP_P12_FILENAME=helpdesk-app.p12

    openssl genrsa -aes256 -passout pass:changeit \
      -out $CERTS_DIR/$CONFIG_NAME/$APP_KEY_FILENAME 2048
    checkResult "Failed to create app private key."

    openssl req -new -key $CERTS_DIR/$CONFIG_NAME/$APP_KEY_FILENAME \
      -passin pass:changeit \
      -subj "/CN=helpdesk/O=Symphony Communications LLC/OU=NOT FOR PRODUCTION USE/C=US" \
      -out $CERTS_DIR/$CONFIG_NAME/$APP_REQ_FILENAME
    checkResult "Failed to create app certificate request."

    openssl x509 -req -sha256 -days 2922 -in $CERTS_DIR/$CONFIG_NAME/$APP_REQ_FILENAME \
      -CA $ROOT_CERT_PATH -CAkey $ROOT_KEY_PATH -passin pass:changeit \
      -out $CERTS_DIR/$CONFIG_NAME/$APP_CERT_FILENAME -set_serial 0x1
    checkResult "Failed to create app certificate."

    openssl pkcs12 -export -out $CERTS_DIR/$CONFIG_NAME/$APP_P12_FILENAME \
      -aes256 -in $CERTS_DIR/$CONFIG_NAME/$APP_CERT_FILENAME -inkey $CERTS_DIR/$CONFIG_NAME/$APP_KEY_FILENAME \
      -passin pass:changeit -passout pass:changeit
    checkResult "Failed to create app p12 file."

    echo "Cert generated at $CERTS_DIR/$CONFIG_NAME/."

    rm -rf $CERTS_DIR/$CONFIG_NAME/$APP_KEY_FILENAME
    rm -rf $CERTS_DIR/$CONFIG_NAME/$APP_REQ_FILENAME
    rm -rf $CERTS_DIR/$CONFIG_NAME/$APP_CERT_FILENAME
}

SCRIPT_DIRECTORY=$(cd `dirname $0` && pwd)
CERTS_DIR=$SCRIPT_DIRECTORY/certs

parseInput "$@"
setupRootKeyAndCert
createCertificate
createAppCertificate
