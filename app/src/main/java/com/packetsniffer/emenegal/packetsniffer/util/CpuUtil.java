package com.packetsniffer.emenegal.packetsniffer.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.packetsniffer.emenegal.packetsniffer.activities.MainActivity;

public final class CpuUtil {
    private static final String cpu_fileName = "cpu_log.txt";

    private static final int CPU_WINDOW = 1000;

    private static final int CPU_REFRESH_RATE = 100; // Warning: anything but > 0

    private static HandlerThread handlerThread;


    private static boolean monitorCpu;

    public static BufferedWriter bw;
    private File cpu_file;

    /**
     * Set the outfile and open the buffer writer. This method should be called in
     * {@link Application#onCreate()}
     **/
    public void setOutput() {
        cpu_file = new File(MainActivity.getContext().getExternalFilesDir(null)+ File.separator+ cpu_fileName);
        try {
            if(!cpu_file.exists()) {
                cpu_file.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(cpu_file,true));
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Start CPU monitoring */
    public static boolean startCpuMonitoring() {
        CpuUtil.monitorCpu = true;

        handlerThread = new HandlerThread("CPU monitoring"); //$NON-NLS-1$
        handlerThread.start();

        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                while (CpuUtil.monitorCpu) {

                    LinuxUtils linuxUtils = new LinuxUtils();

                    int pid = android.os.Process.myPid();
                    String cpuStat1 = linuxUtils.readSystemStat();
                    String pidStat1 = linuxUtils.readProcessStat(pid);

                    try {
                        Thread.sleep(CPU_WINDOW);
                    } catch (Exception e) {
                    }

                    String cpuStat2 = linuxUtils.readSystemStat();
                    String pidStat2 = linuxUtils.readProcessStat(pid);

                    float cpu = linuxUtils
                            .getSystemCpuUsage(cpuStat1, cpuStat2);
                    if (cpu >= 0.0f) {
                        try {
                            bw.write("total " + Float.toString(cpu)+"\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    String[] toks = cpuStat1.split(" ");
                    long cpu1 = linuxUtils.getSystemUptime(toks);

                    toks = cpuStat2.split(" ");
                    long cpu2 = linuxUtils.getSystemUptime(toks);

                    cpu = linuxUtils.getProcessCpuUsage(pidStat1, pidStat2,
                            cpu2 - cpu1);
                    if (cpu >= 0.0f) {
                        try {
                            bw.write("pid " + pid + " "+ Float.toString(cpu)+"\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        synchronized (this) {
                            wait(CPU_REFRESH_RATE);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }

                Log.i("THREAD CPU", "Finishing");
            }

        });

        return CpuUtil.monitorCpu;
    }

    /** Stop CPU monitoring */
    public static void stopCpuMonitoring() {
        if (handlerThread != null) {
            monitorCpu = false;
            handlerThread.quit();
            handlerThread = null;
        }
    }

    /** Dispose of the object and release the resources allocated for it */
    public void dispose() {

        monitorCpu = false;
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
