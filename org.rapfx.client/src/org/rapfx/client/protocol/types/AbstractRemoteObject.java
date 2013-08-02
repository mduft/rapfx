/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.types;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for remote objects, typically used by Widget implementations
 */
public abstract class AbstractRemoteObject implements RemoteObject {

    private static final Log log = LogFactory.getLog(AbstractRemoteObject.class);
    private TypeHandler<? extends RemoteObject> handler;
    private Map<String, ?> properties;
    private String targetId;

    private final Map<String, Boolean> eventEnablement = new TreeMap<String, Boolean>();

    @Override
    public void initialize(TypeHandler<?> handler, String targetId, Map<String, ?> properties) {
        this.handler = handler;
        this.targetId = targetId;
        this.properties = properties;
    }

    /**
     * Used by the {@link ReflectiveTypeHandler} to set event enablement
     * 
     * @param events
     *            the events and their enablement state.
     */
    public void setEventEnablement(Map<String, Boolean> events) {
        if (log.isDebugEnabled()) {
            log.debug("events on " + this + ": " + events);
        }

        eventEnablement.putAll(events);
    }

    /**
     * Manually enable or disable notification for the given event.
     * 
     * @param name
     *            the event
     * @param enable
     *            whether to force enable or disable event notification
     */
    public void setEventEnablement(String name, boolean enable) {
        eventEnablement.put(name, enable);
    }

    /**
     * @param name
     *            event to check
     * @return whether the given event should cause a notification to the server or not.
     */
    public boolean isEventEnabled(String name) {
        Boolean b = eventEnablement.get(name);
        if (b == null || b.booleanValue() == false) {
            return false;
        }
        return true;
    }

    /**
     * @return the initial object properties.
     */
    public Map<String, ?> getProperties() {
        return properties;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RemoteObject> TypeHandler<T> getTypeHandler() {
        return (TypeHandler<T>) handler;
    }

    @Override
    public String getObjectId() {
        return targetId;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + targetId + ")";
    }

}
