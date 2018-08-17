package com.packetsniffer.emenegal.packetsniffer.packet;

import android.util.Log;

import com.packetsniffer.emenegal.packetsniffer.socket.IReceivePacket;

import java.util.ArrayList;
import java.util.List;

public class PacketPublisher implements Runnable{

    private static final String TAG = PacketPublisher.class.getSimpleName();
    private List<IReceivePacket> subscribers;
    private final PacketManager packetManager;
    private volatile boolean isShuttingDown = false;

    public PacketPublisher(){
        packetManager = PacketManager.getInstance();
        subscribers = new ArrayList<>();
    }

    /**
     * register a subscriber who wants to receive packet
     * @param subscriber a subscriber who wants to receive packet
     */
    public void subscribe(IReceivePacket subscriber){
        if(!subscribers.contains(subscriber)){
            subscribers.add(subscriber);
        }
    }

    @Override
    public void run() {
        Log.d(TAG,"BackgroundWriter starting...");

        while(!isShuttingDown()) {
            Packet packet = packetManager.getPacket();
            if(packet != null) {
                for(IReceivePacket subscriber: subscribers)
                    subscriber.receive(packet);
            } else {
                try {
                    synchronized (packetManager) {
                        packetManager.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG,"BackgroundWriter ended");
    }
    private boolean isShuttingDown() {
        return isShuttingDown;
    }
    public void setShuttingDown(boolean shuttingDown) {
        this.isShuttingDown = shuttingDown;
    }

}
