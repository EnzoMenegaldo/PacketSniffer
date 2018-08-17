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

public class Benchmark extends AsyncTask<String, Integer , Void> {


    private static RequestQueue queue = Volley.newRequestQueue(MainActivity.getContext());
    private static final String[] urls = {"https://www.google.com","https://www.youtube.com","https://www.facebook.com","https://www.amazon.com","https://www.reddit.com","https://www.ebay.com","https://www.twitter.com","https://www.netflix.com"};
    private Handler handler;
    private Context context;

    public Benchmark(Handler handler, Context context){
        this.handler = handler;
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        //PhoneResourcesUtil.INSTANCE.startCpuMonitoring();
        BatteryUsageReceiver batteryUsageReceiver = BatteryUsageReceiver.INSTANCE;
        context.registerReceiver(batteryUsageReceiver,batteryUsageReceiver.getIntentFilter());
        publishProgress(0);
        for(int i = 0 ; i < 500; i++) {
            for (String url : urls) {
                sendRequest(new StringRequest(Request.Method.GET, url,null, null));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(i);
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, PacketSnifferService.class);
                context.startService(intent);
            }
        });
        for(int i = 0 ; i < 500; i++) {
            for (String url : urls) {
                sendRequest(new StringRequest(Request.Method.GET, url,null, null));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(i);
        }
       // PhoneResourcesUtil.INSTANCE.stopCpuMonitoring();
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
