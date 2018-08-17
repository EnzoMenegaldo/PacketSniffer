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
import java.io.RandomAccessFile;
import java.util.Date;

import static android.content.Context.ACTIVITY_SERVICE;

public class PhoneResourcesUtil {

    public static final PhoneResourcesUtil INSTANCE = new PhoneResourcesUtil();
    private static final String cpu_fileName = "cpu_log.txt";
    private static final String memory_fileName = "memory_log.txt";
    private boolean cpu_monitoring = true;

    public static BufferedWriter bw;

    private PhoneResourcesUtil(){
        File mem_file = new File(MainActivity.getContext().getExternalFilesDir(null) + File.separator + cpu_fileName + "_" +(new Date()).getTime());
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

        //Percentage can be calculated for API 16+
        return mi.availMem / (double)mi.totalMem * 100.0;
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
                while (cpu_monitoring) {
                    try {
                        bw.write(getAvailableMemory()+"\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void stopCpuMonitoring(){
        try {
            cpu_monitoring = false;
            bw.close();
        } catch (IOException e) {

        }
    }
}
