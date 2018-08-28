package com.packetsniffer.emenegal.packetsniffer.benchmark;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.BatteryUsageReceiver;
import com.packetsniffer.emenegal.packetsniffer.PacketSnifferService;
import com.packetsniffer.emenegal.packetsniffer.activities.MainActivity;
import com.packetsniffer.emenegal.packetsniffer.util.PhoneResourcesUtil;

public class Benchmark extends AsyncTask<String, Integer , Void> {


    private static RequestQueue queue = Volley.newRequestQueue(MainActivity.getContext());
    private static final String[] urls = {"https://www.google.com","https://www.ecosia.org/","https://fleep.io/","https://github.com/","https://start.fedoraproject.org/","http://www.insa-toulouse.fr/fr/index.html","http://www.insa-toulouse.fr/fr/index.html","https://www.irisa.fr/","https://www.inria.fr/","https://www.insa-rennes.fr/","http://www.topito.com/","https://www.google.fr","https://www.google.it","https://www.google.uk","https://www.google.ca","https://www.accuweather.com/fr/fr/france-weather","https://www.deezer.com/fr/","https://www.spotify.com/fr/","https://www.whatsapp.com/","https://www.youtube.com","https://www.facebook.com","https://www.amazon.com","https://www.ebay.com","https://www.twitter.com","https://www.netflix.com","https://www.bing.com","https://www.msn.com/fr-fr/","https://www.microsoft.com/fr-fr/","https://www.linkedin.com/","https://www.lemonde.fr/","https://www.instagram.com/","https://www.twitch.tv/","https://www.netflix.com/fr-en/"};
    private Handler handler;
    private Context context;

    public Benchmark(Handler handler, Context context){
        this.handler = handler;
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, PacketSnifferService.class);
                context.startService(intent);
            }
        });

        //  PhoneResourcesUtil.INSTANCE.startCpuMonitoring();
        BatteryUsageReceiver batteryUsageReceiver = BatteryUsageReceiver.INSTANCE;
        context.registerReceiver(batteryUsageReceiver,batteryUsageReceiver.getIntentFilter());
        publishProgress(0);
        int k = 0 ;
        for(int i = 0 ; i < 100; i++) {
            for (String url : urls) {
                sendRequest(new StringRequest(Request.Method.GET, url,null, null));
                k++;
                if(k == 10){
                    try {
                        k = 0 ;
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println(i);
        }


        //PhoneResourcesUtil.INSTANCE.stopCpuMonitoring();
        context.unregisterReceiver(batteryUsageReceiver);
        BatteryUsageReceiver.INSTANCE.closeLogFile();
        Intent intent = new Intent(PacketSnifferService.STOP_SERVICE_INTENT);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        publishProgress(1);
        return null;
    }

    @Override
    protected void onProgressUpdate (Integer... values){
        if(values[0] == 0)
            Toast.makeText(context,"Starting benchmark",Toast.LENGTH_LONG).show();
        else {
            //Toast.makeText(context, "Stopping benchmark", Toast.LENGTH_LONG).show();
            System.exit(0);
        }
    }

    /**
     * Add the request to the RequestQueue.
     */
    public void sendRequest(StringRequest request){
        queue.add(request);
    }



}
