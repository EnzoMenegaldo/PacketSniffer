package com.packetsniffer.emenegal.packetsniffer.api.strategy.method;

import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.IPrecision;

public class LogarithmMethod implements IMethod {

    @Override
    public double execute(int batteryLevel, IPrecision annotation) {
        if(batteryLevel >=1)
            return Math.log(batteryLevel)+annotation.lower();
        else
            return 0;
    }
}
