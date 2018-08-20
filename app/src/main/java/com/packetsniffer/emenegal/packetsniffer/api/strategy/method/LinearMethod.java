package com.packetsniffer.emenegal.packetsniffer.api.strategy.method;

import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.IPrecision;

public class LinearMethod implements IMethod {

    @Override
    public double execute(int batteryLevel,IPrecision annotation) {
        return (annotation.higher()-annotation.lower())*batteryLevel/100+annotation.lower();
    }

}
