FROM openjdk:7-jre-alpine

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    RUN_SLEEP=1 \
    JAVA_OPTS=""

RUN adduser -D -s /bin/sh eden
WORKDIR /home/eden

ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh && chown eden:eden entrypoint.sh
USER eden

ADD .jar app.jar

ENTRYPOINT ["./entrypoint.sh"]

EXPOSE 8761 5701/udp
