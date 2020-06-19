FROM postgres:10-alpine

####################################################################################################

RUN apk --update add \
    	bash \
    	python \
    	py-pip \
    && rm -rf /var/cache/apk/*

########################################################################################################################
# install aws-cli

RUN pip install awscli --upgrade --user

####################################################################################################
# install confd

RUN wget --quiet --output-document=/usr/bin/confd https://github.com/kelseyhightower/confd/releases/download/v0.16.0/confd-0.16.0-linux-amd64 \
    && chmod +x /usr/bin/confd \
    && mkdir -p /etc/confd/conf.d \
    && mkdir -p /etc/confd/templates

####################################################################################################
# copy the confd template and config files

ADD /create-database.psql.tmpl /etc/confd/templates/create-database.psql.tmpl
ADD /create-database.psql.toml /etc/confd/conf.d/create-database.psql.toml

####################################################################################################
# add the helper scripts

RUN mkdir /scripts

COPY run-create-database.sh /scripts/run-create-database.sh
COPY run-database-backup.sh /scripts/run-database-backup.sh

RUN chown postgres:postgres /scripts

####################################################################################################
