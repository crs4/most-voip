#!/bin/bash

# Stopping Docker and removing docker0
sudo service docker stop
sudo ip link set dev docker0 down
sudo brctl delbr docker0

# Create our own bridge

sudo brctl addbr bridge0
sudo ip addr add 156.148.133.240/23 dev bridge0
sudo ip link set dev bridge0 up

# sudo echo 'DOCKER_OPTS="-b=bridge0"' >> /etc/default/docker

echo starting docker...
sudo service docker start