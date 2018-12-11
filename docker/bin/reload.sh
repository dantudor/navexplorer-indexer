#!/usr/bin/env bash
mvn clean install -U
cp docker/config/softForks.json target/softForks.json
cp docker/config/addressLabels.json target/addressLabels.json
docker-compose restart indexer