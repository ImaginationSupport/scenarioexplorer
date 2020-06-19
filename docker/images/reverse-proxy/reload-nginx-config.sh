#!/usr/bin/env bash

set -eu

cp /docker/nginx.conf.tmpl /etc/confd/templates/nginx.conf.tmpl

#echo "===== before ====="
#cat /etc/nginx/nginx.conf

#echo "===== template ====="
#cat /etc/confd/templates/nginx.conf.tmpl

# run confd to update the config files
confd -onetime -backend env

#echo "===== after ====="
#cat /etc/nginx/nginx.conf

#echo "====="

echo "Reloading Nginx config..."
/usr/sbin/nginx -s reload

echo "done."
echo ""
