package com.packetsniffer.emenegal.packetsniffer.api.strategy;

import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.Precision;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.ResourceStrategy;

import org.atteo.classindex.ClassIndex;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Util {

    /**
     * Retrieve all static fields which are annotated with @Precision
     * @param klass
     * @return a list containing all the fields.
     */
    public static List<Field> getAnnotatedFields(Class<?> klass){
        List<Field> fields = new ArrayList<>();
        for (Field field : klass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Precision.class)) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    fields.add(field);
                }
            }
        }
        return fields;
    }

    /**
     * Retrieve all static fields which are annotated with @Precision in the project
     * A class containing such fields needs to be annotated with @ResourceStrategy
     * @return a list containing all the fields.
     */
    public static List<Field> getAnnotatedFields(){
        List<Field> fields = new ArrayList<>();
        for (Class<?> klass : ClassIndex.getAnnotated(ResourceStrategy.class))
            fields.addAll(getAnnotatedFields(klass));
        return fields;
    }


    /**
     * Update the boolean value of the field according to the value of the associated annotation.
     * @param fields
     */
    public static void initFieldValues(List<Field> fields){
        for(Field field : fields) {
            try {
                field.setBoolean(field.getClass(), field.getAnnotation(Precision.class).value());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
