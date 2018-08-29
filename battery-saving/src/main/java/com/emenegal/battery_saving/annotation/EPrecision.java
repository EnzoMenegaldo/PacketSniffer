package com.emenegal.battery_saving.annotation;

import com.emenegal.battery_saving.enumeration.IEnum;
import com.emenegal.battery_saving.enumeration.DefaultEnum;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IndexAnnotated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EPrecision {
    Class<? extends IEnum> klass() default DefaultEnum.class;
}
