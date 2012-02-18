#!/bin/bash
APPDIR=$(dirname $0)
if [[ -z $(pidof redis-server) ]]; then
  echo "Starting Redis server"
  redis-server $APPDIR/redis/redis.conf >/dev/null&
fi
echo "Starting web server"
exec lein run -m testify.core
