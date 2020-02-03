#!/bin/sh

echo "应用程序将在 ${RUN_SLEEP} 秒后启动" && sleep ${RUN_SLEEP}
exec java ${JAVA_OPTS} -noverify -XX:+AlwaysPreTouch -Djava.security.egd=file:/dev/./urandom -jar "${HOME}/app.jar" "$@"
