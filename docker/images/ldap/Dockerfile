FROM alpine:3.9

# based on: https://hub.docker.com/r/danielguerra/alpine-openldap/

########################################################################################################################
# install openldap

ENV OPENLDAP_VERSION 2.4.48-r0

RUN apk --update add \
    	bash \
    	python \
    	py-pip \
    	openldap \
    	openldap-back-mdb \
    && rm -rf /var/cache/apk/*

########################################################################################################################
# copy the extra modules

COPY modules/ /etc/openldap/modules

########################################################################################################################
# install confd

RUN wget --quiet --output-document=/usr/bin/confd https://github.com/kelseyhightower/confd/releases/download/v0.16.0/confd-0.16.0-linux-amd64 \
    && chmod +x /usr/bin/confd \
    && mkdir -p /etc/confd/conf.d \
    && mkdir -p /etc/confd/templates

########################################################################################################################
# install aws-cli

RUN pip install awscli --upgrade --user

########################################################################################################################
# copy the confd template and config files

COPY slapd.conf.tmpl /etc/confd/templates/slapd.conf.tmpl
COPY slapd.conf.toml /etc/confd/conf.d/slapd.conf.toml

########################################################################################################################

COPY run-ldap-backup.sh /run-ldap-backup.sh

########################################################################################################################

COPY entrypoint.sh /entrypoint.sh

RUN chmod 544 /entrypoint.sh

EXPOSE 389

#TODO should set USER

ENTRYPOINT ["/entrypoint.sh"]

########################################################################################################################
