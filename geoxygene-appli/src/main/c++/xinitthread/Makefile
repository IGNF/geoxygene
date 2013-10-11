all:
	g++ -o libxinitthread.so -shared -fPIC -Wl,-soname,libxx.so -L/usr/lib/X11 -I/usr/include/X11 xinitthread.c++ -lX11

clean:
	rm *.so

