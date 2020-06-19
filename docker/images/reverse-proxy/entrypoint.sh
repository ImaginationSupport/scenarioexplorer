#!/usr/bin/env bash

set -e

####################################################################################################################################################################################

if [ -f /docker/nginx.conf.tmpl ]; then

	# use the template from the volume
	echo "Copying nginx config from volume..."
	cp /docker/nginx.conf.tmpl /etc/confd/templates/nginx.conf.tmpl

	# run confd to update the config files
	echo "Running confd..."
	confd -onetime -backend env

else

	# volume template does not exist, so use the init version
	echo "Template in volume does not exist, copying init nginx config"
	cp /init-nginx.conf /etc/nginx/nginx.conf

fi

####################################################################################################################################################################################

if [ -n "${USE_EMERGENCY_MODE}" ]; then

	# in emergency mode, just run the shell and let the admin log in and fix whatever
	printenv
	echo ""
	while true; do sleep 1000; done

else

	# start nginx
#	exec dockerize -stdout /var/log/nginx/access.log -stderr /var/log/nginx/error.log /usr/sbin/nginx -c /etc/nginx/nginx.conf -g "daemon off;"
	exec dockerize /usr/sbin/nginx -c /etc/nginx/nginx.conf -g "daemon off;"

fi

####################################################################################################################################################################################
