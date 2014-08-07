FROM ubuntu:14.04

MAINTAINER stefano.monni@crs4.it

RUN apt-get update && apt-get upgrade -y && apt-get clean
RUN apt-get -qqy install asterisk

COPY ./asterisk/sip.conf /etc/asterisk/
COPY ./asterisk/extensions.conf /etc/asterisk/
COPY ./asterisk/modules.conf /etc/asterisk/
RUN chmod 755 /etc/asterisk/*

COPY ./asterisk/asterisk_service_runner.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/asterisk/asterisk_service_runner.sh

