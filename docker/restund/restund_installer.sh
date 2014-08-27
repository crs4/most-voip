#!/bin/bash
echo untar restund libraries ...
cd /tmp/
tar -zxf re-0.4.2.tar.gz
tar -zxf restund-0.4.2.tar.gz
cd re-0.4.2
echo installing re libraries ...
make CFLAGS="-D_GNU_SOURCE -Iinclude"
sudo make install
cd ../restund-0.4.2
echo installing restund ...
make  
sudo make install 
sudo cp /usr/local/lib/libre.so /usr/lib/



