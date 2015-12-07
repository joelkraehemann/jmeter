#!/bin/bash

cd ~/Downloads

# install openjdk
apt-get install openjdk-7-jdk

# download JMeter
wget -c http://mirror.switch.ch/mirror/apache/dist//jmeter/binaries/apache-jmeter-2.9.tgz

# unpack JMeter
tar -xzf apache-jmeter-2.9.tgz
