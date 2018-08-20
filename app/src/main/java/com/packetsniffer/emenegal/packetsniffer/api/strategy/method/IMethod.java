package com.packetsniffer.emenegal.packetsniffer.api.strategy.method;

import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.IPrecision;

public interface IMethod {

    double execute(int batteryLevel, IPrecision annotation);
}
