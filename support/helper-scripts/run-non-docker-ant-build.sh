#!/usr/bin/env bash

set -e

if [ $# -gt 0 ]; then
	export SE_TOMCAT_HOSTNAME=localhost
	export SE_TOMCAT_PROTOCOL=http
	export SE_TOMCAT_PORT=8080
	export SE_TOMCAT_USERNAME=lasadmin
	export SE_TOMCAT_PASSWORD=testing

	export SE_TOMCAT_INSTALL_PATH=/var/lib/tomcat8

	cd ..
	clear

	ant -f build-dev.ant $1 $2 $3 $4 $5 $6 $7 $8
else
	echo "normal dev:"
	echo "	dev-rebuild-web-ui-ara"
	echo "	dev-rebuild-web-user-support-ara"
	echo "normal dev, with auth:"
	echo "	dev-rebuild-web-ui-ara-auth"
	echo "	dev-rebuild-web-user-support-ara-auth"
	echo ""
	echo "prep-docker-build-web-ui-ara"
	echo "prep-docker-build-web-user-support-ara"
	echo ""
	echo "prep-docker-build-web-ui-ncsu"
	echo "prep-docker-build-web-user-support-ncsu"
	echo ""
	echo "build-docker-image-web-ui-ara"
	echo "build-docker-image-web-ui-ncsu"
	echo ""
	echo "push-docker-image-web-ui-ara-dev"
	echo "push-docker-image-web-ui-ncsu-dev"
fi
