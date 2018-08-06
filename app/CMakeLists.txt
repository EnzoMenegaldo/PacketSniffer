cmake_minimum_required(VERSION 3.4.1)

add_library( native-lib
          SHARED
          src/main/cpp/packet.c
          src/main/cpp/util.c )

include_directories( src/main/cpp/ )

find_library( log-lib
           log )

target_link_libraries( native-lib
${log-lib} )
