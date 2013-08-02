/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.types;

import org.rapfx.client.protocol.Message;

/**
 * Describes additional information attached to a {@link Message}
 */
public class Header {

    private final String name;
    private final Object value;

    public Header(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name of the {@link Header}.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value of the {@link Header}.
     */
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Header [ name=" + getName() + ", value=" + getValue() + " ]";
    }

}
