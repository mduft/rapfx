/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.types.operations;

import java.util.Map;

import org.rapfx.client.lifecycle.LifeCycle;
import org.rapfx.client.protocol.types.Operation;
import org.rapfx.client.protocol.types.TypeHandler;

/**
 * Represents the request to create a new object with the given ID
 */
public class CreateOperation extends Operation {

    private final String type;
    private final Map<String, Object> properties;

    /**
     * @param targetId
     *            the new object's target ID
     * @param type
     *            the type of the object, used to find the correct {@link TypeHandler}.
     * @param properties
     *            the object's initial properties
     */
    public CreateOperation(String targetId, String type, Map<String, Object> properties) {
        super(targetId);
        this.type = type;
        this.properties = properties;
    }

    /**
     * @return the type of the object to be created. The {@link LifeCycle} must have an appropriate
     *         {@link TypeHandler} registration for it to be able to create the object.
     */
    public String getTargetType() {
        return type;
    }

    /**
     * @return the object's initial properties
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return toStringWithProperties(type, properties);
    }
}
