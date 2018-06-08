call mvn-install-jars.bat
mvnw clean package -Dmaven.test.skip=false -Pprod -e
pause
