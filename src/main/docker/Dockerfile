# FROM openjdk:8-jre-alpine
FROM gitlab.local.excbt.ru:5050/docker/openjdk:8

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JHIPSTER_SLEEP=0 \
    JAVA_OPTS=""

# Add a jhipster user to run our application so that it doesn't need to run as root
RUN adduser -D -s /bin/sh sys101
WORKDIR /home/sys101

ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh && chown sys101:sys101 entrypoint.sh
USER sys101

ADD *.war app.war

ENTRYPOINT ["./entrypoint.sh"]

EXPOSE 8080 5701/udp

