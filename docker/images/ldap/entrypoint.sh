#!/bin/sh

set -e

#echo "==========================================================================="
#printenv
#echo "==========================================================================="

# run confd to update the config files
confd -onetime -backend env

# print the config file to the logs for debugging
if [ -n "${USE_LDAP_LOGGING}" ]; then
	cat /etc/openldap/slapd.conf
fi

# update the owner and permissions of the config files...
chown -R ldap:ldap /var/lib/openldap
chmod 700 /var/lib/openldap

chown -R ldap:ldap /etc/openldap
chmod 640 /etc/openldap/slapd.conf

mkdir -p /var/run/openldap
chown ldap:ldap /var/run/openldap

# start OpenLDAP
if [ -n "${USE_LDAP_LOGGING}" ]; then
	echo "Starting OpenLDAP with debugging..."
	slapd -d 256 -u ldap -g ldap # max logging
else
	echo "Starting OpenLDAP..."
	slapd -d 32768 -u ldap -g ldap # no logging
fi

# for debugging if it crashes, also turn off the set -e
#while true; do sleep 1000; done
