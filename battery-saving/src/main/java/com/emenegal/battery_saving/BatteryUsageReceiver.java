package com.emenegal.battery_saving;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.emenegal.battery_saving.strategy.PluggedResourceStrategy;
import com.emenegal.battery_saving.strategy.UnPluggedResourceStrategy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class BatteryUsageReceiver extends BroadcastReceiver{

    public static final BatteryUsageReceiver INSTANCE = new BatteryUsageReceiver();
    public static int battery_level = 0;
    private static BufferedWriter bw;
    private IntentFilter intentFilter ;
    private static final String fileName = "battery_log.txt";
    private final long startingTime = new Date().getTime();

    private BatteryUsageReceiver(){ }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BATTERY_CHANGED:
                battery_level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                try {
                    bw.write(((new Date()).getTime()-startingTime)/1000+","+battery_level+"\n");
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                StrategyManager.INSTANCE.getStrategy().updateStrategy(battery_level);
                break;
            case BatteryManager.ACTION_CHARGING:
                StrategyManager.INSTANCE.setStrategy(new PluggedResourceStrategy());
                break;
            case BatteryManager.ACTION_DISCHARGING:
                StrategyManager.INSTANCE.setStrategy(new UnPluggedResourceStrategy());
                break;
            default:
                break;
        }
    }

    public IntentFilter getIntentFilter() {
        return intentFilter;
    }

    public void setIntentFilter(IntentFilter intentFilter) {
        this.intentFilter = intentFilter;
    }

    public void initialize(Context context){
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(BatteryManager.ACTION_CHARGING);
        intentFilter.addAction(BatteryManager.ACTION_DISCHARGING);


        try {
            File file = new File(context.getExternalFilesDir("")+ File.separator +fileName+"_"+(new Date()).getTime());
            if(!file.exists())
                file.createNewFile();
            bw = new BufferedWriter(new FileWriter(file,true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeLogFile(){
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
