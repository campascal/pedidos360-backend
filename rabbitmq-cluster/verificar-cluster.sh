#!/bin/sh
set -e

echo "=== ESTADO DEL CLUSTER RABBITMQ ==="
docker compose exec -T rabbitmq1 rabbitmqctl cluster_status

echo "=== COLAS DISPONIBLES ==="
docker compose exec -T rabbitmq1 rabbitmqctl list_queues name type messages consumers