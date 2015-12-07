#!/bin/bash

cd ~/Downloads

# download groovy-all JAR
wget -c http://repo1.maven.org/maven2/org/codehaus/groovy/groovy-all/2.1.1/groovy-all-2.1.1.jar

# copy groovy-all to JMeter classpath
cp groovy-all-2.1.1.jar apache-jmeter-2.9/lib
