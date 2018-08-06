package com.packetsniffer.emenegal.packetsniffer.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.packetsniffer.emenegal.packetsniffer.PacketSnifferService;
import com.packetsniffer.emenegal.packetsniffer.R;
import com.packetsniffer.emenegal.packetsniffer.util.PhoneStateUtil;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_VPN = 0;
    private static final String TAG = MainActivity.class.getSimpleName();

    private static Context context;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

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

        setupVpn();
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

}
