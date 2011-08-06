set SCRIPT_DIR=%~dp0
java -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256m -Xmx712M -Xss2M -jar "%SCRIPT_DIR%\sbt-launcher-0.10.1.jar" %*
