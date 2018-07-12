#!/bin/bash

set -e

exec java -jar -Dspring.profiles.active=testnet -Dspring.config.location=file:/config /data/navexplorer-indexer-0.1.0.jar > /var/log/navexplorer-indexer.log