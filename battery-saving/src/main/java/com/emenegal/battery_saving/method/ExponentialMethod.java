package com.emenegal.battery_saving.method;

import com.emenegal.battery_saving.annotation.IPrecision;

public class ExponentialMethod implements IMethod {

    @Override
    public double execute(int batteryLevel, IPrecision annotation) {
        return Math.pow(batteryLevel,annotation.params()[0]);
    }
}
