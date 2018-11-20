# FROM openjdk:8-jre-alpine
FROM gitlab.local.excbt.ru:5050/docker/openjdk:8

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JHIPSTER_SLEEP=0 \
    JAVA_OPTS=""

RUN mkdir -p /usr/share/fonts
RUN apk add --no-cache ttf-dejavu

RUN rm -rf \
    /var/lib/apt/lists/* \
    /tmp/* \
    /var/tmp/*

ENV TZ=Europe/Moscow
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Add a jhipster user to run our application so that it doesn't need to run as root
RUN adduser -D -s /bin/sh portal
WORKDIR /home/portal

ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh && chown portal:portal entrypoint.sh
USER portal

ADD *.war app.war

ENTRYPOINT ["./entrypoint.sh"]

EXPOSE 8080 5701/udp

