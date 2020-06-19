#!/usr/bin/env bash

set -e

# copy the base catalina.properties file as the template
echo "##################################################"
echo "Copying the base catalina.properties..."
echo ""
cp /usr/local/tomcat/conf/catalina.properties /etc/confd/templates/catalina.properties.tmpl
#tail -n 20 /etc/confd/templates/catalina.properties.tmpl
#echo ""

# append the suffix file
echo "##################################################"
echo "Appending suffix file..."
echo ""
cat /etc/confd/templates/catalina.properties.suffix.tmpl >> /etc/confd/templates/catalina.properties.tmpl
#tail -n 20 /etc/confd/templates/catalina.properties.tmpl
#echo ""

# run confd to update the config files
echo "##################################################"
echo "running confd..."
echo ""
confd -onetime -backend env

echo "##################################################"
echo "starting tomcat..."
echo ""

# start tomcat
echo USE_TOMCAT_DEBUGGING: ${USE_TOMCAT_DEBUGGING}
if [[ -z "${USE_TOMCAT_DEBUGGING}" ]]; then
	# debugging flag NOT set, so start normally
	echo "starting tomcat..."
	catalina.sh run
else
	echo "starting tomcat with debugging..."
	export JPDA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9090"
	catalina.sh jpda run
fi
