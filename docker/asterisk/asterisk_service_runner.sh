#!/bin/bash
#echo "Restarting asterisk with sudo"
sudo service asterisk restart
echo "opening console"
sudo asterisk -r