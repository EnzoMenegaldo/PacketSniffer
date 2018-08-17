package com.packetsniffer.emenegal.packetsniffer.packet;

import java.util.LinkedList;
import java.util.Queue;


public class PacketManager {

    private static final PacketManager instance = new PacketManager();
    private Queue<Packet> packets;

    private boolean storeHttp;
    private boolean storeHttps;
    private boolean storeAll;

    private PacketManager(){
        packets = new LinkedList<>();
    }

    public static PacketManager getInstance(){
        return instance;
    }

    public synchronized void addPacket(Packet packet) {
        packets.add(packet);
        synchronized (this) {
            this.notify();
        }
    }

    public synchronized Packet getPacket() {
        return packets.poll();
    }

}
