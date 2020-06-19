#!/usr/bin/env bash

if [ "$(whoami)" != "postgres" ]; then
    echo "Script must be run as user: postgres"
    exit -1
fi

confd -onetime -backend env

echo "Running database script..."
psql -f /scripts/create-database.psql
echo "done."
