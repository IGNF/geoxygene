@echo off

REM java -Xms1024m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=256m 
REM      -Duser.language=en -jar "geoxygene-appli-1.6.jar" 

REM set JAVA="java"

REM set JAVA_MAXMEM="-Xms1024m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=256m"
REM set GEOX_OPTS="-Duser.language=en"
REM set GEOX_LIB=".\lib"

REM set MAIN="fr.ign.cogit.geoxygene.appli.GeOxygeneApplication"

REM set JARS=.
REM set CLASSPATH=
REM for %%i in (.\lib\*.jar) do call cpappend.bat %%i
REM set CLASSPATH=%JARS%;./conf;geoxygene-appli-1.6.jar;
REM echo %CLASSPATH%
 
REM %JAVA% "%JAVA_MAXMEM%" "%GEOX_OPTS%" -cp %CLASSPATH% fr.ign.cogit.geoxygene.appli.GeOxygeneApplication



java -Xms1024m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=256m -Duser.language=en -cp "geoxygene-appli-1.6.jar;lib/*.jar" fr.ign.cogit.geoxygene.appli.GeOxygeneApplication


