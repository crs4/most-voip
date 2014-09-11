#!/bin/bash
echo downloading dependences...
wget "https://space.crs4.it/public.php?service=files&t=958d5aff472a693db50aea0f3e9473ca&download" -O restund/re-0.4.2.tar.gz
wget "https://space.crs4.it/public.php?service=files&t=329105910cd2944b718f9d92b69f08d8&download" -O restund/restund-0.4.2.tar.gz
echo building image from docker file...
sudo docker build --force-rm=true -t="slm1977/most_voip_asterisk:v1" .

echo running new container with asterisk port mapping
#sudo docker run -t -i -p 3478:3478/tcp -p 9080:80 -p 5060:5060/tcp -p 5060:5060/udp slm1977/most_voip_asterisk:v1 /usr/local/bin/asterisk_service_runner.sh
#sudo docker run -t -i -p 3478:3478/tcp -p 5062:5062/tcp  slm1977/most_voip_asterisk:v1 /bin/bash
#sudo docker run -t -i -p 3478:3478/tcp -p 5062:5062/tcp  slm1977/most_voip_asterisk:v1 usr/local/bin/asterisk_service_runner.sh
#sudo docker run -t -i  slm1977/most_voip_asterisk:v1 /bin/bash
#sudo docker run -t -i -p 3478:3478/tcp -p 3478:3478/udp  -p 34780:34780/tcp -p 34780:34780/udp  -p 9080:80 -p 5060:5060/tcp -p 5060:5060/udp -p 16384:16384/udp -p 16385:16385/udp -p 16386:16386/udp -p 16387:16387/udp -p 16388:16388/udp -p 16389:16389/udp -p 16390:16390/udp -p 16391:16391/udp -p 16392:16392/udp -p 16393:16393/udp -p 16394:16394/udp slm1977/most_voip_asterisk:v1 /bin/bash
sudo docker run -t -i -p 3478:3478/tcp -p 3478:3478/udp  -p 34780:34780/tcp -p 34780:34780/udp  -p 9080:80 -p 5062:5062/tcp -p 5062:5062/udp -p 16384:16384/udp -p 16385:16385/udp -p 16386:16386/udp -p 16387:16387/udp -p 16388:16388/udp -p 16389:16389/udp -p 16390:16390/udp -p 16391:16391/udp -p 16392:16392/udp -p 16393:16393/udp -p 16394:16394/udp slm1977/most_voip_asterisk:v1 /bin/bash
