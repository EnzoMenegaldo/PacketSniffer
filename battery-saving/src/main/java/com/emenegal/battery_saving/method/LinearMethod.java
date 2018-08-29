package com.emenegal.battery_saving.method;

import com.emenegal.battery_saving.annotation.IPrecision;

public class LinearMethod implements IMethod {

    @Override
    public double execute(int batteryLevel,IPrecision annotation) {
        return (annotation.higher()-annotation.lower())*batteryLevel/100+annotation.lower();
    }

}
