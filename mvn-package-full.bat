call mvn-install-jars.bat
mvn clean package -Dmaven.test.skip=false -Pprod -e
pause
