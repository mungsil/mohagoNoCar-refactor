#!/bin/bash

cd /home/ubuntu/app

# Redis 컨테이너 실행 여부 확인
REDIS_CONTAINER_STATUS=$(docker ps | grep redis)
if [ -z "$REDIS_CONTAINER_STATUS" ]; then
  echo "Redis 컨테이너가 실행 중이지 않습니다"
  echo ">>> Pulling Redis image"
  docker compose -f docker-compose.redis.yml pull redis
  echo ">>> Starting Redis container"
  docker compose -f docker-compose.redis.yml up -d redis
else
  echo "Redis 컨테이너가 이미 실행 중입니다."
fi


DOCKER_APP_NAME=spring

# blue 컨테이너 실행 여부 확인
BLUE_CONTAINER_RUNNING=$(docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml ps -q | xargs docker inspect -f '{{.State.Running}}')

# 컨테이너 스위칭
if [ "$BLUE_CONTAINER_RUNNING" != "true" ]; then
	echo "[blue up]"
	docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml up -d --build

  ACTIVE_ENV="blue"
  STANDBY_ENV="green"
  ACTIVE_PORT="8081"

else
	echo "[green up]"
	docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml up -d --build

  ACTIVE_ENV="green"
  STANDBY_ENV="blue"
  ACTIVE_PORT="8082"

fi


# 헬스체크
MAX_RETRIES=30
RETRY_INTERVAL=2
HEALTH_CHECK_URL="http://localhost:$ACTIVE_PORT/actuator/health"
ACTIVE_HEALTH_CHECK_PASSED=false

echo "Waiting for application to be ready..."
for i in $(seq 1 $MAX_RETRIES); do
  if curl -s "$HEALTH_CHECK_URL" | grep -q "UP"; then
    echo "[Application is ready]"
    ACTIVE_HEALTH_CHECK_PASSED=true
    break

  fi
  echo "Waiting... ($i/$MAX_RETRIES)"
  sleep $RETRY_INTERVAL

done

# 새로운 컨테이너가 제대로 떴는지 확인
if [ "$ACTIVE_HEALTH_CHECK_PASSED" = "true" ]; then
  # 이전 컨테이너 종료
	docker-compose -p ${DOCKER_APP_NAME}-${STANDBY_ENV} -f docker-compose.${STANDBY_ENV}.yml down
  docker image prune -af
  echo "[Down] $STANDBY_ENV Down"

else
  echo "Health check failed after $MAX_RETRIES attempts"
  # 새 컨테이너 종료
  docker-compose -p ${DOCKER_APP_NAME}-${ACTIVE_ENV} -f docker-compose.${ACTIVE_ENV}.yml down
  # 디스코드 알림
  sudo ./discord_${ACTIVE_ENV}.sh

fi