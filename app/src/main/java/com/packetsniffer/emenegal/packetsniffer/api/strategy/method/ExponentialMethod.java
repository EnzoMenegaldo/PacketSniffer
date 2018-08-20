package com.packetsniffer.emenegal.packetsniffer.api.strategy.method;

import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.IPrecision;

public class ExponentialMethod implements IMethod {

    @Override
    public double execute(int batteryLevel, IPrecision annotation) {
        return Math.pow(batteryLevel,annotation.params()[0]);
    }
}
