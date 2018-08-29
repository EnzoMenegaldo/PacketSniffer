package com.emenegal.battery_saving.strategy;

/**
 * Strategy used when the devise is not charging
 */
public class UnPluggedResourceStrategy extends AbstractResourceStrategy {

    public UnPluggedResourceStrategy() {
        super();
    }

    /**
     * Update the storage strategy when the battery level changes
     * @param batteryLevel
     */
    public void updateStrategy(int batteryLevel){
        updateBPrecisionFieldValues(batteryLevel);
        updateIPrecisionFieldValues(batteryLevel);
        updateEPrecisionFieldValues(batteryLevel);
    }
}
