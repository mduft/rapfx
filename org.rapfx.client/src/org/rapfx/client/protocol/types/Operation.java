/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.types;

import java.util.Map;

/**
 * Describes an operation supported by the RAP Protocol
 */
public abstract class Operation {

    private final String targetId;

    public Operation(String targetId) {
        this.targetId = targetId;
    }

    /**
     * @return the ID of the target {@link RemoteObject} or it's counterpart on the server side
     *         (which is the same).
     */
    public String getTargetId() {
        return targetId;
    }

    /**
     * Helper for subclass {@link #toString()} implementations that require more information to be
     * rendered.
     * 
     * @param target
     *            the "target" (in case of an event: the event name, in case of a call: the method,
     *            ...).
     * @param props
     *            the properties or arguments passed to the target.
     * @return a {@link String} representation suitable for {@link #toString()}
     */
    protected String toStringWithProperties(String target, Map<String, ?> props) {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [ id=").append(getTargetId());
        if (target != null) {
            builder.append(", target=").append(target);
        }
        if (props != null) {
            for (Map.Entry<String, ?> entry : props.entrySet()) {
                builder.append(", ").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        builder.append(" ]");
        return builder.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ id=" + targetId + " ]";
    }

}
