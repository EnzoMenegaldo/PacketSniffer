package com.packetsniffer.emenegal.packetsniffer.strategy;

import com.packetsniffer.emenegal.packetsniffer.api.strategy.annotation.Precision;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.AbstractResourceStrategy;
import com.packetsniffer.emenegal.packetsniffer.api.strategy.Util;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class AbstractResourceStrategyTest {

    @Precision(value = true, priority = 5)
    public static boolean bool1;

    @Precision(value = true,priority = 2)
    public static boolean bool2;

    @Precision(value = false,priority = 2)
    public static boolean bool3;

    AbstractResourceStrategy strategy;

    @Before
    public void init(){
        strategy = new AbstractResourceStrategy() {
            @Override
            public void updateStrategy(int batteryLevel) {

            }
        };
        List<Field> annotations = Util.getAnnotatedFields(this.getClass());
        Util.initFieldValues(annotations);
        strategy.setFields(annotations);
    }

    @Test
    public void updateFieldValues() {
        strategy.updateFieldValues(false);
        assertFalse(bool1);
        assertFalse(bool2);
        assertFalse(bool3);
    }

    @Test
    public void updateFieldValuesAccordingToPriority() {
        strategy.updateFieldValues(5,false);
        assertFalse(bool1);
        assertTrue(bool2);
        assertTrue(bool3);
        strategy.updateFieldValues(1,false);
        assertFalse(bool1);
        assertFalse(bool2);
        assertFalse(bool3);
        strategy.updateFieldValues(1,true);
        assertTrue(bool1);
        assertTrue(bool2);
        assertTrue(bool3);
    }
}