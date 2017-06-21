#/bin/bash
node -v
npm -v

npm install
env=production npm run build

zip -r dist.zip dist
scp ./dist.zip  root@47.93.184.240:/opt/www
ssh root@47.93.184.240 "rm -rf /opt/www/dist;unzip /opt/www/dist.zip -d /opt/www/"
ssh root@47.93.184.240 "rm -rf /opt/www/sizzler;mv /opt/www/dist /opt/www/sizzler"
