#!/bin/sh

APP_NAME="uaa"
APP_DIR="../modules"
APP_PATTERN="$APP_DIR/$APP_NAME*.jar"
APP=`ls -l $APP_PATTERN | awk '${print $9}'`
APP_SERVER=`ps -ef | grep java | grep $APP | grep -v grep | awk '{print $2}'`

if [ $1 == "start" ]
then
    if [ -z "$JAVA_HOME" ]
    then
        echo JAVA_HOME: $JAVA_HOME
        echo JAVA_VERSION:
        java -version
    fi
    echo start $APP_NAME
    nohup java -server Xms256m -Xmx1024m -XX:MaxPermSize=512m -jar $APP --spring.config.location=../config/ --logging.file=../logs/%APP_NAME%.log >/dev/null 2>&1 &
elif [ $1 == "stop" ]
then
    echo stop $APP_NAME
    kill -9 $APP_SERVER
fi
