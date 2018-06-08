call mvn-install-jars.bat
mvnw clean package -Dmaven.test.skip=true -Ptest
pause
