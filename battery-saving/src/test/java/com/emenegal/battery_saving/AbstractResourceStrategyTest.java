package com.emenegal.battery_saving;

import com.emenegal.battery_saving.enumeration.IEnum;
import com.emenegal.battery_saving.enumeration.PrecisionEnum;
import com.emenegal.battery_saving.annotation.BPrecision;
import com.emenegal.battery_saving.annotation.EPrecision;
import com.emenegal.battery_saving.method.LogarithmMethod;
import com.emenegal.battery_saving.strategy.AbstractResourceStrategy;
import com.emenegal.battery_saving.annotation.IPrecision;
import com.emenegal.battery_saving.method.ExponentialMethod;
import com.emenegal.battery_saving.method.LinearMethod;
import com.emenegal.battery_saving.enumeration.DefaultEnum;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class AbstractResourceStrategyTest {

    @BPrecision(value = true, threshold = 50)
    public static boolean bool1;

    @BPrecision(value = true, threshold = 75)
    public static boolean bool2;

    @BPrecision(value = false, threshold = 25)
    public static boolean bool3;

    @IPrecision(lower = 150 ,higher = 400, method = ExponentialMethod.class, params = {2})
    public static double int1;

    @IPrecision(lower = 350 ,higher = 4000,method = LinearMethod.class)
    public static double int2;

    @IPrecision(lower = 10 ,higher = 70 ,method = LogarithmMethod.class)
    public static double int3;

    @EPrecision(klass = PrecisionEnum.class)
    public static IEnum precisionEnum;

    @EPrecision(klass = DefaultEnum.class)
    public static IEnum defaultEnum;


    private AbstractResourceStrategy strategy;

    @Before
    public void init(){
        strategy = new AbstractResourceStrategy() {
            @Override
            public void updateStrategy(int batteryLevel) {

            }
        };
        List<Field> bAnnotations = Util.getAnnotatedFields(this.getClass(),BPrecision.class);
        List<Field> iAnnotations = Util.getAnnotatedFields(this.getClass(),IPrecision.class);
        List<Field> eAnnotations = Util.getAnnotatedFields(this.getClass(),EPrecision.class);

        try {
            Util.initFieldValues(bAnnotations,BPrecision.class);
            Util.initFieldValues(iAnnotations,IPrecision.class);
            Util.initFieldValues(eAnnotations,EPrecision.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        strategy.setbFields(bAnnotations);
        strategy.setiFields(iAnnotations);
        strategy.seteFields(eAnnotations);
    }

    @Test
    public void updateBPrecisionFieldValues() {
        strategy.updateBPrecisionFieldValues();
        assertTrue(bool1);
        assertTrue(bool2);
        assertFalse(bool3);
    }

    @Test
    public void updateBPrecisionFieldValuesAccordingToBatteryLevel() {
        strategy.updateBPrecisionFieldValues(90);
        assertTrue(bool1);
        assertTrue(bool2);
        assertFalse(bool3);
        strategy.updateBPrecisionFieldValues(70);
        assertTrue(bool1);
        assertFalse(bool2);
        assertFalse(bool3);
        strategy.updateBPrecisionFieldValues(10);
        assertFalse(bool1);
        assertFalse(bool2);
        assertTrue(bool3);
    }


    @Test
    public void updateIPrecisionLinearMethod() {
        strategy.updateIPrecisionFieldValues(50);
        assertEquals(int2,2175,0);
    }

    @Test
    public void updateIPrecisionExponentialMethod() {
        strategy.updateIPrecisionFieldValues(50);
        assertEquals(int1,2500,0);
    }

    @Test
    public void updateIPrecisionLogarithmMethod() {
        strategy.updateIPrecisionFieldValues(1);
        assertEquals(int3,10,0);
        strategy.updateIPrecisionFieldValues(0);
        assertEquals(int3,0,0);
        strategy.updateIPrecisionFieldValues(50);
        assertEquals(int3,13.91,0.01);
    }

    @Test
    public void updatePrecisionEnum() {
        assertEquals(precisionEnum,PrecisionEnum.SaveEnergy);
        strategy.updateEPrecisionFieldValues(90);
        assertEquals(precisionEnum,PrecisionEnum.FullPower);
        strategy.updateEPrecisionFieldValues(80);
        assertEquals(precisionEnum,PrecisionEnum.HighPower);
        strategy.updateEPrecisionFieldValues(65);
        assertEquals(precisionEnum,PrecisionEnum.HalfPower);
        strategy.updateEPrecisionFieldValues(25);
        assertEquals(precisionEnum,PrecisionEnum.FewPower);
        strategy.updateEPrecisionFieldValues(1);
        assertEquals(precisionEnum,PrecisionEnum.SaveEnergy);
    }

    @Test
    public void updateDefaultEnum(){
        assertEquals(defaultEnum,DefaultEnum.Small);
        strategy.updateEPrecisionFieldValues(90);
        assertEquals(defaultEnum,DefaultEnum.Large);
        strategy.updateEPrecisionFieldValues(50);
        assertEquals(defaultEnum,DefaultEnum.Medium);
        strategy.updateEPrecisionFieldValues(1);
        assertEquals(defaultEnum,DefaultEnum.Small);
    }
}