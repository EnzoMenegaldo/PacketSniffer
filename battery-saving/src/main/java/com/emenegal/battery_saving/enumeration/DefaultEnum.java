package com.emenegal.battery_saving.enumeration;

public enum  DefaultEnum implements IEnum {

    Small(0),Medium(50),Large(80);

    int batteryLevel;

    DefaultEnum(int batteryLevel){
        this.batteryLevel = batteryLevel;
    }

    @Override
    public int getBatteryLevel(){
        return batteryLevel;
    }
}
