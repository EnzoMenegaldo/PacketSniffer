package com.packetsniffer.emenegal.packetsniffer.util;


import android.app.ActivityManager;
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
    private static final String cpu_fileName = "cpu_log";
    private static final String memory_fileName = "memory_log";
    private boolean monitoring = true;
    private final long startingTime = new Date().getTime();


    public static BufferedWriter bw;

    private PhoneResourcesUtil(){
        File mem_file = new File(MainActivity.getContext().getExternalFilesDir(null) + File.separator + memory_fileName + "_" +(new Date()).getTime()+".txt");
        try {
            if(!mem_file.exists()) {
                mem_file.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(mem_file,true));
        }catch (IOException e) {
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

    public String executeTop() {
        java.lang.Process p = null;
        BufferedReader in = null;
        String returnString = null;
        try {
            p = Runtime.getRuntime().exec("dumpsys cpuinfo");
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((returnString = in.readLine()) != null) {
                System.out.println(returnString);
               /* if(returnString.contains("%cpu") && returnString.contains("%user")){
                    System.out.println(returnString);
                    break;
                }*/
            }
        } catch (IOException e) {
            Log.e("executeTop", "error in getting first line of top");
            e.printStackTrace();
        } finally {
            try {
                in.close();
                p.destroy();
            } catch (IOException e) {
                Log.e("executeTop",
                        "error in closing and destroying top process");
                e.printStackTrace();
            }
        }
        return returnString;
    }


    public void startCpuMonitoring(){
        new Thread(){
            @Override
            public void run(){
                while (monitoring) {
                    try {
                        bw.write(((new Date()).getTime()-startingTime)/1000+","+getAvailableMemory()+"\n");
                        bw.flush();
                        Thread.sleep(1000);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void stopCpuMonitoring(){
        try {
            monitoring = false;
            bw.close();
        } catch (IOException e) {

        }
    }
}
