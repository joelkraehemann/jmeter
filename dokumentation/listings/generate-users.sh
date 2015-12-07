#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
/usr/lib/jvm/java-7-openjdk-amd64/bin/java -jar target/resttool-1.0-SNAPSHOT-jar-with-dependencies.jar -g user 500 -o user.csv
