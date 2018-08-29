package com.packetsniffer.emenegal.packetsniffer.api.strategy;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.BPrecision;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.EPrecision;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.IPrecision;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.ResourceStrategy;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.enumeration.IEnum;

import org.atteo.classindex.ClassIndex;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Util {

    /**
     * Retrieve all static fields which are annotated with @annotation
     * @param klass
     * @param annotation
     * @return a list containing all the fields.
     */
    public static List<Field> getAnnotatedFields(Class<?> klass, Class<? extends Annotation> annotation){
        List<Field> fields = new ArrayList<>();
        for (Field field : klass.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotation)) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    fields.add(field);
                }
            }
        }
        return fields;
    }

    /**
     * Retrieve all static fields which are annotated with @annotation in the project
     * A class containing such fields needs to be annotated with @ResourceStrategy
     * @param annotation
     * @return a list containing all the fields.
     */
    public static List<Field> getAnnotatedFields(Class<? extends Annotation> annotation){
        List<Field> fields = new ArrayList<>();
        for (Class<?> klass : ClassIndex.getAnnotated(ResourceStrategy.class))
            fields.addAll(getAnnotatedFields(klass,annotation));
        return fields;
    }

    /**
     * Update the boolean value of the field according to the value of the associated annotation.
     */
    public static void initFieldValues(List<Field> fields, Class<? extends Annotation> klass) throws ClassNotFoundException {
        if(klass.equals(BPrecision.class)) {
            for (Field field : fields) {
                try {
                    field.setBoolean(field.getClass(), field.getAnnotation(BPrecision.class).value());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }else if(klass.equals(IPrecision.class)){
            for(Field field : fields) {
                try {
                    field.setDouble(field.getClass(), field.getAnnotation(IPrecision.class).lower());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }else if(klass.equals(EPrecision.class)){
            for(Field field : fields) {
                try {
                    field.set(field.getClass(), IEnum.getLowerValue(field.getAnnotation(EPrecision.class).klass()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }else
            throw new ClassNotFoundException();
    }


    /**
     * Return true if the phone is currently charging else return false
     * @param context
     * @return
     */
    public static boolean isCharging(Context context){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return status == BatteryManager.BATTERY_STATUS_CHARGING ;
    }

}
