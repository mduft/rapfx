/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.types;

import java.util.Map;

import org.rapfx.client.lifecycle.LifeCycle;

/**
 * Describes an entity that is capable of handling requests for a certain type.
 */
public interface TypeHandler<T extends RemoteObject> {

    /**
     * @return the {@link LifeCycle} in charge for this {@link TypeHandler} instance.
     */
    public LifeCycle getLifeCycle();

    /**
     * Creates a new instance of the object this {@link TypeHandler} is responsible for.
     * 
     * @param target
     *            the target ID
     * @param properties
     *            initial properties of the new object
     * @return the newly created instance of this {@link TypeHandler}s target type.
     */
    public T create(String target, Map<String, ?> properties);

    /**
     * Sets a property on the given object.
     * 
     * @param object
     *            the object to set the property on.
     * @param properties
     *            the properties to set.
     */
    public boolean set(T object, Map<String, ?> properties);

    /**
     * Calls a method on the given object.
     * 
     * @param object
     *            the object to call the method on.
     * @param methodName
     *            the method name to call.
     * @param arguments
     *            the arguments to the method.
     */
    public boolean call(T object, String methodName, Map<String, ?> arguments);

    /**
     * Instructs the given object to fire notify operations for the given event.
     * 
     * @param object
     *            the object to start listening on
     * @param events
     *            description of the events and whether to fire notifies for them or not.
     */
    public boolean listen(T object, Map<String, Boolean> events);

    /**
     * Notifies the given object that the remote fired an event on the remote counterpart.
     * 
     * @param object
     *            the object to notify about a remote event.
     * @param event
     *            the name of the event that occurred.
     * @param properties
     *            the properties associated with the event.
     */
    public boolean notify(T object, String event, Map<String, ?> properties);

    /**
     * Requests destruction of the given object. Afterwards references to this object are given up.
     * 
     * @param object
     *            the instance to destroy.
     */
    public void destroy(T object);

}
