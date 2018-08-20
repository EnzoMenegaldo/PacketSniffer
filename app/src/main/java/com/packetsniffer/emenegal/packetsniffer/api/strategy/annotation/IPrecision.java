package com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation;

import com.packetsniffer.emenegal.packetsniffer.api.strategy.method.IMethod;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.method.LinearMethod;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IndexAnnotated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IPrecision {
    Class<? extends IMethod> method() default LinearMethod.class;
    double lower() default 1;
    double higher() default 1;
    double[] params() default 0;
}
