/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.lifecycle;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Holds references to all known objects, reference-able through their ID.
 */
public class ObjectRegistry<T> {

    private final Map<String, T> objects = new TreeMap<>();

    /**
     * Stores the given object instance under the given key.
     * 
     * @param id
     *            the id of the object
     * @param obj
     *            the object instance
     */
    public void set(String id, T obj) {
        if (objects.containsKey(id)) {
            throw new IllegalStateException("object already registered: " + id);
        }

        objects.put(id, obj);
    }

    /**
     * Retrieves a previously stored object with the given id. It is the responsibility of the
     * caller to assure that the type of the object matches the requested type.
     * 
     * @param id
     *            the id of the object to retrieve
     * @return the object previously stored through {@link #set(String, Object)}
     */
    @SuppressWarnings("unchecked")
    public <X extends T> X get(String id) {
        return (X) objects.get(id);
    }

    /**
     * Forgets the given object if it was part of the registry.
     * 
     * @param id
     *            the id of the object to remove.
     */
    public void remove(String id) {
        objects.remove(id);
    }

    /**
     * @return all entries in the registry
     */
    public Set<Map.Entry<String, T>> getAll() {
        return objects.entrySet();
    }
}
