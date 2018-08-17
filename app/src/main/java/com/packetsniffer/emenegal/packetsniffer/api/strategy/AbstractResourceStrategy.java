package com.packetsniffer.emenegal.packetsniffer.api.strategy;

import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.Precision;

import java.lang.reflect.Field;
import java.util.List;


public abstract class AbstractResourceStrategy implements ICollectionStrategy{


    protected List<Field> fields;


    public AbstractResourceStrategy() {
        fields = Util.getAnnotatedFields();
        Util.initFieldValues(fields);
    }

    public abstract void updateStrategy(int batteryLevel);


    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields){
        this.fields = fields;
    }

    /**
     * Set the annotation associated boolean value to 'value' for all fields with a priority higher or equals of the parameter priority
     * and set the annotation associated boolean value to !value for all the others.
     * @param priority
     * @param value
     */
    public void updateFieldValues(int priority, boolean value){
        for(Field field : fields) {
            Precision precision = field.getAnnotation(Precision.class);
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
    public void updateFieldValues(boolean value){
        for(Field field : fields) {
            try {
                field.setBoolean(field.getClass(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
