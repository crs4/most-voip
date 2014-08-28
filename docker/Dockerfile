FROM ubuntu:14.04

MAINTAINER stefano.monni@crs4.it

RUN apt-get update && apt-get upgrade -y && apt-get clean
RUN apt-get -qqy install asterisk
RUN apt-get update && apt-get -qqy install gcc
RUN apt-get -qqy install make

COPY ./restund/re-0.4.2.tar.gz /tmp/
COPY ./restund/restund-0.4.2.tar.gz /tmp/
COPY ./restund/restund_installer.sh /tmp/

COPY ./restund/restund.conf /etc/

RUN chmod +x /tmp/restund_installer.sh
RUN /tmp/restund_installer.sh

COPY ./asterisk/sip.conf /etc/asterisk/
COPY ./asterisk/extensions.conf /etc/asterisk/
COPY ./asterisk/rtp.conf /etc/asterisk/
COPY ./asterisk/modules.conf /etc/asterisk/
RUN chmod 755 /etc/asterisk/*
COPY ./asterisk/asterisk_service_runner.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/asterisk_service_runner.sh

