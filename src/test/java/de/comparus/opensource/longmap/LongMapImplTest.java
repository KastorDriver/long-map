package de.comparus.opensource.longmap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LongMapImplTest {

    private LongMapImpl<String> longMap;

    @Before
    public void before() {
        longMap = new LongMapImpl<>();
    }

    @Test
    public void whenCreateWithoutArgumentsThenCreateWithDefaultParams() {
        assertEquals(16, longMap.table.length);
        assertEquals(0.75f, longMap.loadFactor, 0.1);
    }

    @Test
    public void whenCreateWithArgumentsThenCreateWithPassedParams() {
        longMap = new LongMapImpl<>(7, 0.5f);
        assertEquals(7, longMap.table.length);
        assertEquals(0.5f, longMap.loadFactor, 0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCapacityLessThanZeroThenThrowIllegalArgumentException() {
        longMap = new LongMapImpl<>(-1, 0.75f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenLoadFactorEqualsZeroThenThrowIllegalArgumentException() {
        longMap = new LongMapImpl<>(1, 0f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenLoadFactorLessThanZeroThenThrowIllegalArgumentException() {
        longMap = new LongMapImpl<>(1, -1f);
    }

    @Test
    public void whenRichThresoldLimitThenDoubleStorageSize() {
        longMap = new LongMapImpl<>(0, 0.75f);
        assertEquals(LongMapImpl.EMPTY_TABLE, longMap.table);

        longMap.put(1, "One");
        assertEquals(1, longMap.table.length);

        longMap.put(2, "Two");
        assertEquals(2, longMap.table.length);

        longMap.put(3, "Three");
        assertEquals(4, longMap.table.length);
    }

    @Test
    public void whenGetByExistKeyThenReturnKeysValue() {
        longMap.put(1, "Some string");

        String actualString = longMap.get(1);
        assertEquals("Some string", actualString);
    }

    @Test
    public void whenGetByNonExistKeyThenReturnNull() {
        longMap.put(1, "Some string");

        String actualString = longMap.get(2);
        assertNull(actualString);
    }



    @Ignore
    @Test
    public void test() {
        Map<String, String> map1 = new HashMap<>(0);
        map1.put("One", "Two");
    }
}
