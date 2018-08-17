package com.packetsniffer.emenegal.packetsniffer.api.strategy;

import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.BPrecision;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.IPrecision;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.ResourceStrategy;

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
     * @param fields
     */
    public static void initFieldValues(List<Field> fields, Class<? extends Annotation> annotation ){
        if(annotation.equals(BPrecision.class)){
            for(Field field : fields) {
                try {
                    field.setBoolean(field.getClass(), field.getAnnotation(BPrecision.class).value());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }else{
            for(Field field : fields) {
                try {
                    field.setInt(field.getClass(), field.getAnnotation(IPrecision.class).lower());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
