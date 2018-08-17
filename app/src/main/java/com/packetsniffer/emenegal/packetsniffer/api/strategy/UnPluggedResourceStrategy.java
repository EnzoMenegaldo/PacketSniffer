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
            updateFieldValues(true);
        if(batteryLevel < 25)
            updateFieldValues(1,false);
        if(batteryLevel < 40)
            updateFieldValues(2,false);
        if(batteryLevel < 50)
            updateFieldValues(3,false);
        if(batteryLevel < 60)
            updateFieldValues(4,false);
        if(batteryLevel < 70)
            updateFieldValues(5,false);


    }
}
