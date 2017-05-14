package de.comparus.opensource.longmap;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class LongMapImplTest {

    private LongMap<String> longMap;

    @Before
    public void before() {
        longMap = new LongMapImpl<>();
    }

    @Test
    public void test() {
        longMap.put(1, "One");
        longMap.put(1, "Two");

        Map<String, String> map1 = new HashMap<>();
        map1.put("One", "Two");

        Map<String, String> map2 = new HashMap<>();
        map2.put("One", "Two");

        System.out.println(map1.equals(map2));
    }
}
