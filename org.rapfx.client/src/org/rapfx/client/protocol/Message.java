/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol;

import java.util.ArrayList;
import java.util.List;

import org.rapfx.client.protocol.types.Header;
import org.rapfx.client.protocol.types.Operation;

/**
 * Describes a message received from or sent to the RAP-aware Server
 */
public class Message {

    private final List<Header> headers = new ArrayList<>();
    private final List<Operation> operations = new ArrayList<>();

    /**
     * @return all headers sent along with this {@link Message}.
     */
    public List<Header> getHeaders() {
        return headers;
    }

    /**
     * @param key
     *            the name of the requested {@link Header}.
     * @return whether this {@link Header} is part of the {@link Message}
     */
    public boolean containsHeader(String key) {
        for (Header header : getHeaders()) {
            if (header.getName().equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param key
     *            the name of the requested {@link Header}.
     * @return the value of the {@link Header} if it exists, <code>null</code> otherwise.
     */
    public Object getHeaderValue(String key) {
        for (Header header : getHeaders()) {
            if (header.getName().equals(key)) {
                return header.getValue();
            }
        }
        return null;
    }

    /**
     * Registers an additional {@link Header} with this {@link Message}, which will be sent to the
     * server once the {@link Message} is sent.
     * 
     * @param header
     *            additional {@link Header} to register with this {@link Message}.
     */
    public void addHeader(Header header) {
        headers.add(header);
    }

    /**
     * @return all operations registered with this {@link Message}
     */
    public List<Operation> getOperations() {
        return operations;
    }

    /**
     * Adds additional {@link Operation}s to the list of {@link Operation}s already registered with
     * this {@link Message}.
     * 
     * @param op
     *            the next {@link Operation} that should be sent to the server.
     */
    public void addOperation(Operation op) {
        // TODO: optimize - squash subsequent set/event chains
        operations.add(op);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Message [\n");
        for (Header header : getHeaders()) {
            builder.append("\t " + header.toString() + "\n");
        }
        for (Operation op : getOperations()) {
            builder.append("\t " + op.toString() + "\n");
        }
        builder.append("]\n");
        return builder.toString();
    }

}
