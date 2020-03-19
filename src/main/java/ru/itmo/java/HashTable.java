package ru.itmo.java;

import java.util.Map;


public class HashTable {

    private static final int INITIAL_CAPACITY = 16;
    private static final double INITIAL_LOAD_FACTOR = 0.5;
    private static final int INITIAL_STEP = 137;

    private int capacity;
    private double loadFactor;
    private int size;
    private int notNullSize;
    private int step;
    private Entry[] elements;

    public HashTable() {
        this(INITIAL_CAPACITY, INITIAL_LOAD_FACTOR);
    }

    public HashTable(int inputCapacity) {
        this(inputCapacity, INITIAL_LOAD_FACTOR);
    }

    public HashTable(int inputCapacity, double inputLoadFactor) {
        this.capacity = Math.max(1, inputCapacity);
        this.step = (this.capacity <= INITIAL_STEP) ? 1 : INITIAL_STEP;
        this.loadFactor = Math.max(0, Math.min(inputLoadFactor, 1));
        this.elements = new Entry[this.capacity];
    }

    Object get(Object key) {
        if (key == null) {
            return null;
        }
        int index = this.findIndex(key);
        if (this.isIndexEmpty(index)) {
            return null;
        }
        return this.elements[index].value;
    }

    Object put(Object key, Object value) {
        if (key == null) {
            return null;
        }
        int index = this.findIndex(key);
        // Element not found - add it
        if (this.isIndexEmpty(index)) {
            if (this.elements[index] == null) {
                ++this.notNullSize;
            }
            this.elements[index] = new Entry(key, value);
            ++this.size;
            this.ensureCapacity();
            return null;
        }
        // Element is found - change value
        Object previous = this.elements[index].value;
        this.elements[index].value = value;
        return previous;
    }

    Object remove(Object key) {
        if (key == null) {
            return null;
        }
        int index = this.findIndex(key);
        if (isIndexEmpty(index)) {
            return null;
        }
        Object previous = this.elements[index].value;
        //mark current element as deleted
        this.elements[index] = new Entry(null, null);
        --size;
        //if next element is null - replace this and all previous elements that are marked as deleted with nulls
        //(optimization)
        if (this.elements[(index + this.step) % this.capacity] == null) {
            while (this.elements[index] != null && this.elements[index].isDeleted()) {
                this.elements[index] = null;
                index -= this.step;
                if (index < 0) {
                    index += this.capacity;
                }
            }
        }
        return previous;
    }

    int size() {
        return this.size;
    }

    private boolean isIndexEmpty(int index) {
        return this.elements[index] == null || this.elements[index].isDeleted();
    }

    private int findIndex(Object key) {
        int index = Math.abs(key.hashCode()) % this.capacity;
        //if element is null or required element is found, end search
        while (this.elements[index] != null
                && !(!this.elements[index].isDeleted() && this.elements[index].key.equals(key))) {
            index = (index + this.step) % this.capacity;
        }
        return index;
    }

    private void ensureCapacity() {
        //recreate array if allowed size is exceeded or no nulls are left in the array
        if (this.size < this.capacity * this.loadFactor && this.notNullSize < this.capacity) {
            return;
        }

        Entry[] tElements = this.elements;

        //increase array size if array recreation is caused by exceeding size
        if (this.size >= this.capacity * this.loadFactor) {
            this.capacity *= 2;
        }
        this.size = 0;
        this.notNullSize = 0;
        this.step = (this.capacity <= INITIAL_STEP) ? 1 : INITIAL_STEP;
        this.elements = new Entry[this.capacity];

        for (Entry tElement : tElements) {
            if (tElement != null && !tElement.isDeleted()) {
                this.put(tElement.key, tElement.value);
            }
        }
    }

    private static class Entry {
        private final Object key;
        private Object value;

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        boolean isDeleted() {
            return key == null;
        }
    }

}