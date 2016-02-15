project (libxinitthread)
cmake_minimum_required (VERSION 2.8.8)
add_definitions(-std=gnu99 -Wall -fvisibility=hidden -fPIC)

find_package(X11 REQUIRED)

include_directories (
	${X11_INCLUDE_DIRS}
	${CMAKE_BINARY_DIR}
	.
)

# put compiled library to the build directory root
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})

add_library (xinitthread SHARED xinitthreads.c)
target_link_libraries (xinitthread -lpthread -lX11)


