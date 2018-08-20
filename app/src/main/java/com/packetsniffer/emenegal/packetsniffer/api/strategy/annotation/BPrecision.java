package com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IndexAnnotated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BPrecision{
    //The associated boolean field will take this value when the app gets enough resources according to its priority
    boolean value() default true;
    int priority() default 1;
}

