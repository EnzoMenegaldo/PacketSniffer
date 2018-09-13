package com.packetsniffer.emenegal.packetsniffer.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.emenegal.battery_saving.component.AnnotationList;
import com.packetsniffer.emenegal.packetsniffer.PacketSnifferService;
import com.packetsniffer.emenegal.packetsniffer.R;
import com.packetsniffer.emenegal.packetsniffer.benchmark.Benchmark;
import com.packetsniffer.emenegal.packetsniffer.database.OrmLiteDBHelper;
import com.packetsniffer.emenegal.packetsniffer.util.PhoneStateUtil;

public class MainActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper> {

    public static final String CHANNEL_ID = "101";
    private static final int REQUEST_CODE_VPN = 0;
    private static final String TAG = MainActivity.class.getSimpleName();
    private AnnotationList annotationList;
    private static int requestCounter = 0;

    private LocalBroadcastManager lbm;

    private static Context context;

    private Handler handler;
    private TextView currentTimeTextView;
    private TextView requestCounterTextView;
    private int currentTime;

    public static RequestQueue queue;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    //Listen to get the intent sent by the benchmark when it finishes
    private BroadcastReceiver benchmarkFinished = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Benchmark.BENCHMARK_FINISHED.equals(intent.getAction())) {
                Log.i(TAG,"Benchmark is finished");
                System.exit(0);
            }else if(Benchmark.BENCHMARK_RUNNING.equals(intent.getAction())){
                updateRequestCounter(intent.getIntExtra("request",0));
            }else if(Benchmark.BENCHMARK_UPDATE_TIMER.equals(intent.getAction())){
                updateCurrentTime();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        queue = Volley.newRequestQueue(this);
        currentTimeTextView = (TextView) findViewById(R.id.currentTime);
        requestCounterTextView = (TextView) findViewById(R.id.requestCounter);

        handler = new Handler();

        this.annotationList = (AnnotationList) findViewById(R.id.annotation_list);

        Switch btnBenchmark = (Switch) findViewById(R.id.switchRunBenchmark);
        btnBenchmark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                Log.d(TAG,"start VPN");
                Intent start_vpn_intent = new Intent(getApplicationContext(), PacketSnifferService.class);
                startService(start_vpn_intent);

                //StrategyManager.INSTANCE.initialize(context,activity.getAnnotationList());

                Log.d(TAG,"start benchmark");
                Intent start_benchmark_intent = new Intent(getApplicationContext(),Benchmark.class);
                startService(start_benchmark_intent);


            }else {
                //Send intent to stop the vpn service
                Intent stop_vpn_intent = new Intent(PacketSnifferService.STOP_SERVICE_INTENT);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(stop_vpn_intent);

                // StrategyManager.INSTANCE.stop(context);
            }
        });

        //Register local receiver
        lbm = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Benchmark.BENCHMARK_FINISHED);
        intentFilter.addAction(Benchmark.BENCHMARK_RUNNING);
        intentFilter.addAction(Benchmark.BENCHMARK_UPDATE_TIMER);
        lbm.registerReceiver(benchmarkFinished, new IntentFilter(intentFilter));

        setupVpn();
        createNotificationChannel();
    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public static Context getContext(){ return context; }

    private void setupVpn() {
        // check for VPN already running
        try {
            if (!PhoneStateUtil.INSTANCE.checkForActiveInterface(getString(R.string.vpn_interface))) {
                // get user permission for VPN
                Intent intent = VpnService.prepare(this);
                if (intent != null) {
                    // ask user for VPN permission
                    startActivityForResult(intent, 0);
                } else {
                    // already have VPN permission
                    onActivityResult(REQUEST_CODE_VPN, RESULT_OK, null);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception checking network interfaces :" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateCurrentTime(){
        currentTime++;
        currentTimeTextView.setText("current time    : "+currentTime+" min");
    }

    public void updateRequestCounter(int i){
        requestCounter += i;
        requestCounterTextView.setText("requests    : "+requestCounter);
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public AnnotationList getAnnotationList() {
        return annotationList;
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
