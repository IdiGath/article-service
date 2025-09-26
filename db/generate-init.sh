#!/bin/bash
export JAVA_USER_PASSWORD=$(grep JAVA_USER_PASSWORD ../.env | cut -d '=' -f2)
envsubst < init.sql.template > init.sql
