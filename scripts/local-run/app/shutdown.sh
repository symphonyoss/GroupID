#!/usr/bin/env bash

SCRIPT_DIRECTORY=$(cd `dirname $0` && pwd)
PID_FILE=${SCRIPT_DIRECTORY}/helpdesk-renderer.pid

if [ ! -e ${PID_FILE} ]
then
    echo "PID file not found"
    exit 1
fi

PID=`cat ${PID_FILE}`
kill ${PID}
rm ${PID_FILE}

echo "Application stopped"