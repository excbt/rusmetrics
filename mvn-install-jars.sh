#!/bin/bash
./mvnw install:install-file -Dfile=./libs/ru/excbt/jasper/fonts_rus/1.0/fonts_rus-1.0.jar -DgroupId=ru.excbt.jasper -DartifactId=fonts_rus -Dversion=1.0 -Dpackaging=jar
./mvnw install:install-file -Dfile=./libs/ru/excbt/jasper/jasperreports-functions/6.3.0/jasperreports-functions-6.3.0.jar -DgroupId=ru.excbt.jasper -DartifactId=jasperreports-functions -Dversion=6.3.0 -Dpackaging=jar