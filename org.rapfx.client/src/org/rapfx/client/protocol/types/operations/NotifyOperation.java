/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.types.operations;

import java.util.Map;

import org.rapfx.client.protocol.types.Operation;

/**
 * Notifies the given target about an event that happened on the remote.
 */
public class NotifyOperation extends Operation {

    private final String event;
    private final Map<String, Object> properties;

    /**
     * @param targetId
     *            the target object
     * @param event
     *            the event that fired
     * @param properties
     *            optional properties associated with the event
     */
    public NotifyOperation(String targetId, String event, Map<String, Object> properties) {
        super(targetId);
        this.event = event;
        this.properties = properties;
    }

    /**
     * @return the name of the fired event
     */
    public String getEvent() {
        return event;
    }

    /**
     * @return the optional properties associated with the event.
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return toStringWithProperties(event, properties);
    }

}
