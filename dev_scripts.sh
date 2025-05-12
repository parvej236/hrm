#!/bin/bash
pwdir=$(pwd)

source ~/projects/microservices/scripts/set_environment_variable.sh

cd ~/projects/microservices/hrm/
mvn clean package

cd ~/projects/microservices/scripts/
docker compose up --build hrm &

cd $pwdir