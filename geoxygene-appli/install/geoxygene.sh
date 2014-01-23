#!/bin/sh

## uncomment and put the path to your jre here
#JAVA_HOME="/home/ed/jre1.6.0_21"

## uncomment and change your memory configuration here 
## Xms is initial size, Xmx is maximum size
## values are ##M for ## Megabytes, ##G for ## Gigabytes
JAVA_MAXMEM="-Xms1024m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=256m"

GEOX_HOME=`dirname "$0"`/.
GEOX_LIB="./lib"

GEOX_OPTS="-Duser.language=en"

JAVA="java"
MAIN="fr.ign.cogit.geoxygene.appli.GeOxygeneApplication"
echo "classpath = " $CLASSPATH

for libfile in "$GEOX_LIB/"*.jar
do
  CLASSPATH="$libfile":"$CLASSPATH";
  echo "libfile = " $libfile
done
CLASSPATH=.:./conf:$CLASSPATH
export CLASSPATH;

echo "classpath = " $CLASSPATH

echo $JAVA $JAVA_MAXMEM -cp "$CLASSPATH" $MAIN $*

$JAVA $JAVA_MAXMEM $GEOX_OPTS -cp "$CLASSPATH":geoxygene-appli-1.6.jar $MAIN $*
