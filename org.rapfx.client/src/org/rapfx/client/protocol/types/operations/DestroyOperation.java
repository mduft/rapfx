/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.types.operations;

import org.rapfx.client.protocol.types.Operation;

/**
 * Requests destruction of the given object
 */
public class DestroyOperation extends Operation {

    public DestroyOperation(String targetId) {
        super(targetId);
    }

}
