#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
mvn clean package
mvn assembly:assembly
