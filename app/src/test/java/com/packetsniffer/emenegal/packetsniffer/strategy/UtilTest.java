package com.packetsniffer.emenegal.packetsniffer.strategy;

import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.BPrecision;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.Util;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.IPrecision;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class UtilTest {

    @BPrecision(true)
    public static boolean aBoolean;

    @BPrecision(true)
    public static boolean anotherBoolean;

    @BPrecision(true)
    public boolean nonStaticBoolean;

    @IPrecision(lower = 10,higher = 100)
    public static double aInterval;


    @Test
    public void getAnnotatedFields() {
        List<Field> bAnnotations = Util.getAnnotatedFields(this.getClass(),BPrecision.class);
        assertEquals(bAnnotations.size(),2);
        List<Field> iAnnotations = Util.getAnnotatedFields(this.getClass(),IPrecision.class);
        assertEquals(iAnnotations.size(),1);
    }


    @Test
    public void initBPrecisionFieldValues() {
        List<Field> bAnnotations = Util.getAnnotatedFields(this.getClass(),BPrecision.class);
        assertFalse(aBoolean);
        Util.initFieldValues(bAnnotations,BPrecision.class);
        assertTrue(aBoolean);
    }

    @Test
    public void initIPrecisionFieldValues() {
        List<Field> iAnnotations = Util.getAnnotatedFields(this.getClass(),IPrecision.class);
        Util.initFieldValues(iAnnotations,IPrecision.class);
        assertEquals(aInterval,10,0);
    }
}