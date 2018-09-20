package com.packetsniffer.emenegal.packetsniffer.benchmark;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.packetsniffer.emenegal.packetsniffer.activities.MainActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.SSLException;

public class HttpsRequestJob extends JobIntentService {


    //private static final String[] urls = {"https://www.google.com","https://www.google.fr","https://www.google.ca","https://www.spotify.com/fr/","https://www.instagram.com/?hl=fr","https://www.apple.com/fr/","https://www.pinterest.fr/","https://www.deezer.com/fr/","https://fr.linkedin.com/","https://www.whatsapp.com/","https://www.youtube.com","https://www.facebook.com","https://www.amazon.com","https://www.ebay.com","https://www.twitter.com"};
    private static final String[] urls = {"https://www.whatsapp.com/","https://www.youtube.com","https://www.facebook.com","https://www.amazon.com","https://www.ebay.com","https://www.twitter.com","https://www.instagram.com/?hl=fr","https://www.apple.com/fr/","https://www.pinterest.fr/","https://www.deezer.com/fr/"};
    //public static final String[] urls = {"https://www.apple.com/fr/","https://www.twitter.com","https://www.whatsapp.com/","https://www.youtube.com","https://www.facebook.com"};

    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1000;

    public static final String HTTP_ACTION = "run_job";

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, HttpsRequestJob.class, JOB_ID, work);
    }

    public void executeGet(String url) {
        URL obj;
        HttpURLConnection httpsConn = null;
        URLConnection urlConn = null;
        try {
            obj = new URL(url);
            urlConn = obj.openConnection();

            httpsConn = (HttpURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.setRequestProperty("Connection", "close");
            httpsConn.setConnectTimeout(2000);


            int responseCode = httpsConn.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url + "  Response Code : " + responseCode);

            InputStream inputStream;
            if (responseCode == 200 || responseCode == 201)
                inputStream = httpsConn.getInputStream();
            else
                inputStream = httpsConn.getErrorStream();

            int ret = 0;
            byte[] buf = new byte[512];
            while ((ret = inputStream.read(buf)) > 0) {
                //processBuf(buf);
            }

        } catch (SSLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpsConn != null)
                httpsConn.disconnect();
        }
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        for(int i = 0 ; i < 12 ; i++)
            for (String url : urls)
                executeGet(url);
    }
}
