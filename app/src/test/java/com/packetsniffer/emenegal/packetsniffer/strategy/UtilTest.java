package com.packetsniffer.emenegal.packetsniffer.strategy;

import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.Precision;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.Util;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class UtilTest {

    @Precision(true)
    public static boolean aBoolean;

    @Precision(true)
    public static boolean anotherBoolean;

    @Precision(true)
    public boolean nonStaticBoolean;


    @Test
    public void getAnnotatedFields() {
        List<Field> annotations = Util.getAnnotatedFields(this.getClass());
        assertEquals(annotations.size(),2);
    }


    @Test
    public void initFieldValues() {
        List<Field> annotations = Util.getAnnotatedFields(this.getClass());
        assertFalse(aBoolean);
        Util.initFieldValues(annotations);
        assertTrue(aBoolean);
    }
}