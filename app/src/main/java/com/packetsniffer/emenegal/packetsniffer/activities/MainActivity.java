package com.packetsniffer.emenegal.packetsniffer.activities;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.packetsniffer.emenegal.packetsniffer.R;

public class MainActivity extends Activity {

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
    }

    public static Context getContext(){ return context; }
}
