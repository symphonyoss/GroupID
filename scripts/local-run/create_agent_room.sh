#!/usr/bin/env bash

declare -A pod_envs
pod_envs[nexus1]='nexus1-2.symphony.com'
pod_envs[nexus2]='nexus2-2.symphony.com'
pod_envs[nexus3]='nexus3-2.symphony.com'
pod_envs[nexus4]='nexus4-2.symphony.com'

declare -A fqdn_envs
fqdn_envs[nexus1]='sym-nexus1-dev-chat-glb-3-ause1-all.symphony.com'
fqdn_envs[nexus2]='sym-nexus2-dev-chat-glb-3-ause1-all.symphony.com'
fqdn_envs[nexus3]='sym-nexus3-dev-chat-glb-3-ause1-all.symphony.com'
fqdn_envs[nexus4]='sym-nexus4-dev-chat-glb-3-ause1-all.symphony.com'

##
# Parses the input and adds information to shell variables
#
function parseInput {
    while [[ $# -ge 1 ]]
    do
        option="$1"

        case $option in
            #
            # handles --env option
            --env)
                shift
                ENV=$1
                POD_ADDRESS=${pod_envs[$ENV]}
                FQDN_POD_ADDRESS=${fqdn_envs[$ENV]}
                AUTH_ENDPOINT="https://$FQDN_POD_ADDRESS:8444/sessionauth/v1/authenticate"
                CREATE_ENDPOINT="https://$POD_ADDRESS/pod/v3/room/create"
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
                AGENT=$1
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

    if [[ -z "$AGENT" ]]
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
    echo "               --env <nexus1 | nexus2 | nexus3 | nexus4>"
    echo "               --name Room Name"
    echo "               --description Room Description"
    echo "               --agent Agent ID"
    echo
}

##
# Authenticate admin user
#
function authenticate {
    CERTS_DIR=${SCRIPT_DIRECTORY}/certs
    CERTS_FILE=${SCRIPT_DIRECTORY}/certs/${ENV}/helpdesk.pem

    if [ ! -e ${CERTS_FILE} ]
    then
        CERTS_PKCS12=${SCRIPT_DIRECTORY}/certs/${ENV}/helpdesk.p12

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

    STREAM_ID=$(curl -s -H "sessionToken: $SESSION_TOKEN" -H "Content-Type: application/json" -d "{\"name\":\"$ROOM_NAME\", \"description\": \"$DESCRIPTION\", \"membersCanInvite\": true, \"discoverable\": true, \"public\": true, \"readOnly\": false, \"copyProtected\": false, \"crossPod\": false, \"viewHistory\": true }" $CREATE_ENDPOINT | jq -r '.roomSystemInfo.id')

    if [ $STREAM_ID == null ]
    then
        echo "[ERROR] Fail to create stream."
        exit 1
    fi
}

function add_membership {
    echo "Adding membership to the queue room"
    MEMBERSHIP_ENDPOINT="https://$POD_ADDRESS/pod/v1/room/$STREAM_ID/membership/add"
    RESULT=$(curl -s -H "sessionToken: $SESSION_TOKEN" -H "Content-Type: application/json" -d "{ \"id\": $AGENT }" $MEMBERSHIP_ENDPOINT | jq -r '.message')

    echo $RESULT
}

function generate_custom_file {
    echo "Generating custom file"
    echo "agentStreamId: $STREAM_ID" > ${SCRIPT_DIRECTORY}/configs/${ENV}/application-custom.yaml
}

SCRIPT_DIRECTORY=$(cd `dirname $0` && pwd)

parseInput "$@"
authenticate
create_room
add_membership
generate_custom_file
