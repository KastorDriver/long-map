package de.comparus.opensource.longmap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.Assert.*;

public class LongMapImplTest {

    private LongMapImpl<String> longMap;

    @Before
    public void before() {
        longMap = new LongMapImpl<>();
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
    public void whenCreateWithoutArgumentsThenCreateWithDefaultParams() {
        assertEquals(16, longMap.threshold);
        assertEquals(0.75f, longMap.loadFactor, 0.1);
        assertEquals(0, longMap.table.length);
    }

    @Test
    public void whenCreateWithArgumentsThenCreateWithPassedParams() {
        longMap = new LongMapImpl<>(7, 0.5f);
        assertEquals(7, longMap.threshold);
        assertEquals(0.5f, longMap.loadFactor, 0.1);
        assertEquals(0, longMap.table.length);
    }

    @Test
    public void whenPutFirstElementThenInitializeStore() {
        longMap = new LongMapImpl<>(8, 0.5f);
        assertEquals(0, longMap.table.length);

        longMap.put(1, "first");
        assertEquals(8, longMap.table.length);
    }

    @Test
    public void whenResizeStoreThenResizeToNearestPowerOfTwo() {
        longMap = new LongMapImpl<>(7, 0.5f);
        assertEquals(0, longMap.table.length);

        longMap.put(1, "first");
        assertEquals(8, longMap.table.length);
    }

    @Test
    public void whenRichThresoldLimitThenDoubleStorageSize() {
        longMap = new LongMapImpl<>(0, 0.75f);
        assertArrayEquals(LongMapImpl.EMPTY_TABLE, longMap.table);

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

    @Test
    public void whenRemoveElementByKeyThenGetByThisKeyReturnNull() {
        longMap.put(1, "one");
        longMap.remove(1);
        assertNull(longMap.get(1));
    }

    @Test
    public void whenCreateNewMapThenSizeIsZero() {
        assertEquals(0, longMap.size());
    }

    @Test
    public void whenPutNewElementThenSizeIncreases() {
        assertEquals(0, longMap.size());

        longMap.put(1, "one");
        assertEquals(1, longMap.size());

        longMap.put(2, "two");
        assertEquals(2, longMap.size());
    }

    @Test
    public void whenRemoveElementThenSizeDecreases() {
        longMap.put(1, "one");
        longMap.put(2, "two");
        assertEquals(2, longMap.size());

        longMap.remove(1);
        assertEquals(1, longMap.size());

        longMap.remove(2);
        assertEquals(0, longMap.size());
    }

    @Test
    public void whenCreateNewMapThenItIsEmpty() {
        assertTrue(longMap.isEmpty());
    }

    @Test
    public void whenPutNewElementThenMapIsNotEmpty() {
        longMap.put(1, "one");
        assertFalse(longMap.isEmpty());
    }

    @Test
    public void whenRemoveLastElementThenMapIsEmpty() {
        longMap.put(1, "one");
        longMap.put(2, "two");

        longMap.remove(1);
        longMap.remove(2);
        assertTrue(longMap.isEmpty());
    }

    @Test
    public void whenCheckByNonExistKeyThenReturnFalse() {
        assertFalse(longMap.containsKey(1));
    }

    @Test
    public void whenCheckByExistKeyThenReturnFalse() {
        longMap.put(1, "one");
        assertTrue(longMap.containsKey(1));
    }

    @Test
    public void whenCheckByNonExistValueThenReturnFalse() {
        assertFalse(longMap.containsValue("one"));
    }

    @Test
    public void whenCheckByExistValueThenReturnFalse() {
        longMap.put(1, "one");
        assertTrue(longMap.containsValue("one"));
    }

    @Test
    public void whenRequestKeysByEmptyMapThenReturnEmptyArray() {
        assertArrayEquals(new long[0], longMap.keys());
    }

    @Test
    public void whenRequestKeysByFilledMapThenReturnArrayWithFilledKeys() {
        long[] expectedKeys = new long[2];
        expectedKeys[0] = 1;
        expectedKeys[1] = 2;

        Predicate<Long> existsFromExpectedKeys = (key) -> {
            for (long expectedKey: expectedKeys) {
                if (key == expectedKey) {
                    return true;
                }
            }

            return false;
        };

        longMap.put(1, "one");
        longMap.put(2, "two");

        assertTrue(existsFromExpectedKeys.test(1L));
        assertTrue(existsFromExpectedKeys.test(2L));
    }

    @Test
    public void whenClearMapThenSizeIsZero() {
        longMap.put(1, "one");
        longMap.put(2, "two");
        longMap.clear();

        assertEquals(0, longMap.size());
    }

    @Ignore
    @Test
    public void test() {
        Map<String, String> map1 = new HashMap<>(0);
        map1.put("One", "Two");
    }
}
