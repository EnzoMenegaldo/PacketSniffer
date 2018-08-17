package com.packetsniffer.emenegal.packetsniffer.api.strategy;

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
        if(batteryLevel > 80 )
            updateBPrecisionFieldValues(true);
        if(batteryLevel < 25)
            this.updateBPrecisionFieldValues(1,false);
        if(batteryLevel < 40)
            this.updateBPrecisionFieldValues(2,false);
        if(batteryLevel < 50)
            this.updateBPrecisionFieldValues(3,false);
        if(batteryLevel < 60)
            this.updateBPrecisionFieldValues(4,false);
        if(batteryLevel < 70)
            this.updateBPrecisionFieldValues(5,false);


    }
}
