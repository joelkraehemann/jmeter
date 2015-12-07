#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64

cd ~/Downloads/qti_test

# invoke JMeter
../apache-jmeter-2.9/bin/jmeter -t loadtest.jmx
