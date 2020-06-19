#!/bin/bash
export SE_TOMCAT_USERNAME="admin"
export SE_TOMCAT_PASSWORD="Future5"
export SE_TOMCAT_HOSTNAME="se"
export SE_TOMCAT_PROTOCOL="http"
export SE_TOMCAT_PORT=8080

ant -f build-dev.ant dev-rebuild-web-ui-ara;
