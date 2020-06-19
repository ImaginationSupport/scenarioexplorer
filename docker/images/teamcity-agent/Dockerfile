FROM jetbrains/teamcity-agent

####################################################################################################

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        wget \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

########################################################################################################################
# download and install java

RUN wget --quiet https://download.java.net/openjdk/jdk11/ri/openjdk-11+28_linux-x64_bin.tar.gz \
    -O /tmp/openjdk-11.tar.gz \
    && mkdir /opt/openjdk-11 \
    && tar -xzf /tmp/openjdk-11.tar.gz --strip-components=1 -C /opt/openjdk-11 \
    && rm /tmp/openjdk-11.tar.gz

ENV JAVA_HOME /opt/openjdk-11
ENV PATH="${PATH}:/opt/openjdk-11/bin"

####################################################################################################
# download and install ant

RUN wget --quiet https://www.apache.org/dist/ant/binaries/apache-ant-1.10.7-bin.tar.gz \
    -O /tmp/apache-ant.tar.gz \
    && mkdir /opt/apache-ant \
    && tar xzf /tmp/apache-ant.tar.gz -C /opt/apache-ant --strip-component=1 \
    && rm /tmp/apache-ant.tar.gz
ENV PATH="${PATH}:/opt/apache-ant/bin"

########################################################################################################################
# download and install dart sass

RUN wget --quiet https://github.com/sass/dart-sass/releases/download/1.23.0/dart-sass-1.23.0-linux-x64.tar.gz -O /tmp/dart-sass.tar.gz \
    && mkdir /opt/dart-sass \
    && tar xzf /tmp/dart-sass.tar.gz -C /opt/dart-sass --strip-component=1 \
    && rm /tmp/dart-sass.tar.gz
ENV PATH="${PATH}:/opt/dart-sass"

####################################################################################################
# add the credential helper and config file

RUN mkdir /root/.docker

COPY docker-config.json /root/.docker/config.json
COPY docker-credential-ecr-login /usr/local/bin

RUN chmod 700 /root/.docker \
	&& chmod 600 /root/.docker/config.json \
	&& chmod 755 /usr/local/bin/docker-credential-ecr-login

####################################################################################################
