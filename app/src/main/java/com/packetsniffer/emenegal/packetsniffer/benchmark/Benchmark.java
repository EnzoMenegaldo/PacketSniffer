package com.packetsniffer.emenegal.packetsniffer.benchmark;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.emenegal.battery_saving.StrategyManager;
import com.packetsniffer.emenegal.packetsniffer.PacketSnifferService;
import com.packetsniffer.emenegal.packetsniffer.activities.MainActivity;
import com.packetsniffer.emenegal.packetsniffer.database.DBHelper;
import com.packetsniffer.emenegal.packetsniffer.util.PhoneResourcesUtil;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import static org.apache.commons.httpclient.params.HttpMethodParams.USER_AGENT;

public class Benchmark extends AsyncTask<String, Long , Void> {


    private static final String REQUEST_TAG = "TAG";
    //private static final String[] urls = {"https://www.google.com","https://fleep.io","https://intranet.inria.fr","https://developer.android.com","https://stackoverflow.com","https://github.com","https://www.google.fr","https://www.google.ca","https://www.spotify.com/fr/","https://www.instagram.com/?hl=fr","https://www.apple.com/fr/","https://www.pinterest.fr/","https://www.deezer.com/fr/","https://fr.linkedin.com/","https://www.whatsapp.com/","https://www.youtube.com","https://www.facebook.com","https://www.amazon.com","https://www.ebay.com","https://www.twitter.com"};
    //private static final String[] urls = {"https://www.google.com","https://www.instagram.com/?hl=fr","https://www.apple.com/fr/","https://www.pinterest.fr/","https://www.deezer.com/fr/","https://fr.linkedin.com/","https://www.whatsapp.com/","https://www.youtube.com","https://www.facebook.com","https://www.amazon.com"};
    private static final String[] urls = {"https://www.google.com","http://www.lefigaro.fr/"};
    private Handler handler;
    private Context context;

    private MainActivity activity;

    public Benchmark(MainActivity activity){
        this.activity = activity;
        this.handler = activity.getHandler();
        this.context = activity.getApplicationContext();
    }

    @Override
    protected Void doInBackground(String... params) {

        //StrategyManager.INSTANCE.initialize(context);
        PhoneResourcesUtil.INSTANCE.startMonitoring();

        handler.post(() -> {
            Intent intent = new Intent(context, PacketSnifferService.class);
            context.startService(intent);
        });



        publishProgress((long) 0);

        long startingDate = System.currentTimeMillis();
        long currentDate = 0 ;
        //Time in ms
        long duration = 240*60*1000;

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
            publishProgress(currentDate-startingDate);
        }

        PhoneResourcesUtil.INSTANCE.stopMonitoring();
        //StrategyManager.INSTANCE.stop(context);

        /*
        Intent intent = new Intent(PacketSnifferService.STOP_SERVICE_INTENT);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);*/
        publishProgress((long) -1);
        return null;
    }

    @Override
    protected void onProgressUpdate (Long... values){
        if(values[0] == 0)
            Toast.makeText(context,"Starting benchmark",Toast.LENGTH_LONG).show();
        else if(values[0] == -1) {
            //Toast.makeText(context, "Stopping benchmark", Toast.LENGTH_LONG).show();
            //System.exit(0);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    activity.updateCurrentLoop(-1);
                }
            });
        }else if(values[0] == -2) {
            //Toast.makeText(context, "Stopping benchmark", Toast.LENGTH_LONG).show();
            //System.exit(0);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    activity.updateCurrentLoop(-2);
                }
            });
        }
        else{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    activity.updateCurrentLoop(values[0]/1000/60);
                }
            });
        }
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

            /*BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();*/

            //print result
            //System.out.println(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
