FROM tomcat:9-jdk11-openjdk-slim

########################################################################################################################
# clean the tomcat/webapps/ROOT dir

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        wget \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

########################################################################################################################
# clean the tomcat/webapps/ROOT dir

RUN rm -rf /usr/local/tomcat/webapps/ROOT
RUN rm -rf /usr/local/tomcat/webapps/docs
RUN rm -rf /usr/local/tomcat/webapps/examples

########################################################################################################################
# create the new root index redirect page

COPY webapps_ROOT_index.html /usr/local/tomcat/webapps/ROOT/index.html
RUN chmod 644 /usr/local/tomcat/webapps/ROOT/index.html

########################################################################################################################
# update the manager and host-manager context.xml files

COPY helper-context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml
COPY helper-context.xml /usr/local/tomcat/webapps/host-manager/META-INF/context.xml

########################################################################################################################

RUN wget --quiet --output-document=/usr/bin/confd \
	https://github.com/kelseyhightower/confd/releases/download/v0.16.0/confd-0.16.0-linux-amd64 \
    && chmod +x /usr/bin/confd \
    && mkdir -p /etc/confd/conf.d \
    && mkdir -p /etc/confd/templates

########################################################################################################################
# copy the confd template and config files

COPY tomcat-users.xml.tmpl /etc/confd/templates/tomcat-users.xml.tmpl
COPY tomcat-users.xml.toml /etc/confd/conf.d/tomcat-users.xml.toml

COPY catalina.properties.suffix.tmpl /etc/confd/templates/catalina.properties.suffix.tmpl
COPY catalina.properties.toml /etc/confd/conf.d/catalina.properties.toml

########################################################################################################################

COPY entrypoint.sh /entrypoint.sh

RUN chmod 544 /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]

########################################################################################################################
