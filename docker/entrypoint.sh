#!/bin/bash

set -e

pattern="/data/navexplorer-indexer-*.jar"
files=($pattern)

exec java -jar -Dspring.profiles.active=testnet -Dspring.config.location=file:/config ${files[0]} > /var/log/navexplorer-indexer.log