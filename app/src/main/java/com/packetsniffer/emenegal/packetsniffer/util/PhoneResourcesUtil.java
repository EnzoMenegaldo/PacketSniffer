package com.packetsniffer.emenegal.packetsniffer.util;


import android.app.ActivityManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.packetsniffer.emenegal.packetsniffer.activities.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import static android.content.Context.ACTIVITY_SERVICE;

public class PhoneResourcesUtil {

    public static final PhoneResourcesUtil INSTANCE = new PhoneResourcesUtil();
    private static final String fileName = "battery_log";

    private boolean monitoring = true;
    private final long startingTime = new Date().getTime();


    public static BufferedWriter bw;

    private PhoneResourcesUtil(){
        try {
            File file = new File(MainActivity.getContext().getExternalFilesDir("")+ File.separator +fileName+"_"+(new Date()).getTime());
            if(!file.exists())
                file.createNewFile();
            bw = new BufferedWriter(new FileWriter(file,true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculate the percentage of available memory left
     * @return the percentage
     */
    // https://stackoverflow.com/questions/3170691/how-to-get-current-memory-usage-in-android/3192348#3192348
    public double getAvailableMemory() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) MainActivity.getContext().getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        int maxMemory = activityManager.getMemoryClass();

        //Runtime.getRuntime().gc();
        long memoryAvailable = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        //Percentage can be calculated for API 16+
        //return mi.availMem / (double)mi.totalMem * 100.0;

        https://stackoverflow.com/questions/6073744/android-how-to-check-how-much-memory-is-remaining/41373601#41373601
        return memoryAvailable/1048576; // in MB
    }

    /**
     * @return true if the available memory is low
     */
    public boolean isLowMemory(){
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) MainActivity.getContext().getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        return mi.lowMemory;
    }


    public void startMonitoring(){
        new Thread(){
            @Override
            public void run(){
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus;
                int battery_level;

                while (monitoring) {
                    try {
                        batteryStatus = MainActivity.getContext().registerReceiver(null, ifilter);
                        battery_level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                        bw.write(((new Date()).getTime()-startingTime)/1000+","+battery_level+"\n");
                        bw.flush();

                        Thread.sleep(60000);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void stopMonitoring(){
        try {
            monitoring = false;
            bw.close();
        } catch (IOException e) {

        }
    }
}
