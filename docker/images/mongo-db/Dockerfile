FROM mongo:3.6
#FROM 4.0-xenial

########################################################################################################################

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        bzip2 \
        python3 \
        python3-pip \
        python3-setuptools \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && pip3 install --upgrade pip

########################################################################################################################
# install aws cli

RUN pip3 install awscli --upgrade --user --no-warn-script-location

########################################################################################################################

COPY run-database-backup.sh /run-database-backup.sh

########################################################################################################################
