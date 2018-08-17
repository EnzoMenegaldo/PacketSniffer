package com.packetsniffer.emenegal.packetsniffer.strategy;

import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.BPrecision;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.AbstractResourceStrategy;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.Util;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.IPrecision;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class AbstractResourceStrategyTest {

    @BPrecision(value = true, priority = 5)
    public static boolean bool1;

    @BPrecision(value = true,priority = 2)
    public static boolean bool2;

    @BPrecision(value = false,priority = 2)
    public static boolean bool3;

    @IPrecision(lower = 150 ,higher = 400)
    public static int int1;

    @IPrecision(lower = 150 ,higher = 400)
    public static int int2;

    AbstractResourceStrategy strategy;

    @Before
    public void init(){
        strategy = new AbstractResourceStrategy() {
            @Override
            public void updateStrategy(int batteryLevel) {

            }
        };
        List<Field> bAnnotations = Util.getAnnotatedFields(this.getClass(),BPrecision.class);
        List<Field> iAnnotations = Util.getAnnotatedFields(this.getClass(),IPrecision.class);
        Util.initFieldValues(bAnnotations,BPrecision.class);
        Util.initFieldValues(iAnnotations,IPrecision.class);
        strategy.setbFields(bAnnotations);
        strategy.setiFields(iAnnotations);
    }

    @Test
    public void updateBPrecisionFieldValues() {
        strategy.updateBPrecisionFieldValues(false);
        assertFalse(bool1);
        assertFalse(bool2);
        assertFalse(bool3);
    }

    @Test
    public void updateBPrecisionFieldValuesAccordingToPriority() {
        strategy.updateBPrecisionFieldValues(5,false);
        assertFalse(bool1);
        assertTrue(bool2);
        assertTrue(bool3);
        strategy.updateBPrecisionFieldValues(1,false);
        assertFalse(bool1);
        assertFalse(bool2);
        assertFalse(bool3);
        strategy.updateBPrecisionFieldValues(1,true);
        assertTrue(bool1);
        assertTrue(bool2);
        assertTrue(bool3);
    }

    @Test
    public void updateIPrecisionFieldValues() {
        strategy.updateIPrecisionFieldValues(666);
        assertEquals(int1,666);
        assertEquals(int2,666);
    }
}