package com.emenegal.battery_saving.method;

import com.emenegal.battery_saving.annotation.IPrecision;

public interface IMethod {

    double execute(int batteryLevel, IPrecision annotation);
}
