package com.packetsniffer.emenegal.packetsniffer;

import android.util.Log;

import com.packetsniffer.emenegal.packetsniffer.activities.MainActivity;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.BPrecision;
import com.packetsniffer.emenegal.packetsniffer.database.DBHelper;
import com.packetsniffer.emenegal.packetsniffer.packet.Packet;
import com.packetsniffer.emenegal.packetsniffer.packetRebuild.PCapFileWriter;
import com.packetsniffer.emenegal.packetsniffer.transport.tcp.TCPHeader;

import java.io.IOException;

public class StorageManager{

    public static final String TAG = StorageManager.class.getSimpleName();

    public static final StorageManager INSTANCE = new StorageManager();

    @BPrecision
    public static boolean storePacketInFile;
    @BPrecision(false)
    public static boolean storePacketInDB;
    @BPrecision(false)
    public static boolean storeOutgoing;
    @BPrecision(false)
    public static boolean storeIncoming;
    @BPrecision(false)
    public static boolean storeTCP;
    @BPrecision(false)
    public static boolean storeUDP;
    @BPrecision(false)
    public static boolean storeHTTP;
    @BPrecision(false)
    public static boolean storeHTTPS;

    private StorageManager(){}


    /**
     * Store the packet in the database according to the current strategy
     * @param packet
     */
    public void storePacket(Packet packet) {
        boolean store = false;
        if(storePacketInDB){
            if(packet.isHttps() && storeHTTPS)
                store = true;
            else if(packet.isHttp() && storeHTTP)
                store = true;
            else if(packet.isOutgoing() && storeIncoming)
                store = true;
            else if(packet.getTransportHeader() instanceof TCPHeader && storeTCP)
                store = true;
        }

        if(store)
            savePacketInDB(packet);
    }

    /**
     * Store the packet data in a file according to the current strategy
     * @param packet
     * @param pcapOutput
     */
    public void storePacket(byte[] packet, PCapFileWriter pcapOutput) {
        if(storePacketInFile)
            savePacketInFile(packet,pcapOutput);
    }


    /**
     * Store the packet data in a file
     * @param packet
     * @param pcapOutput
     */
    private void savePacketInFile(byte[] packet, PCapFileWriter pcapOutput){
        if (pcapOutput != null) {
            try {
                pcapOutput.addPacket(packet, 0, packet.length, System.currentTimeMillis() * 1000000);
            } catch (IOException e) {
                Log.e(TAG, "pcapOutput.addPacket IOException :" + e.getMessage());
                e.printStackTrace();
            }
        }else{
            Log.e(TAG, "overrun from capture: length:"+packet.length);
        }
    }

    /**
     * Store the packet in the database
     * @param packet
     */
    private void savePacketInDB(Packet packet){
        Log.i(TAG,"Add packet to DB");
        DBHelper.INSTANCE.getDBHelper(MainActivity.getContext()).getNetActivityDao().create(packet.getPacketModel());
    }

}
