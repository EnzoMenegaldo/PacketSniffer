package com.packetsniffer.emenegal.packetsniffer.benchmark;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.packetsniffer.emenegal.packetsniffer.PacketSnifferService;
import com.packetsniffer.emenegal.packetsniffer.activities.MainActivity;
import com.packetsniffer.emenegal.packetsniffer.util.PhoneResourcesUtil;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static org.apache.commons.httpclient.params.HttpMethodParams.USER_AGENT;

public class Benchmark extends JobIntentService {


    private static final String TAG = "Benchmark";
    public static final String BENCHMARK_FINISHED = "benchmark_finished";
    //private static final String[] urls = {"https://www.google.com","https://fleep.io","https://intranet.inria.fr","https://developer.android.com","https://stackoverflow.com","https://github.com","https://www.google.fr","https://www.google.ca","https://www.spotify.com/fr/","https://www.instagram.com/?hl=fr","https://www.apple.com/fr/","https://www.pinterest.fr/","https://www.deezer.com/fr/","https://fr.linkedin.com/","https://www.whatsapp.com/","https://www.youtube.com","https://www.facebook.com","https://www.amazon.com","https://www.ebay.com","https://www.twitter.com"};
    private static final String[] urls = {"https://www.google.com","https://www.instagram.com/?hl=fr","https://www.apple.com/fr/","https://www.pinterest.fr/","https://www.deezer.com/fr/","https://fr.linkedin.com/","https://www.whatsapp.com/","https://www.youtube.com","https://www.facebook.com","https://www.amazon.com"};
    private long startingDate;
    private static long duration = 8*60*60*1000;

    public Benchmark() {
        super();
        startingDate = System.currentTimeMillis();
        duration = 8*60*60*1000;
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        //StrategyManager.INSTANCE.initialize(context,activity.getAnnotationList());
        PhoneResourcesUtil.INSTANCE.startMonitoring();

        Log.d(Benchmark.TAG,"start benchmark");
        Intent start_vpn_intent = new Intent(getApplicationContext(), PacketSnifferService.class);
        startService(start_vpn_intent);

        long currentDate = 0 ;
        //Time in ms

        while(currentDate < startingDate + duration) {
            for (String url : urls) {
                executeGet(url);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            currentDate = System.currentTimeMillis();
        }

        PhoneResourcesUtil.INSTANCE.stopMonitoring();
        // StrategyManager.INSTANCE.stop(context);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Send intent to stop the vpn service
        Intent stop_vpn_intent = new Intent(PacketSnifferService.STOP_SERVICE_INTENT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(stop_vpn_intent);

        //Send intent to tell the ui that the benchmark is done
        Intent localIntent = new Intent(BENCHMARK_FINISHED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

    }

    @Override
    public boolean onStopCurrentWork() {
        //Send intent to stop the vpn service
        Intent stop_vpn_intent = new Intent(PacketSnifferService.STOP_SERVICE_INTENT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(stop_vpn_intent);
        return super.onStopCurrentWork();
    }


    public void executeGet(String url) {
        URL obj;
        try {
            obj = new URL(url);
            HttpURLConnection con;
            if(url.contains("https"))
                con = (HttpsURLConnection) obj.openConnection();
            else
                con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            /*String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }*/
            in.close();

            //print result
            //System.out.println(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
