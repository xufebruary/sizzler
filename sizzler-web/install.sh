#/bin/bash
node -v
npm -v

npm install
env=production npm run build

zip -r dist.zip dist
