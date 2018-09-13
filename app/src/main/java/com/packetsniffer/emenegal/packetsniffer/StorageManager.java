package com.packetsniffer.emenegal.packetsniffer;

import android.util.Log;

import com.emenegal.battery_saving.annotation.EPrecision;
import com.emenegal.battery_saving.annotation.IPrecision;
import com.emenegal.battery_saving.annotation.ResourceStrategy;
import com.emenegal.battery_saving.enumeration.IEnum;
import com.emenegal.battery_saving.method.ExponentialMethod;
import com.emenegal.battery_saving.method.LinearMethod;
import com.emenegal.battery_saving.method.LogarithmMethod;
import com.packetsniffer.emenegal.packetsniffer.activities.MainActivity;
import com.emenegal.battery_saving.annotation.BPrecision;
import com.packetsniffer.emenegal.packetsniffer.database.DBHelper;
import com.packetsniffer.emenegal.packetsniffer.packet.Packet;
import com.packetsniffer.emenegal.packetsniffer.packetRebuild.PCapFileWriter;
import com.packetsniffer.emenegal.packetsniffer.transport.udp.UDPHeader;
import java.io.IOException;

@ResourceStrategy
public class StorageManager{

    @IPrecision(lower = 0, higher = 20, method = LinearMethod.class, params = {})
    public static double aValue;

    public static final String TAG = StorageManager.class.getSimpleName();

    public static final StorageManager INSTANCE = new StorageManager();

    @BPrecision(value = true,threshold = 90)
    public static boolean storePacketInFile = true;
    @BPrecision(value = true,threshold = 85)
    public static boolean storeIncoming = true;
    @BPrecision(value = true, threshold = 50)
    public static boolean storeUDP = true;
    @BPrecision(value = true, threshold = 35)
    public static boolean storeHTTP = true;
    @BPrecision(value = true,threshold = 10)
    public static boolean storeHTTPS = true;
    @BPrecision(value = true,threshold = 80)
    public static boolean storePacketInDB = true;

    private StorageManager(){}


    /**
     * Store the packet in the database according to the current strategy
     * @param packet
     */
    public void storePacket(Packet packet) {
        boolean store = false;
        if(!storePacketInDB){
            if(packet.isInComing() && !storeIncoming){
                return;
            }else{
                if(packet.getTransportHeader() instanceof UDPHeader) {
                    if (storeUDP)
                        store = true;
                    else
                        return;
                }else{
                    if(packet.isHttps() && storeHTTPS)
                        store = true;
                    else if(packet.isHttp() && storeHTTP)
                        store = true;
                }
            }
        }else{
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
        //Log.i(TAG,"Add packet to DB");
        DBHelper.INSTANCE.getDBHelper(MainActivity.getContext()).getNetActivityDao().create(packet.getPacketModel());
    }

}
