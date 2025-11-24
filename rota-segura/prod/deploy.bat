rem gcloud compute ssh --project galvanic-tea-477602-t7 rotasegura2@%1 --command "bash"
cmd /c gcloud compute ssh --project galvanic-tea-477602-t7 rotasegura2@%1 --command "rm run.sh"
cmd /c gcloud compute ssh --project galvanic-tea-477602-t7 rotasegura2@%1 --command "rm docker-compose.yml"
cmd /c gcloud --project galvanic-tea-477602-t7 compute scp ..\prod\run.sh rotasegura2@%1:run.sh
cmd /c gcloud --project galvanic-tea-477602-t7 compute scp ..\prod\docker-compose.yml rotasegura2@%1:docker-compose.yml
cmd /c gcloud compute ssh --project galvanic-tea-477602-t7 rotasegura2@%1 --command "bash run.sh"