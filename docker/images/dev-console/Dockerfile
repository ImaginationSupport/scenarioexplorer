FROM centos:8

########################################################################################################################
# update the timezone

RUN rm -f /etc/localtime \
	&& ln -s /usr/share/zoneinfo/America/New_York /etc/localtime

########################################################################################################################
# install EPEL

RUN yum install -y dnf-plugins-core \
	&& dnf install -y https://dl.fedoraproject.org/pub/epel/epel-release-latest-8.noarch.rpm \
	&& dnf config-manager --set-enabled PowerTools

########################################################################################################################
# install general tools

RUN dnf install -y \
		git \
		graphviz \
		nano \
		openldap-clients \
		python3 \
		wget

########################################################################################################################
# install docker

RUN dnf install -y https://download.docker.com/linux/centos/7/x86_64/stable/Packages/containerd.io-1.2.6-3.3.el7.x86_64.rpm \
	&& dnf config-manager --add-repo=https://download.docker.com/linux/centos/docker-ce.repo \
	&& dnf install -y docker-ce

########################################################################################################################
# download and install java

RUN wget --quiet https://download.java.net/openjdk/jdk13/ri/openjdk-13+33_linux-x64_bin.tar.gz \
    -O /tmp/openjdk.tar.gz \
    && mkdir /opt/openjdk \
    && tar -xzf /tmp/openjdk.tar.gz --strip-components=1 -C /opt/openjdk \
    && rm /tmp/openjdk.tar.gz

ENV JAVA_HOME /opt/openjdk
ENV PATH="${PATH}:/opt/openjdk/bin"

########################################################################################################################
# download and install ant

RUN wget --quiet https://www.apache.org/dist/ant/binaries/apache-ant-1.10.7-bin.tar.gz -O /tmp/apache-ant.tar.gz \
    && mkdir /opt/apache-ant \
    && tar xzf /tmp/apache-ant.tar.gz -C /opt/apache-ant --strip-component=1 \
    && rm /tmp/apache-ant.tar.gz
ENV PATH="${PATH}:/opt/apache-ant/bin"

########################################################################################################################
# download and install confd

RUN wget --quiet -O /usr/bin/confd https://github.com/kelseyhightower/confd/releases/download/v0.16.0/confd-0.16.0-linux-amd64 \
    && chmod +x /usr/bin/confd \
    && mkdir -p /etc/confd/conf.d \
    && mkdir -p /etc/confd/templates

########################################################################################################################
# download and install dart sass

RUN wget --quiet https://github.com/sass/dart-sass/releases/download/1.23.0/dart-sass-1.23.0-linux-x64.tar.gz -O /tmp/dart-sass.tar.gz \
    && mkdir /opt/dart-sass \
    && tar xzf /tmp/dart-sass.tar.gz -C /opt/dart-sass --strip-component=1 \
    && rm /tmp/dart-sass.tar.gz
ENV PATH="${PATH}:/opt/dart-sass"

########################################################################################################################
# add the credential helper and config file

RUN mkdir /root/.docker

COPY docker-config.json /root/.docker/config.json
COPY docker-credential-ecr-login /usr/local/bin

RUN chmod 700 /root/.docker \
	&& chmod 600 /root/.docker/config.json \
	&& chmod 755 /usr/local/bin/docker-credential-ecr-login

########################################################################################################################
# add the helper shell script

COPY run-ant-build.sh /run-ant-build.sh
RUN chmod 500 /run-ant-build.sh

########################################################################################################################
# clean up

RUN yum clean all \
    && rm -rf /var/cache/yum \
    && dnf clean all

########################################################################################################################
# set up the entry point and

COPY entrypoint.sh /entrypoint.sh
RUN chmod 500 /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]

########################################################################################################################
