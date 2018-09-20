package com.packetsniffer.emenegal.packetsniffer.activities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import com.emenegal.battery_saving.StrategyManager;
import com.emenegal.battery_saving.component.AnnotationList;
import com.packetsniffer.emenegal.packetsniffer.PacketSnifferService;
import com.packetsniffer.emenegal.packetsniffer.R;
import com.packetsniffer.emenegal.packetsniffer.benchmark.HttpsRequestJob;
import com.packetsniffer.emenegal.packetsniffer.database.OrmLiteDBHelper;
import com.packetsniffer.emenegal.packetsniffer.util.PhoneStateUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;

public class MainActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper> {


    final static long startingTime = System.currentTimeMillis();
    public static final String CHANNEL_ID = "101";
    private static final int REQUEST_CODE_VPN = 0;
    private static final String TAG = MainActivity.class.getSimpleName();
    private AnnotationList annotationList;

    private static Context context;

    private Handler handler;
    private TextView currentTimeTextView;
    private TextView requestCounterTextView;
    private int counter;
    private Timer requestTimer;

    private static final String fileName = "battery_log";
    private File batteryLevelFile;
    private BufferedWriter bufferedWriter;


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        currentTimeTextView = (TextView) findViewById(R.id.currentTime);
        requestCounterTextView = (TextView) findViewById(R.id.requestCounter);

        try {
            batteryLevelFile = new File(MainActivity.getContext().getExternalFilesDir("")+ File.separator +fileName+"_"+ OrmLiteActionBarActivity.TIME+".txt");
            if(!batteryLevelFile.exists())
                batteryLevelFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        handler = new Handler();

        this.annotationList = (AnnotationList) findViewById(R.id.annotation_list);

        Switch btnBenchmark = (Switch) findViewById(R.id.switchRunBenchmark);
        btnBenchmark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                Log.d(TAG,"start VPN");
                Intent start_vpn_intent = new Intent(getApplicationContext(), PacketSnifferService.class);
                startService(start_vpn_intent);

                scheduleStopVPN();

                StrategyManager.INSTANCE.initialize(context,getAnnotationList());


                scheduleHttpAlert();



            }else {
                //Send intent to stop the vpn service
                Intent stop_vpn_intent = new Intent(PacketSnifferService.STOP_SERVICE_INTENT);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(stop_vpn_intent);
                StrategyManager.INSTANCE.stop(context);
            }
        });

        setupVpn();
        createNotificationChannel();
    }

    protected void onResume() {
        super.onResume();
        updateRequestCounter();
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


    public void updateRequestCounter(){
        requestCounterTextView.setText("requests    : "+counter);
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

    public void scheduleStopVPN() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000*5+5000,"ALARM__VPN_STOP_MANAGER", new AlarmManager.OnAlarmListener(){
            @Override
            public void onAlarm() {
                Intent stop_vpn_intent = new Intent(PacketSnifferService.STOP_SERVICE_INTENT);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(stop_vpn_intent);

                scheduleStartVPN();
            }
        },null);
    }

    public void scheduleStartVPN() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 30000,"ALARM__VPN_START_MANAGER", new AlarmManager.OnAlarmListener(){
            @Override
            public void onAlarm() {
                Log.d(TAG,"restart VPN");
                Intent start_vpn_intent = new Intent(getApplicationContext(), PacketSnifferService.class);
                startService(start_vpn_intent);

                scheduleStopVPN();
            }
        },null);
    }

    public void scheduleHttpAlert() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000,"ALARM_HTTP_MANAGER", new AlarmManager.OnAlarmListener(){
            @Override
            public void onAlarm() {
                counter++;
                scheduleHttpAlert();
                saveBatterylevel();
                HttpsRequestJob.enqueueWork(context,new Intent(HttpsRequestJob.HTTP_ACTION));
            }
        },null);
    }

    public void saveBatterylevel(){
        BufferedWriter bw  = null;
        try {
            bw = new BufferedWriter(new FileWriter(batteryLevelFile,true));
        } catch (IOException e) {
            e.printStackTrace();
        }

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = MainActivity.getContext().registerReceiver(null, ifilter);
        int battery_level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

        try {
            System.out.println("TIME : "+(System.currentTimeMillis()-startingTime)+"  BATTERY LEVEL : "+battery_level);
            bw.write((System.currentTimeMillis()-startingTime) /1000+","+battery_level+"\n");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
