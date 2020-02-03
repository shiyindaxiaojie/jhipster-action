@echo off

set APP_NAME=gateway
set APP_DIR=..\modules
set APP_PATTERN=%APP_DIR%\%APP_NAME%*.jar
for /f "delims=\" %%a in ('dir /b /a-d /o-d %APP_PATTERN%') do (
     set APP=%APP_DIR%\%%a
)

if "%1" == "start" (
     if exist "%JAVA_HOME%" (
          echo JAVA_HOME: %JAVA_HOME%
          echo JAVA_VERSION:
          java -version
     )
     echo start %APP_NAME%
     start "%APP_NAME%" java -server -Xms256m -Xmx1024m -XX:MaxPermSize=512m -jar %APP% --spring.config.location=../config/ --logging.file=../logs/%APP_NAME%.log
) else if "%1" == "stop" (
     echo stop %APP_NAME%
     taskkill /fi "WINDOWTITLE eq %APP_NAME%"
)
