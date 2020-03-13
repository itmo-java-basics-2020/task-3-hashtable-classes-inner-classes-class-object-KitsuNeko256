package ru.itmo.java;

import java.util.Map;


public class HashTable {

    private static final int INITIAL_CAPACITY = 16;
    private static final double INITIAL_LOAD_FACTOR = 0.5;
    private static final int INITIAL_STEP = 137;

    private static class Entry {
        private Object key;
        private Object value;

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    private int capacity;
    private double loadFactor;
    private int size;
    private int notNullSize;
    private int step;
    private Entry[] elements;

    public HashTable(int inputCapacity, double inputLoadFactor) {
        this.capacity = Math.max(1, inputCapacity);
        this.step = (this.capacity <= INITIAL_STEP) ? 1 : INITIAL_STEP;
        this.loadFactor = Math.max(0, Math.min(inputLoadFactor, 1));
        this.elements = new Entry[this.capacity];
    }

    public HashTable(int inputCapacity) {
        this(inputCapacity, INITIAL_LOAD_FACTOR);
    }

    public HashTable() {
        this(INITIAL_CAPACITY, INITIAL_LOAD_FACTOR);
    }

    private boolean isIndexEmpty(int index) {
        return this.elements[index] == null || this.elements[index].key == null;
    }

    private int findIndex(Object key) {
        int index = Math.abs(key.hashCode()) % this.capacity;
        int firstEmpty = -1;
        while (this.elements[index] != null && (this.elements[index].key == null || !this.elements[index].key.equals(key))) {
            if (firstEmpty == -1 && this.isIndexEmpty(index)) {
                firstEmpty = index;
            }
            index = (index + this.step) % this.capacity;
        }
        return index;
    }

    Object get(Object key) {
        if (key == null) {
            return null;
        }
        int index = this.findIndex(key);
        if (this.isIndexEmpty(index) || !this.elements[index].key.equals(key)) {
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
            if (this.elements[index] == null)
                ++this.notNullSize;
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
        // Element not found - return null
        if (isIndexEmpty(index)) {
            return null;
        }
        // Element is found - delete it
        Object previous = this.elements[index].value;
        if (this.elements[(index + this.step) % this.capacity] == null) {
            this.elements[index].key = null;
            while (this.elements[index] != null && this.elements[index].key == null) {
                this.elements[index] = null;
                index -= this.step;
                if (index < 0) {
                    index += this.capacity;
                }
            }
        } else {
            this.elements[index] = new Entry(null, null);
        }
        --size;
        return previous;
    }

    private void ensureCapacity() {
        if (this.size < this.capacity * this.loadFactor && this.notNullSize < this.capacity * Math.max(loadFactor, 0.70 ) && this.notNullSize < capacity) {
            return;
        }

        Entry[] tElements = this.elements;

        this.size = 0;
        this.notNullSize = 0;
        if (this.size < this.capacity * this.loadFactor) {
            this.capacity *= 2;
        }
        this.step = (this.capacity <= INITIAL_STEP) ? 1 : INITIAL_STEP;
        this.elements = new Entry[this.capacity];

        for (Entry tElement : tElements) {
            if (tElement != null && tElement.key != null) {
                this.put(tElement.key, tElement.value);
            }
        }
    }

    int size() {
        return this.size;
    }

}