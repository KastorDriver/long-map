package de.comparus.opensource.longmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LongMapImpl<V> implements LongMap<V>, Cloneable, Serializable {

    private static final int DEFAULT_CAPACITY = 16;
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Entry<V>[] table;
    private final float loadFactor;
    private int threshold;
    private int size;

    public LongMapImpl() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public LongMapImpl(int capacity, float loadFactor) {
        this.table = new Entry[capacity];
        this.loadFactor = loadFactor;
    }

    public V put(long key, V value) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);
        for (Entry<V> entry = table[index]; entry != null; entry = entry.next) {
            if (key == entry.key) {
                V oldValue = entry.value;
                entry.value = value;
                return oldValue;
            }
        }

        addEntry(hash, key, value, index);
        return null;
    }

    private void addEntry(int hash, long key, V value, int index) {
        if ((size >= threshold) && (null != table[index])) {
            resize(2 * table.length);
            index = indexFor(hash, table.length);
        }

        createEntry(hash, key, value, index);
    }

    private void resize(int newCapacity) {
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable);
        table = newTable;
        threshold = (int) Math.min(newCapacity * loadFactor, MAXIMUM_CAPACITY + 1);
    }

    private void transfer(Entry[] newTable) {
        int newCapacity = newTable.length;
        for (Entry<V> entry: table) {
            while (entry != null) {
                Entry<V> next = entry.next;
                int index = indexFor(entry.hash, newCapacity);
                entry.next = newTable[index];
                newTable[index] = entry;
                entry = next;
            }
        }
    }

    private void createEntry(int hash, long key, V value, int index) {
        Entry<V> entry = table[index];
        table[index] = new Entry<>(hash, key, value, entry);
        size++;
    }

    public V get(long key) {
        Entry<V> entry = getEntry(key);
        return entry == null ? null : entry.value;
    }

    private Entry<V> getEntry(long key) {
        if (size == 0) {
            return null;
        }

        int hash = hash(key);
        int index = indexFor(hash, table.length);
        for (Entry<V> entry = table[index]; entry != null; entry = entry.next) {
            if (key == entry.key) {
                return entry;
            }
        }

        return null;
    }

    public V remove(long key) {
        Entry<V> entry = removeEntryForKey(key);
        return entry == null ? null : entry.value;
    }

    private Entry<V> removeEntryForKey(long key) {
        if (size == 0) {
            return null;
        }

        int hash = hash(key);
        int index = indexFor(hash, table.length);
        Entry<V> prev = table[index];
        Entry<V> entry = prev;

        while (entry != null) {
            Entry<V> next = entry.next;
            if (key == entry.key) {
                size--;
                if (prev == entry) {
                    table[index] = next;
                } else {
                    prev.next = next;
                }

                return entry;
            }

            prev = entry;
            entry = next;
        }

        return entry;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(long key) {
        return getEntry(key) != null;
    }

    public boolean containsValue(V value) {
        if (value == null) {
            return containsNullValue();
        }

        Entry[] tab = table;
        for (int i = 0; i < tab.length; i++) {
            for (Entry entry = tab[i]; entry != null; entry = entry.next) {
                if (value.equals(entry.value)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean containsNullValue() {
        Entry[] tab = table;
        for (int i = 0; i < tab.length; i++) {
            for (Entry entry = tab[i]; entry != null; entry = entry.next) {
                if (entry.value == null) {
                    return true;
                }
            }
        }

        return false;
    }

    public long[] keys() {
        long[] keys = new long[size];
        int nextKeyIndex = 0;

        Entry[] tab = table;
        for (int i = 0; i < tab.length; i++) {
            for (Entry entry = tab[i]; entry != null; entry = entry.next) {
                keys[nextKeyIndex] = entry.key;
                nextKeyIndex++;
            }
        }

        return keys;
    }

    public V[] values() {
        List<V> values = new ArrayList<>(size);

        Entry<V>[] tab = table;
        for (int i = 0; i < tab.length; i++) {
            for (Entry<V> entry = tab[i]; entry != null; entry = entry.next) {
                values.add(entry.value);
            }
        }

        return (V[]) values.toArray();
    }

    public long size() {
        return size;
    }

    public void clear() {
        Arrays.fill(table, null);
        size = 0;
    }

    private static int hash(long key) {
        return Long.hashCode(key);
    }

    private static int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    private static class Entry<V> {
        final long key;
        V value;
        Entry<V> next;
        int hash;

        public Entry(int hash, long key, V value, Entry<V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Entry<?> entry = (Entry<?>) o;

            if (key != entry.key) return false;
            return value != null ? value.equals(entry.value) : entry.value == null;
        }

        @Override
        public int hashCode() {
            int result = (int) (key ^ (key >>> 32));
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }
}
