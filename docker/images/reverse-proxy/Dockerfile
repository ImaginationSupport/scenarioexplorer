FROM debian:buster-slim

####################################################################################################

# from: https://github.com/g17/nginx-ldap

ENV NGINX_VERSION release-1.17.3
ENV DOCKERIZE_VERSION v0.6.0

####################################################################################################

RUN echo 'debconf debconf/frontend select Noninteractive' | debconf-set-selections \
	&& echo 'deb http://ftp.debian.org/debian/ buster-backports main' > /etc/apt/sources.list.d/backports.list \
	&& apt-get update \
	&& apt-get install -t buster-backports -y --no-install-recommends \
		ca-certificates \
		certbot \
		gcc \
		git \
		libldap2-dev \
		libpcre3-dev \
		libssl-dev \
		make \
		wget \
		zlib1g-dev

# See http://wiki.nginx.org/InstallOptions
RUN mkdir /var/log/nginx \
	&& mkdir /etc/nginx \
	&& cd ~ \
	&& git clone https://github.com/kvspb/nginx-auth-ldap.git \
	&& git clone https://github.com/nginx/nginx.git \
	&& cd ~/nginx \
	&& git checkout tags/${NGINX_VERSION} \
	&& ./auto/configure \
		--add-module=/root/nginx-auth-ldap \
		--with-http_ssl_module \
		--with-debug \
		--conf-path=/etc/nginx/nginx.conf \
		--sbin-path=/usr/sbin/nginx \
		--pid-path=/var/log/nginx/nginx.pid \
		--error-log-path=/var/log/nginx/error.log \
		--http-log-path=/var/log/nginx/access.log \
        --with-stream \
        --with-stream_ssl_module \
        --with-debug \
        --with-file-aio \
        --with-threads \
        --with-http_gunzip_module \
        --with-http_gzip_static_module \
        --with-http_v2_module \
        --with-http_auth_request_module \
	&& make install \
	&& cd .. \
	&& rm -rf nginx-auth-ldap \
	&& rm -rf nginx \
	&& wget -O /tmp/dockerize.tar.gz https://github.com/jwilder/dockerize/releases/download/${DOCKERIZE_VERSION}/dockerize-linux-amd64-${DOCKERIZE_VERSION}.tar.gz \
	&& tar -C /usr/local/bin -xzvf /tmp/dockerize.tar.gz \
	&& rm -rf /tmp/dockerize.tar.gz

####################################################################################################

RUN apt-get autoremove -y \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

####################################################################################################
# install confd

RUN wget --quiet --output-document=/usr/bin/confd https://github.com/kelseyhightower/confd/releases/download/v0.15.0/confd-0.15.0-linux-amd64 \
    && chmod +x /usr/bin/confd \
    && mkdir -p /etc/confd/conf.d \
    && mkdir -p /etc/confd/templates

####################################################################################################
# copy the confd config files (the template is copied from the volume later)

ADD /nginx.conf.toml /etc/confd/conf.d/nginx.conf.toml

####################################################################################################
# copy the additional nginx config files

COPY ssl-config.conf /etc/nginx/
COPY http-redirect-https.conf /etc/nginx/
COPY error-pages.conf /etc/nginx/

####################################################################################################
# set up the web root and root URL redirect file

RUN mkdir -p /var/www/root
RUN mkdir -p /var/www/error

COPY index.html /var/www/root/index.html
COPY error/* /var/www/error/

RUN chmod -R 555 /var/www/root \
	&& chmod -R 555 /var/www/error

####################################################################################################

COPY entrypoint.sh /entrypoint.sh
RUN chmod 544 /entrypoint.sh

COPY reload-nginx-config.sh /reload-nginx-config.sh
RUN chmod 544 /reload-nginx-config.sh

COPY init-nginx.conf /init-nginx.conf
RUN chmod 644 /init-nginx.conf

EXPOSE 80 443

VOLUME /docker

CMD ["/entrypoint.sh"]

####################################################################################################
