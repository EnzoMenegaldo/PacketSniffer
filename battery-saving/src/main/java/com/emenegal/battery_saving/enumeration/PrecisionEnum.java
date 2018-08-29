package com.emenegal.battery_saving.enumeration;

public enum PrecisionEnum implements IEnum {
    SaveEnergy(0),FewPower(25),HalfPower(50),HighPower(75),FullPower(90);

    int batteryLevel;

    PrecisionEnum(int batteryLevel){
        this.batteryLevel = batteryLevel;
    }

    @Override
    public int getBatteryLevel(){
        return batteryLevel;
    }
}
