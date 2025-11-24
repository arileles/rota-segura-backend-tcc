#!/bin/bash
sudo apt update
sudo apt install docker-compose -y
gcloud auth configure-docker us-central1-docker.pkg.dev --quiet
docker-compose pull
docker-compose up -d
