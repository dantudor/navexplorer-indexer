#!/usr/bin/env bash
mvn clean install -U
docker-compose restart indexer