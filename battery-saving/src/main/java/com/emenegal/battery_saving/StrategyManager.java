package com.emenegal.battery_saving;


import android.content.Context;

import com.emenegal.battery_saving.strategy.ICollectionStrategy;
import com.emenegal.battery_saving.strategy.PluggedResourceStrategy;
import com.emenegal.battery_saving.strategy.UnPluggedResourceStrategy;

public class StrategyManager {
    public static final String TAG = StrategyManager.class.getSimpleName();

    public static final StrategyManager INSTANCE = new StrategyManager();

    private ICollectionStrategy strategy;

    public void setStrategy(ICollectionStrategy strategy) {
        this.strategy = strategy;
    }

    public ICollectionStrategy getStrategy() {
        return strategy;
    }

    /**
     * Set the strategy according to the current charging state.
     * Then register the broadcast receiver to gather the battery state.
     * @param context
     */
    public void initialize(Context context){
       if(Util.isCharging(context))
            strategy = new PluggedResourceStrategy();
        else
            strategy = new UnPluggedResourceStrategy();

        context.registerReceiver(BatteryUsageReceiver.INSTANCE,BatteryUsageReceiver.INSTANCE.getIntentFilter());
    }

    /**
     * Unregister the broadcast receiver and close the battery log file.
     * @param context
     */
    public void stop(Context context){
        context.unregisterReceiver(BatteryUsageReceiver.INSTANCE);
    }

}