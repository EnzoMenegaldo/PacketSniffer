package com.packetsniffer.emenegal.packetsniffer.api.strategy.strategy;


import com.packetsniffer.emenegal.packetsniffer.api.strategy.Util;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.BPrecision;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.EPrecision;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.IPrecision;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.enumeration.IEnum;

import java.lang.reflect.Field;
import java.util.List;


public abstract class AbstractResourceStrategy implements ICollectionStrategy {


    protected List<Field> bFields;


    protected List<Field> iFields;


    protected List<Field> eFields;



    public AbstractResourceStrategy() {
        bFields = Util.getAnnotatedFields(BPrecision.class);
        iFields = Util.getAnnotatedFields(IPrecision.class);
        eFields = Util.getAnnotatedFields(EPrecision.class);

        try {
            Util.initFieldValues(bFields,BPrecision.class);
            Util.initFieldValues(iFields,IPrecision.class);
            Util.initFieldValues(eFields,EPrecision.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public abstract void updateStrategy(int batteryLevel);


    public List<Field> getbFields() {
        return bFields;
    }

    public void setbFields(List<Field> bFields){
        this.bFields = bFields;
    }

    public List<Field> getiFields() {
        return iFields;
    }

    public void setiFields(List<Field> iFields) {
        this.iFields = iFields;
    }

    public List<Field> geteFields() {
        return eFields;
    }

    public void seteFields(List<Field> eFields) {
        this.eFields = eFields;
    }

    /**
     * For all bFields with a threshold higher or equals of the parameter threshold, set their value to the contrary of the value used when we get enough resources.
     * For the others, set the value to the one used when the app get enough resources
     * @param batteryLevel
     */
    public void updateBPrecisionFieldValues(int batteryLevel){
        for(Field field : bFields) {
            BPrecision precision = field.getAnnotation(BPrecision.class);
            try {
                if (precision.threshold() >= batteryLevel)
                    field.setBoolean(field.getClass(), !precision.value());
                else
                    field.setBoolean(field.getClass(), precision.value());
            } catch(IllegalAccessException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Set the boolean to the value of initialisation at full resources
     */
    public void updateBPrecisionFieldValues(){
        for(Field field : bFields) {
            try {
                BPrecision precision = field.getAnnotation(BPrecision.class);
                field.setBoolean(field.getClass(), precision.value());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set the integer value of the field according to its min and max values and the battery level
     * So far, each value is calculated using a linear expression
     * @param batteryLevel
     */
    public void updateIPrecisionFieldValues(int batteryLevel){
        for(Field field : iFields) {
            try {
                IPrecision precision = field.getAnnotation(IPrecision.class);
                double value = precision.method().newInstance().execute(batteryLevel,precision);
                field.set(field.getClass(), value);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set the field value to the appropriate enum according to the current battery level
     * @param batteryLevel
     */
    public void updateEPrecisionFieldValues(int batteryLevel){
        for(Field field : eFields) {
            try {
                EPrecision precision = field.getAnnotation(EPrecision.class);
                field.set(field.getClass(), IEnum.getIEnum(batteryLevel,precision.klass()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


}
