#!/usr/bin/env bash

set -eu

NOW=$(date +"%Y%m%d-%H%M%S")

BACKUP_FILENAME=mongo-db-${NOW}-${SE_CLUSTER_NAME}-mongodb.tar.bz2
BACKUP_WORKING=/tmp/backup
BACKUP_PATH=/tmp/${BACKUP_FILENAME}

BUCKET_NAME=scenario-explorer-backups
FOLDER_NAME=mongo-db

########################################################################################################################

S3_PATH=s3://${BUCKET_NAME}/${FOLDER_NAME}/${BACKUP_FILENAME}

echo "Backing up mongo db to: ${BACKUP_FILENAME}"
mkdir -p ${BACKUP_WORKING}
mongodump --username=${SE_MONGODB_USERNAME} --password=${SE_MONGODB_PASSWORD} --out ${BACKUP_WORKING}
pushd ${BACKUP_WORKING}
tar -cjf ${BACKUP_PATH} .
popd
echo "done."
echo ""

echo "Copying backups to S3..."
/root/.local/bin/aws s3 --region us-east-1 cp /tmp/${BACKUP_FILENAME} ${S3_PATH}
echo "done."
echo ""

echo "Cleaning up..."
rm ${BACKUP_PATH}
rm -rf ${BACKUP_WORKING}
echo "done."
echo ""

echo "All tasks complete."
echo ""

########################################################################################################################
