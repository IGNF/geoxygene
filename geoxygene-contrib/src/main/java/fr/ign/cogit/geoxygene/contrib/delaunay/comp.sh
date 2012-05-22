#/bin/sh
gcc -c -fPIC -DTRILIBRARY triangle.c trianguledll.c -I $JAVA_HOME/include -I $JAVA_HOME/include/linux
gcc -shared -fPIC triangle.o trianguledll.o -o libtrianguledll.so

