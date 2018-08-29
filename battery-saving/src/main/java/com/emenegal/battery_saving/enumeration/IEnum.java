package com.emenegal.battery_saving.enumeration;

public interface IEnum {

    int getBatteryLevel();

    /**
     * Return the appropriate IEnum from the class "klass" according to the current battery level
     * @param batteryLevel
     * @param klass
     * @return
     */
    static IEnum getIEnum(int batteryLevel, Class<? extends IEnum> klass){
        IEnum[] constants = klass.getEnumConstants();
        if(constants.length > 0) {
            IEnum maxConst = constants[0];

            for (IEnum constant : constants) {
                if (batteryLevel >= constant.getBatteryLevel() && constant.getBatteryLevel() >= maxConst.getBatteryLevel())
                    maxConst = constant;
            }
            return maxConst;
        }else{
            return null;
        }
    }

    static IEnum getLowerValue(Class<? extends IEnum> klass){
        IEnum[] constants = klass.getEnumConstants();
        if(constants.length > 0) {
            IEnum minConst = constants[0];

            for (IEnum constant : constants) {
                if (constant.getBatteryLevel() <= minConst.getBatteryLevel())
                    minConst = constant;
            }
            return minConst;
        }else{
            return null;
        }
    }
}
