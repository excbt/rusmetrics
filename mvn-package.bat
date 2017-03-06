call mvn-install-jars.bat
mvn clean package -Dmaven.test.skip=true -Pprod -e
pause
