package com.emenegal.battery_saving.method;

import com.emenegal.battery_saving.annotation.IPrecision;

public class LogarithmMethod implements IMethod {

    @Override
    public double execute(int batteryLevel, IPrecision annotation) {
        if(batteryLevel >=1)
            return Math.log(batteryLevel)+annotation.lower();
        else
            return 0;
    }
}
