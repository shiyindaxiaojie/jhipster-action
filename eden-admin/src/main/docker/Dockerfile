FROM openjdk:8-jre-alpine

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    RUN_SLEEP=0 \
    JAVA_OPTS=""

RUN adduser -D -s /bin/sh eden
WORKDIR /home/eden

ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh && chown eden:eden entrypoint.sh
USER eden

ADD *.war app.war

ENTRYPOINT ["./entrypoint.sh"]

EXPOSE 8080 5701/udp
