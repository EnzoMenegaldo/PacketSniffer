package com.packetsniffer.emenegal.packetsniffer.util;


import android.app.ActivityManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v4.content.LocalBroadcastManager;

import com.packetsniffer.emenegal.packetsniffer.activities.MainActivity;
import com.packetsniffer.emenegal.packetsniffer.activities.OrmLiteActionBarActivity;
import com.packetsniffer.emenegal.packetsniffer.benchmark.Benchmark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import static android.content.Context.ACTIVITY_SERVICE;

public class PhoneResourcesUtil {

    public static final PhoneResourcesUtil INSTANCE = new PhoneResourcesUtil();
    private static final String fileName = "battery_log";

    private boolean monitoring = true;
    private final long startingTime = new Date().getTime();
    private File file;

    public static BufferedWriter bw;

    private PhoneResourcesUtil(){
        try {
            file = new File(MainActivity.getContext().getExternalFilesDir("")+ File.separator +fileName+"_"+ OrmLiteActionBarActivity.TIME+".txt");
            if(!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startMonitoring(){
        new Thread(){
            @Override
            public void run(){
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus;
                int battery_level;
                try {
                    bw = new BufferedWriter(new FileWriter(file,true));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                monitoring = true;

                while (monitoring) {
                    try {
                        batteryStatus = MainActivity.getContext().registerReceiver(null, ifilter);
                        battery_level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                        bw.write(((new Date()).getTime()-startingTime)/1000+","+battery_level+"\n");
                        bw.flush();

                        Intent localIntent = new Intent(Benchmark.BENCHMARK_UPDATE_TIMER);
                        LocalBroadcastManager.getInstance(MainActivity.getContext()).sendBroadcast(localIntent);

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
