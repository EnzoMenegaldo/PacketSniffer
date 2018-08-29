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
    private IntentFilter intentFilter ;
    private int oldBatteryLevel;


    private BatteryUsageReceiver(){
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(BatteryManager.ACTION_CHARGING);
        intentFilter.addAction(BatteryManager.ACTION_DISCHARGING);
        oldBatteryLevel = -1;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BATTERY_CHANGED:
                battery_level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                if(battery_level != oldBatteryLevel){
                    oldBatteryLevel = battery_level;
                    StrategyManager.INSTANCE.getStrategy().updateStrategy(battery_level);
                }

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

}
