cmake_minimum_required(VERSION 3.4.1)

add_library( native_lib
          SHARED
          src/main/cpp/packet.c
          src/main/cpp/util.c )

include_directories( src/main/jni/ )

find_library( log-lib
           log )

target_link_libraries( native_lib
${log-lib} )
