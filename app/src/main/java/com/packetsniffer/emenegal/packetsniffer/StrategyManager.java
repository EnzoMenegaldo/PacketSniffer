package com.packetsniffer.emenegal.packetsniffer;

import android.util.Log;

import com.packetsniffer.emenegal.packetsniffer.activities.MainActivity;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.ICollectionStrategy;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.Precision;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.ResourceStrategy;
import com.packetsniffer.emenegal.packetsniffer.database.DBHelper;
import com.packetsniffer.emenegal.packetsniffer.packet.Packet;
import com.packetsniffer.emenegal.packetsniffer.packetRebuild.PCapFileWriter;
import com.packetsniffer.emenegal.packetsniffer.transport.tcp.TCPHeader;

import java.io.IOException;

@ResourceStrategy
public class StrategyManager {
    public static final String TAG = StrategyManager.class.getSimpleName();

    public static final StrategyManager INSTANCE = new StrategyManager();

    private ICollectionStrategy strategy;


    @Precision(false)
    public static boolean storePacketInFile;
    @Precision(false)
    public static boolean storePacketInDB;
    @Precision(false)
    public static boolean storeOutgoing;
    @Precision(false)
    public static boolean storeIncoming;
    @Precision(false)
    public static boolean storeTCP;
    @Precision(false)
    public static boolean storeUDP;
    @Precision(false)
    public static boolean storeHTTP;
    @Precision(false)
    public static boolean storeHTTPS;

    private StrategyManager(){}

    public void setStrategy(ICollectionStrategy strategy) {
        this.strategy = strategy;
    }

    public ICollectionStrategy getStrategy() {
        return strategy;
    }


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
