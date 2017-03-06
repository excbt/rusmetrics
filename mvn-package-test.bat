call mvn-install-jars.bat
mvn clean package -Dmaven.test.skip=true -Ptest
pause
