FROM imaginationsupport/tomcat-base

########################################################################################################################
# copy the WAR's into the webapps dir

COPY web-ui.war /usr/local/tomcat/webapps/scenarioexplorer.war
COPY web-user-support.war /usr/local/tomcat/webapps/user-support.war

########################################################################################################################
# move the server libraries into place

ADD tomcat-jars-web-ui.tar.gz /usr/local/tomcat/lib/
ADD tomcat-jars-web-user-support.tar.gz /usr/local/tomcat/lib/

########################################################################################################################

ENV JVM_OPTS --add-modules java.activation

########################################################################################################################
