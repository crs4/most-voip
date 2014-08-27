#!/bin/bash
HOST_IP=`hostname -I`
echo $HOST_IP 
sed -i "s/SERVER_IP/$HOST_IP/g" restund/test.txt