#!/bin/bash

echo There are $# arguments to $0: $*
if [ "$#" -lt 1 ]; then
  echo "It creates a custom bridge for the container by providing a static ip address as input argument"
  echo "Usage: $0 ADDRESS_IP [-stop_docker]" >&2
  exit 1
fi

if [ "$#" -eq 2 ] && [ "$2" = "-stop_docker" ]; then
  echo Stopping Docker 
  sudo service docker stop
fi



sudo ip link set dev docker0 down
sudo brctl delbr docker0

# Create our own bridge

sudo brctl addbr bridge0
sudo ip addr add $1 dev bridge0
sudo ip link set dev bridge0 up

# sudo echo 'DOCKER_OPTS="-b=bridge0"' >> /etc/default/docker

echo starting docker...
sudo service docker start