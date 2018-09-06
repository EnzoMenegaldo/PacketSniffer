package com.packetsniffer.emenegal.packetsniffer.activities;

import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
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
import com.packetsniffer.emenegal.packetsniffer.PacketSnifferService;
import com.packetsniffer.emenegal.packetsniffer.R;
import com.packetsniffer.emenegal.packetsniffer.benchmark.Benchmark;
import com.packetsniffer.emenegal.packetsniffer.database.OrmLiteDBHelper;
import com.packetsniffer.emenegal.packetsniffer.util.PhoneStateUtil;

import java.lang.reflect.Field;
import java.util.List;


public class MainActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper> {

    private static final int REQUEST_CODE_VPN = 0;
    private static final String TAG = MainActivity.class.getSimpleName();

    private static Context context;

    private Handler handler;
    private TextView currentLoop;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        currentLoop = (TextView) findViewById(R.id.currentLoop);

        Switch vpn_switch = (Switch)findViewById(R.id.switch_vpn);
        vpn_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Intent intent = new Intent(getApplicationContext(), PacketSnifferService.class);
                    startService(intent);
                }else{
                    //Send an intent to tell the service to stop
                    Intent intent = new Intent(PacketSnifferService.STOP_SERVICE_INTENT);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
        });

        handler = new Handler();

        Button btnBenchmark = (Button)findViewById(R.id.btnRunBenchmark);
        btnBenchmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Benchmark(MainActivity.this).execute();
            }
        });

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

    public void updateCurrentLoop(long i){
        currentLoop.setText("current loop : "+i);
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

}
