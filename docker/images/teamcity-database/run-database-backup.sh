#!/usr/bin/env bash

set -eu

NOW=$(date +"%Y%m%d-%H%M%S")

BACKUP_FILENAME=teamcity-db-${NOW}.psql.bz2
BACKUP_PATH=/tmp/${BACKUP_FILENAME}

BUCKET_NAME=scenario-explorer-backups
FOLDER_NAME=teamcity-db

########################################################################################################################

S3_PATH=s3://${BUCKET_NAME}/${FOLDER_NAME}/${BACKUP_FILENAME}

echo "Backing up teamcity database to: ${BACKUP_FILENAME}"
su - postgres -c "pg_dump ${SE_TEAMCITY_DATABASE}" | bzip2 -9 - > ${BACKUP_PATH}
echo "done."
echo ""

echo "Copying backups to S3..."
/root/.local/bin/aws s3 --region us-east-1 cp /tmp/${BACKUP_FILENAME} ${S3_PATH}
echo "done."
echo ""

echo "Cleaning up..."
rm /tmp/${BACKUP_FILENAME}
echo "done."
echo ""

echo "All tasks complete."
echo ""

########################################################################################################################