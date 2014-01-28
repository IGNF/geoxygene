@echo off

REM java -Xms1024m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=256m 
REM      -Duser.language=en -jar "geoxygene-appli-1.6.jar" 

REM java -Xms1024m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=256m -Duser.language=en -cp "geoxygene-appli-1.6.jar;lib/*.jar" fr.ign.cogit.geoxygene.appli.GeOxygeneApplication

set JAVA="java"

set JAVA_MAXMEM="-Xms1024m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=256m"
set GEOX_OPTS="-Duser.language=en"
set GEOX_LIB=".\lib"

set MAIN="fr.ign.cogit.geoxygene.sig3d.Launcher"

set JARS=.
set CLASSPATH=
for %%i in (.\lib\*.jar) do call cpappend.bat %%i
set CLASSPATH=%JARS%;./conf;geoxygene-sig3d-1.7-SNAPSHOT.jar;
REM echo %CLASSPATH%
 
%JAVA% "%JAVA_MAXMEM%" "%GEOX_OPTS%" -cp %CLASSPATH% %MAIN%






