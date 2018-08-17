package com.packetsniffer.emenegal.packetsniffer.api.strategy;


import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.BPrecision;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.IPrecision;

import java.lang.reflect.Field;
import java.util.List;


public abstract class AbstractResourceStrategy implements ICollectionStrategy{


    protected List<Field> bFields;


    protected List<Field> iFields;



    public AbstractResourceStrategy() {
        bFields = Util.getAnnotatedFields(BPrecision.class);
        iFields = Util.getAnnotatedFields(IPrecision.class);
        Util.initFieldValues(bFields,BPrecision.class);
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

    /**
     * Set the annotation associated boolean value to 'value' for all bFields with a priority higher or equals of the parameter priority
     * and set the annotation associated boolean value to !value for all the others.
     * @param priority
     * @param value
     */
    public void updateBPrecisionFieldValues(int priority, boolean value){
        for(Field field : bFields) {
            BPrecision precision = field.getAnnotation(BPrecision.class);
            try {
                if (precision.priority() >= priority)
                    field.setBoolean(field.getClass(), value);
                else
                    field.setBoolean(field.getClass(), !value);
            } catch(IllegalAccessException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Set the boolean value of the field
     * @param value
     */
    public void updateBPrecisionFieldValues(boolean value){
        for(Field field : bFields) {
            try {
                field.setBoolean(field.getClass(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set the interger value of the field
     * @param value
     */
    public void updateIPrecisionFieldValues(int value){
        for(Field field : iFields) {
            try {
                field.set(field.getClass(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
