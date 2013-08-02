/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol;

import java.util.Map;
import java.util.TreeMap;

/**
 * Tiny Helper to ease the task of creating Property {@link Map}s attached to {@link Message}s using
 * the builder pattern.
 * 
 * @param <T>
 *            the type of the carried Properties.
 */
public class PropertyBuilder<T> {

    private final Map<String, T> map = new TreeMap<String, T>();

    /**
     * Add another value to the target
     * 
     * @param key
     *            the key
     * @param value
     *            the value
     * @return <code>this</code>
     */
    public PropertyBuilder<T> add(String key, T value) {
        map.put(key, value);
        return this;
    }

    /**
     * @return the built {@link Map} of properties.
     */
    public Map<String, T> build() {
        return map;
    }

}
