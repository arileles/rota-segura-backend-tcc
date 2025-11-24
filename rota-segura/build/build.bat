podman compose build
gcloud auth print-access-token | podman login -u oauth2accesstoken --password-stdin us-central1-docker.pkg.dev/galvanic-tea-477602-t7/rotasegura
podman push us-central1-docker.pkg.dev/galvanic-tea-477602-t7/rotasegura/rotasegura-backend
podman push us-central1-docker.pkg.dev/galvanic-tea-477602-t7/rotasegura/rotasegura-frontend