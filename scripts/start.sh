#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu/app"

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

# jar 파일 실행
echo "$TIME_NOW > $PROJECT_ROOT/build/libs/*.jar 파일 실행" >> $DEPLOY_LOG
nohup java -jar $PROJECT_ROOT/build/libs/*.jar > $APP_LOG 2> $ERROR_LOG &

CURRENT_PID=$(pgrep -f $PROJECT_ROOT/build/libs/*.jar)
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> $DEPLOY_LOG
