#/usr/bin/env bash
complete -W "compile-common-java build-common-jar test-common compile-console-tools-java generate-swagger-v2 generate-openapi-v3 dev-rebuild-web-ui-ara dev-rebuild-web-ui-ara-auth dev-rebuild-web-user-support-ara dev-rebuild-web-user-support-ara-auth dev-rebuild-web-ui-ncsu dev-rebuild-web-user-support-ncsu configure-dev-docker-build-web-ui configure-dev-docker-build-web-user-support display-web-ui-build-success display-web-user-support-build-success install-local-dev-server-tomcat-jars generate-ant-build-graph clean-docker build-docker-image-ldap build-docker-image-mongo-db build-docker-image-teamcity-agent build-docker-image-teamcity-database build-docker-image-tomcat-base build-docker-image-web-ui-ara build-docker-image-web-ui-ncsu build-docker-image-reverse-proxy build-docker-image-dev-console build-docker-image-swagger-ui build-docker-images push-docker-image-ldap-dev push-docker-image-ldap-prod push-docker-image-mongo-db-dev push-docker-image-mongo-db-prod push-docker-image-teamcity-agent-dev push-docker-image-teamcity-agent-prod push-docker-image-teamcity-database-dev push-docker-image-teamcity-database-prod push-docker-image-tomcat-base-dev push-docker-image-tomcat-base-prod push-docker-image-web-ui-ara-dev push-docker-image-web-ui-ara-prod push-docker-image-web-ui-ncsu-dev push-docker-image-web-ui-ncsu-prod push-docker-image-reverse-proxy-dev push-docker-image-reverse-proxy-prod push-docker-image-dev-console-dev push-docker-image-dev-console-prod push-docker-image-swagger-ui-dev push-docker-image-swagger-ui-prod prep-docker-build-web-ui-ara prep-docker-build-web-ui-ncsu run-build-docker-image run-push-docker-image pull-base-image create-build-tag compile-web-ui-java test-web-ui configure-web-ui-auth stage-web-ui-static-content compile-web-ui-css init-compile-web-ui-js compile-web-ui-js build-web-ui-war build-web-ui-tomcat-server-jars-tar-gz undeploy-web-ui deploy-web-ui use-web-ui-theme-ara use-web-ui-theme-ncsu compile-web-user-support-java test-web-user-support configure-web-user-support-auth stage-web-user-support-static-content compile-web-user-support-css init-compile-web-user-support-js compile-web-user-support-js build-web-user-support-war build-web-user-support-tomcat-server-jars-tar-gz undeploy-web-user-support deploy-web-user-support use-web-user-support-theme-ara use-web-user-support-theme-ncsu clean load-properties-teamcity load-properties-deploy-tomcat load-properties-localdev use-auth finish-config create-build-dir create-output-dir setup-build-time check-running-in-teamcity verify-path-exists verify-running-as-root init-tomcat-tasks undeploy-tomcat deploy-tomcat" run-dockerized-ant-build.sh
