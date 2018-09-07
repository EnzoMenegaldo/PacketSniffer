package com.packetsniffer.emenegal.packetsniffer.activities;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.BatteryManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.emenegal.battery_saving.annotation.BPrecision;
import com.emenegal.battery_saving.component.AnnotationList;
import com.packetsniffer.emenegal.packetsniffer.PacketSnifferService;
import com.packetsniffer.emenegal.packetsniffer.R;
import com.packetsniffer.emenegal.packetsniffer.benchmark.Benchmark;
import com.packetsniffer.emenegal.packetsniffer.database.OrmLiteDBHelper;
import com.packetsniffer.emenegal.packetsniffer.util.PhoneStateUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;


public class MainActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper> {

    private static final int REQUEST_CODE_VPN = 0;
    private static final String TAG = MainActivity.class.getSimpleName();
    private AnnotationList annotationList;

    private LocalBroadcastManager lbm;
    private static final int BENCHMARK_JOB_ID = 1000;

    private static Context context;
    private boolean benchmarkIsWorking;

    private Handler handler;
    private TextView currentTimeTextView;
    private int currentTime;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    //Listen to get the intent sent by the benchmark when it finishes
    private BroadcastReceiver benchmarkFinished = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Benchmark.BENCHMARK_FINISHED.equals(intent.getAction())) {
                benchmarkIsWorking = false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        currentTimeTextView = (TextView) findViewById(R.id.currentTime);

        handler = new Handler();

        this.annotationList = (AnnotationList) findViewById(R.id.annotation_list);

        Button btnBenchmark = (Button)findViewById(R.id.btnRunBenchmark);
        btnBenchmark.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                benchmarkIsWorking = true;
                new Thread(){
                    @Override
                    public void run() {
                        while (benchmarkIsWorking) {
                            try {
                                handler.post(() -> updateCurrentTime());
                                Thread.sleep(60000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();

                Intent mServiceIntent = new Intent();
                Benchmark.enqueueWork(getContext(), Benchmark.class, BENCHMARK_JOB_ID, mServiceIntent);
            }
        });

        //Register local receiver
        lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(benchmarkFinished, new IntentFilter(Benchmark.BENCHMARK_FINISHED));

        setupVpn();
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

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public AnnotationList getAnnotationList() {
        return annotationList;
    }


}
