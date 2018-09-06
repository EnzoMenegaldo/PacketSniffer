package com.packetsniffer.emenegal.packetsniffer.packet;

import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;


public class PacketManager {

    public static final PacketManager INSTANCE = new PacketManager();
    private Queue<Packet> packets;

    private PacketManager(){
        packets = new LinkedList<>();
    }

    public synchronized void addPacket(Packet packet) {
        //Log.i(PacketManager.class.getSimpleName(),"ADD");
        packets.add(packet);
        synchronized (this){
            this.notify();
        }
    }

    public synchronized Packet getPacket() {
        //Log.i(PacketManager.class.getSimpleName(),"GETTT");
        return packets.poll();
    }

}
