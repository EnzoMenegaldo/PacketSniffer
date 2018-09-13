package com.packetsniffer.emenegal.packetsniffer.benchmark;

import android.app.Service;
import android.content.Intent;

import android.os.IBinder;

import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.packetsniffer.emenegal.packetsniffer.util.PhoneResourcesUtil;

import java.io.IOException;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class Benchmark extends Service {


    private static final String TAG = "Benchmark";
    public static final String BENCHMARK_FINISHED = "benchmark_finished";
    public static final String BENCHMARK_RUNNING = "benchmark_run";
    public static final String BENCHMARK_UPDATE_TIMER = "update_timer";

    private static final String[] urls = {"https://www.google.com","https://fleep.io","https://intranet.inria.fr","https://developer.android.com","https://stackoverflow.com","https://github.com","https://www.google.fr","https://www.google.ca","https://www.spotify.com/fr/","https://www.instagram.com/?hl=fr","https://www.apple.com/fr/","https://www.pinterest.fr/","https://www.deezer.com/fr/","https://fr.linkedin.com/","https://www.whatsapp.com/","https://www.youtube.com","https://www.facebook.com","https://www.amazon.com","https://www.ebay.com","https://www.twitter.com"};
    //private static final String[] urls = {"https://www.google.com","https://www.instagram.com/?hl=fr","https://www.apple.com/fr/","https://www.pinterest.fr/","https://www.deezer.com/fr/","https://fr.linkedin.com/","https://www.whatsapp.com/","https://www.youtube.com","https://www.facebook.com","https://www.amazon.com"};
    //private static final String[] urls = {"https://www.google.com","https://www.instagram.com/?hl=fr"};
    private static final long startingTime = System.currentTimeMillis();
    private static final long duration = 16*60*60*1000;
    public static final int BENCHMARK_FOREGROUND_ID = 777;

    public Benchmark() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(){
            @Override
            public void run() {
                startBenchmark();
            }
        }.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(Benchmark.TAG,"destroy benchmark");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public void executeGet(String url) {
        URL obj;
        HttpsURLConnection httpsConn = null;
        URLConnection urlConn = null;
        try {
            obj = new URL(url);
            urlConn = obj.openConnection();

            httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.setRequestProperty("Connection","close");


            int responseCode = httpsConn.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url + "  Response Code : " + responseCode);

            InputStream inputStream;
            if(responseCode == 200 || responseCode == 201)
                inputStream = httpsConn.getInputStream();
            else
                inputStream = httpsConn.getErrorStream();

            int ret = 0;
            byte[] buf = new byte[512];
            while ((ret = inputStream.read(buf)) > 0) {
                //processBuf(buf);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(httpsConn != null) {
                httpsConn.disconnect();
            }
        }
    }

    private void startBenchmark(){
        Log.d(Benchmark.TAG,"start benchmark");

        //Start background thread
        PhoneResourcesUtil.INSTANCE.startMonitoring();

        long currentTime = 0 ;

        while(currentTime < startingTime + duration) {
            for (String url : urls) {
                executeGet(url);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            currentTime = System.currentTimeMillis();
            Intent localIntent = new Intent(BENCHMARK_RUNNING);
            localIntent.putExtra("request",20);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }

        Log.d(Benchmark.TAG,"finish benchmark");


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Stop background thread and close file
        PhoneResourcesUtil.INSTANCE.stopMonitoring();

        //Send intent to tell the ui that the benchmark is done
        Intent localIntent = new Intent(BENCHMARK_FINISHED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
