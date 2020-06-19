#!/usr/bin/env bash

clear

if [ $# -gt 0 ]; then
	docker exec -t dev-console /run-ant-build.sh $1 $2 $3 $4 $5 $6
else
	echo "normal dev:"
	echo "	  dev-rebuild-web-ui-ara"
	echo "	  dev-rebuild-web-user-support-ara"
	echo
	echo "normal dev, with auth:"
	echo "	  dev-rebuild-web-ui-ara-auth"
	echo "	  dev-rebuild-web-user-support-ara-auth"
	echo
	echo "junit:"
	echo "    test-common"
	echo "    test-web-ui"
	echo "    test-web-user-support"
	echo
	echo "prep-docker-build-web-ui-ara"
	echo "prep-docker-build-web-user-support-ara"
	echo
	echo "prep-docker-build-web-ui-ncsu"
	echo "prep-docker-build-web-user-support-ncsu"
	echo
	echo "build-docker-image-web-ui-ara"
	echo "build-docker-image-web-ui-ncsu"
	echo
	echo "push-docker-image-web-ui-ara-dev"
	echo "push-docker-image-web-ui-ncsu-dev"
fi
