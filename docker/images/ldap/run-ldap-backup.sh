#!/usr/bin/env bash

set -eu

NOW=$(date +"%Y%m%d-%H%M%S")

BACKUP_FILENAME=ldap-${NOW}-${SE_CLUSTER_NAME}.ldif.bz2
BACKUP_PATH=/tmp/${BACKUP_FILENAME}

BUCKET_NAME=scenario-explorer-backups
FOLDER_NAME=ldap

########################################################################################################################

S3_PATH=s3://${BUCKET_NAME}/${FOLDER_NAME}/${BACKUP_FILENAME}

echo "Backing up ldap to: ${BACKUP_FILENAME}"
slapcat | bzip2 -9 - > ${BACKUP_PATH}
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
