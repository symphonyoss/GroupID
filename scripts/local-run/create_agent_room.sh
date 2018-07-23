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
            ## handles --podAddr
            --podAddr)
                shift
                POD_ADDRESS=$1
                shift
                ;;
            #
            ## handles --sessionAuthFQDN
            --sessionAuthFQDN)
                shift
                SESSION_AUTH_ADDRESS=$1
                shift
                ;;
            #
            ## handles --sessionAuthPort
            --sessionAuthPort)
                shift
                SESSION_AUTH_PORT=$1
                shift
                ;;
            #
            # handles --configName option
            --configName)
                shift
                CONFIG_NAME=$1
                shift
                ;;
            #
            # handles --name option
            --name)
                shift
                ROOM_NAME=$1
                shift
                ;;
            #
            # handles --description option
            --description)
                shift
                DESCRIPTION=$1
                shift
                ;;
            #
            # handles --agent option
            --agent)
                shift
                AGENT_ID=$1
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

    if [[ -z "$POD_ADDRESS" ]]
    then
        echo "[ERROR] Missing environment."
        exit 1
    fi

    if [[ -z "$SESSION_AUTH_ADDRESS" ]]
    then
        echo "[ERROR] Missing environment."
        exit 1
    fi

    if [[ -z "$SESSION_AUTH_PORT" ]]
    then
        echo "[ERROR] Missing environment."
        exit 1
    fi

    if [[ -z "$CONFIG_NAME" ]]
    then
        echo "[ERROR] Configuration folder."
        exit 1
    fi

    if [[ -z "$ROOM_NAME" ]]
    then
        echo "[ERROR] Missing room name."
        exit 1
    fi

    if [[ -z "$DESCRIPTION" ]]
    then
        echo "[ERROR] Missing room description."
        exit 1
    fi

    if [[ -z "$AGENT_ID" ]]
    then
        echo "[ERROR] Missing agent identifier."
        exit 1
    fi
}

##
# Displays help text on how to use this script.
#
function help {
    echo
    echo "Create Symphony agent room."
    echo "This script create a new agent room"
    echo
    echo "Usage: create_agent_room.sh [-h|--help]"
    echo "               --podAddr <pod address>"
    echo "               --sessionAuthFQDN <session auth address>"
    echo "               --sessionAuthPort <session auth port>"
    echo "               --configName <configName>"
    echo "               --name <Room Name>"
    echo "               --description <Room Description>"
    echo "               --agent <Agent ID>"
    echo "Options:"
    echo "--podAddr            Pod address (without protocol)"
    echo "--sessionAuthFQDN    Session auth server fully qualified domain name (without protocol)"
    echo "--sessionAuthPort    Session auth port"
    echo "--configName         Folder where the certificates will be generated: <scriptfolder>/certs/<configName>"
    echo "--name               Name of the room"
    echo "--description        Short text describing the room"
    echo "--agent              Pass the user id to be added as an agent in the room"
    echo
}

##
# Authenticate admin user
#
function authenticate {
    AUTH_ENDPOINT="https://$SESSION_AUTH_ADDRESS:${SESSION_AUTH_PORT}/sessionauth/v1/authenticate"
    CERTS_DIR=${SCRIPT_DIRECTORY}/certs
    CERTS_FILE=${SCRIPT_DIRECTORY}/certs/${CONFIG_NAME}/helpdesk.pem
    CERTS_PKCS12=${SCRIPT_DIRECTORY}/certs/${CONFIG_NAME}/helpdesk.p12

    if [ ! -e ${CERTS_FILE} ]
    then
        if [ ! -e ${CERTS_PKCS12} ]
        then
            echo "[ERROR] Missing bot certificate."
            exit 1
        fi

        openssl pkcs12 -in ${CERTS_PKCS12} -passin pass:changeit -nodes -out ${CERTS_FILE}
    fi

    SESSION_TOKEN=$(curl -X POST -k --cert $CERTS_FILE --cert-type PEM -s $AUTH_ENDPOINT | jq -r '.token')

    if [ -z $SESSION_TOKEN ]
    then
        if [ ! -e ${CERTS_PKCS12} ]
        then
            echo "[ERROR] Missing bot certificate."
            exit 1
        fi

        SESSION_TOKEN=$(curl -X POST -k --cert $CERTS_PKCS12:changeit -s $AUTH_ENDPOINT | jq -r '.token')
    fi

    if [ -z $SESSION_TOKEN ]
    then
        echo "[ERROR] Fail to authenticate bot user."
        exit 1
    fi

    echo "Bot user authenticated."
    echo
}

#
# Create room
#
function create_room {
    echo "Creating room $ROOM_NAME"

    CREATE_ENDPOINT="https://$POD_ADDRESS/pod/v3/room/create"

    C_RESULT=$(curl -s -H "sessionToken: $SESSION_TOKEN" -H "Content-Type: application/json" -d "{\"name\":\"$ROOM_NAME\", \"description\": \"$DESCRIPTION\", \"membersCanInvite\": true, \"discoverable\": true, \"public\": true, \"readOnly\": false, \"copyProtected\": false, \"crossPod\": false, \"viewHistory\": true }" $CREATE_ENDPOINT)

    STREAM_ID=$(echo $C_RESULT | jq -r '.roomSystemInfo.id')

    if [ $STREAM_ID == null ]
    then
        echo "[ERROR] Fail to create stream. Response: $C_RESULT"
        exit 1
    fi
    echo "Room created successfully: $STREAM_ID"
}

function add_membership {
    echo "Adding membership to the queue room"
    MEMBERSHIP_ENDPOINT="https://$POD_ADDRESS/pod/v1/room/$STREAM_ID/membership/add"
#    RESULT=$(curl -s -H "sessionToken: $SESSION_TOKEN" -H "Content-Type: application/json" -d "{ \"id\": $AGENT_ID }" $MEMBERSHIP_ENDPOINT)
    RESULT=$(curl -s -H "sessionToken: $SESSION_TOKEN" -H "Content-Type: application/json" -d "{ \"id\": $AGENT_ID }" $MEMBERSHIP_ENDPOINT)

#    MESSAGE=$(jq -r '.message')
    MESSAGE=$(echo $RESULT | jq -e '.message' 2> /dev/null)

    if [ ! -z "$MESSAGE" ]; then
      echo "Membership status $MESSAGE"
    else
      echo "Error($?) while adding membership: $RESULT"
    fi
}

function generate_custom_file {
    echo "Generating custom file"
    mkdir -p ${SCRIPT_DIRECTORY}/configs/${CONFIG_NAME}
    echo "agentStreamId: $STREAM_ID" > ${SCRIPT_DIRECTORY}/configs/${CONFIG_NAME}/application-custom.yaml
}

SCRIPT_DIRECTORY=$(cd `dirname $0` && pwd)

parseInput "$@"
authenticate
create_room
add_membership
generate_custom_file
