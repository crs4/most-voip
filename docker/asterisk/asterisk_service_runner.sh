#!/bin/bash

HOST_IP=`hostname -I | tr -d ' '`
echo $HOST_IP 
sed -i "s/SERVER_IP/$HOST_IP/g" /etc/restund.conf

sudo /usr/local/sbin/restund -d -f /etc/restund.conf

#echo "Restarting asterisk with sudo"
sudo service asterisk restart
echo "opening console"
sudo asterisk -r