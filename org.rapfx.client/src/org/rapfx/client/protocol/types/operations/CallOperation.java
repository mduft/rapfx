/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.types.operations;

import java.util.Map;

import org.rapfx.client.protocol.types.Operation;

/**
 * Represents a remote method call on a given object ID.
 */
public class CallOperation extends Operation {

    private final String method;
    private final Map<String, ?> arguments;

    /**
     * @param targetId
     *            the target object's ID
     * @param method
     *            the name of the method to call on that object
     * @param arguments
     *            the arguments as key-value pairs
     */
    public CallOperation(String targetId, String method, Map<String, ?> arguments) {
        super(targetId);
        this.method = method;
        this.arguments = arguments;
    }

    /**
     * @return the name of the method to call
     */
    public String getMethodName() {
        return method;
    }

    /**
     * @return the arguments to pass to the method
     */
    public Map<String, ?> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return toStringWithProperties(method, arguments);
    }

}
