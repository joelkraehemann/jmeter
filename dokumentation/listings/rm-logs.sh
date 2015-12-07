#!/bin/bash

# remove OpenOLAT directory and create it again.
rm -rf /opt/openolat/olatdata
mkdir /opt/openolat/olatdata
chown tomcat7.tomcat7 /opt/openolat/olatdata

# remove tomcat7 log file and create empty file
rm /var/lib/tomcat7/logs/catalina.out
touch /var/lib/tomcat7/logs/catalina.out
