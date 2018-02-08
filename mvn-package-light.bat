call mvn-install-jars.bat
mvnw package -Dmaven.test.skip=true -Pprod -e
rem pause
