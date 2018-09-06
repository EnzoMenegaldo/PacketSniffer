//
// Created by emenegal on 6/22/18.
//

#include "util.h"


JNIEXPORT jint JNICALL
Java_com_packetsniffer_emenegal_packetsniffer_packet_Packet_get_1uid(JNIEnv *env, jobject instance,
                                                                     jint ipVersion, jint protocol,
                                                                     jstring sourceIP_,
                                                                     jint sourcePort,
                                                                     jstring destinationIP_,
                                                                     jint destinationPort) {
    const char *sourceIP = (*env)->GetStringUTFChars(env, sourceIP_, 0);
    const char *destinationIP = (*env)->GetStringUTFChars(env, destinationIP_, 0);

    int uid =  get_uid(ipVersion, protocol, sourceIP, (const uint16_t) sourcePort, destinationIP, (const uint16_t) destinationPort);

    (*env)->ReleaseStringUTFChars(env, sourceIP_, sourceIP);
    (*env)->ReleaseStringUTFChars(env, destinationIP_, destinationIP);

    return uid;
}