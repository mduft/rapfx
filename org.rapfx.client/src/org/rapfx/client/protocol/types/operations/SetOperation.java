/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.types.operations;

import java.util.Map;

import org.rapfx.client.protocol.types.Operation;

/**
 * Requests values to be set on the target object.
 */
public class SetOperation extends Operation {

    private final Map<String, ?> properties;

    /**
     * @param targetId
     *            the target object
     * @param properties
     *            the properties to be set on the object
     */
    public SetOperation(String targetId, Map<String, ?> properties) {
        super(targetId);
        this.properties = properties;
    }

    /**
     * @return the properties for the target object
     */
    public Map<String, ?> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return toStringWithProperties(null, properties);
    }
}
