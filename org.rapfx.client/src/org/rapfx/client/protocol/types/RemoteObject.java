/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.types;

import java.util.Map;

/**
 * Base interface for all objects that have a remote counterpart on the server side. The ID of the
 * object must be the same on server and client.
 */
public interface RemoteObject {

    /**
     * Initializes the object.
     * 
     * @param handler
     *            the handler that created this object.
     * @param targetId
     *            the id of this object.
     */
    public void initialize(TypeHandler<?> handler, String targetId, Map<String, ?> properties);

    /**
     * @return the unique ID of the object and it's remote counterpart.
     */
    public String getObjectId();

    /**
     * @return the handler that created this {@link RemoteObject}.
     */
    public <T extends RemoteObject> TypeHandler<T> getTypeHandler();

}
