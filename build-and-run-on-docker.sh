#!/bin/sh
./gradlew bootBuildImage --imageName=task/transactions && docker-compose up