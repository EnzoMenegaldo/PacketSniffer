//
// Created by emenegal on 6/19/18.
//

#ifndef PACKETSNIFFERANDROID_UTILS_H
#define PACKETSNIFFERANDROID_UTILS_H

#endif //PACKETSNIFFERANDROID_UTILS_H

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>
#include <unistd.h>
#include <pthread.h>
#include <setjmp.h>
#include <errno.h>
#include <fcntl.h>
#include <dirent.h>
#include <poll.h>
#include <sys/types.h>
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <sys/epoll.h>
#include <dlfcn.h>
#include <sys/stat.h>
#include <sys/resource.h>
#include <inttypes.h>

#include <netdb.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <netinet/in6.h>
#include <netinet/ip.h>
#include <netinet/ip6.h>
#include <netinet/udp.h>
#include <netinet/tcp.h>
#include <netinet/ip_icmp.h>
#include <netinet/icmp6.h>

#include <android/log.h>
#include <sys/system_properties.h>


#define UID_MAX_AGE 30000 // milliseconds
#define TAG "PacketSniffer.JNI"

struct uid_cache_entry {
    uint8_t version;
    uint8_t protocol;
    uint8_t saddr[16];
    uint16_t sport;
    uint8_t daddr[16];
    uint16_t dport;
    jint uid;
    long time;
};

int handle_ip(const uint8_t *pkt, size_t length);

jint get_uid(const int version, const int protocol,
             const char* saddr, const uint16_t sport,
             const char* daddr, const uint16_t dport);

jint get_uid_sub(const int version, const int protocol,
                 const void *saddr, const uint16_t sport,
                 const void *daddr, const uint16_t dport,
                 const char *source, const char *dest,
                 long now);

void log_android(int prio, const char *fmt, ...);

void hex2bytes(const char *hex, uint8_t *buffer);


uint8_t char2nible(const char c);


int is_lower_layer(int protocol);

int is_upper_layer(int protocol);

uint16_t calc_checksum(uint16_t start, const uint8_t *buffer, size_t length);







