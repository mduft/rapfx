/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.types.operations;

import java.util.Map;

import org.rapfx.client.protocol.Message;
import org.rapfx.client.protocol.types.Operation;

/**
 * Requests events to be fired (or suppressed) when they happen on the given target. Suppressing
 * events can help greatly in saving bandwidth by avoiding unnecessary {@link Message}s.
 */
public class ListenOperation extends Operation {

    private final Map<String, Boolean> events;

    /**
     * @param targetId
     *            the target object to listen to
     * @param events
     *            the event names (key) and whether it should fire or not (value)
     */
    public ListenOperation(String targetId, Map<String, Boolean> events) {
        super(targetId);
        this.events = events;
    }

    /**
     * @return the event states (see {@link #ListenOperation(String, Map)})
     */
    public Map<String, Boolean> getEventStates() {
        return events;
    }

    @Override
    public String toString() {
        return toStringWithProperties(null, events);
    }

}
