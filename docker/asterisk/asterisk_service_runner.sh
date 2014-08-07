#!/bin/bash
echo "Restarting asterisk"
service asterisk restart
echo "opening console"
asterisk -r