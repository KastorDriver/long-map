package de.comparus.opensource.longmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LongMapImpl<V> implements LongMap<V>, Cloneable, Serializable {

    static final int DEFAULT_CAPACITY = 16;
    static final int MAXIMUM_CAPACITY = 1 << 30;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final Entry<?>[] EMPTY_TABLE = {};

    Entry<V>[] table = (Entry<V>[]) EMPTY_TABLE;
    final float loadFactor;
    int threshold;
    int size;

    public LongMapImpl() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public LongMapImpl(int capacity, float loadFactor) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + capacity);
        }

        if (capacity > MAXIMUM_CAPACITY) {
            capacity = MAXIMUM_CAPACITY;
        }

        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }

        this.threshold = capacity;
        this.loadFactor = loadFactor;
    }

    public V put(long key, V value) {
        if (table == EMPTY_TABLE) {
            inflateTable(this.threshold);
        }

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

    private void inflateTable(int toSize) {
        int capacity = roundUpToPowerOf2(toSize);
        threshold = (int) Math.min(capacity * loadFactor, MAXIMUM_CAPACITY + 1);
        table = new Entry[capacity];
    }

    private int roundUpToPowerOf2(int number) {
        int rounded = number >= MAXIMUM_CAPACITY
                ? MAXIMUM_CAPACITY
                : (rounded = Integer.highestOneBit(number)) != 0
                    ? (Integer.bitCount(number) > 1) ? rounded << 1 : rounded
                    : 1;
        return rounded;
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
        for (Entry<V> entry : table) {
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
        int keyIndex = 0;

        Entry[] tab = table;
        for (int i = 0; i < tab.length; i++) {
            for (Entry entry = tab[i]; entry != null; entry = entry.next) {
                keys[keyIndex] = entry.key;
                keyIndex++;
            }
        }

        return keys;
    }

    //TODO is it possible with type erasure?
    public V[] values() {
        throw new UnsupportedOperationException();
    }

    public V[] values(V[] dest) {
        List<V> values = new ArrayList<V>(size);

        Entry[] tab = table;
        for (int i = 0; i < tab.length; i++) {
            for (Entry entry = tab[i]; entry != null; entry = entry.next) {
                values.add((V) entry.value);
            }
        }

        return values.toArray(dest);
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

    static class Entry<V> {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LongMapImpl<?> longMap = (LongMapImpl<?>) o;

        if (Float.compare(longMap.loadFactor, loadFactor) != 0) return false;
        if (threshold != longMap.threshold) return false;
        if (size != longMap.size) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(table, longMap.table);
    }

    @Override
    public int hashCode() {
        int hash = 0;

        for (int index = 0; index < table.length; index++) {
            for (Entry<V> entry = table[index]; entry != null; entry = entry.next) {
                hash += entry.hashCode();
            }
        }

        return hash;
    }
}
