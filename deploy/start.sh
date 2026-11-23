#!/bin/sh
set -eu

# Render supplies private addresses as host:port. Spring uses HTTP base URLs.
for service in USER GAME TEAM TOURNAMENT REGISTRATION MATCH RESULT RANKING SANCTION PRIZE NOTIFICATION AUTH; do
  address="$(printenv "${service}_SERVICE_HOSTPORT" || true)"
  if [ -n "$address" ]; then
    export "${service}_SERVICE_URL=http://$address"
  fi
done

exec java -jar /app/app.jar "$@"
