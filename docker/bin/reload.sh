#!/usr/bin/env bash
mvn clean install -U
cp docker/config/softForks.json target/softForks.json
docker-compose restart indexer