/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.lifecycle;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rapfx.client.protocol.types.Operation;
import org.rapfx.client.protocol.types.RemoteObject;
import org.rapfx.client.protocol.types.TypeHandler;
import org.rapfx.client.protocol.types.operations.CallOperation;
import org.rapfx.client.protocol.types.operations.CreateOperation;
import org.rapfx.client.protocol.types.operations.DestroyOperation;
import org.rapfx.client.protocol.types.operations.ListenOperation;
import org.rapfx.client.protocol.types.operations.NotifyOperation;
import org.rapfx.client.protocol.types.operations.SetOperation;

/**
 * Dispatches {@link Operation}s to the correct {@link TypeHandler}.
 */
public class OperationDispatcher {

    private static final Log log = LogFactory.getLog(OperationDispatcher.class);

    /**
     * The {@link LifeCycle}, used to find {@link TypeHandler}s and {@link RemoteObject} s.
     */
    private final LifeCycle lifecycle;

    public OperationDispatcher(LifeCycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    /**
     * Dispatch an operation to the registered type handlers of the {@link LifeCycle}.
     * 
     * @param op
     *            the operation to dispatch
     * @return <code>true</code> if handled, <code>false</code> if not.
     */
    public boolean dispatch(Operation op) {
        String target = op.getTargetId();
        TypeHandler<RemoteObject> handler;
        if (op instanceof CreateOperation) {
            String type = ((CreateOperation) op).getTargetType();
            handler = lifecycle.getTypeHandlerRegistry().get(type);

            if (handler == null) {
                log.error("No handler for type " + type);
                return false;
            }

            Map<String, Object> properties = ((CreateOperation) op).getProperties();
            RemoteObject object = handler.create(target, properties);
            lifecycle.getObjectRegistry().set(target, object);
            object.initialize(handler, target, properties);
            return true;
        } else {
            RemoteObject object = lifecycle.getObjectRegistry().get(target);

            if (object == null) {
                log.warn("object " + target + " unkown (" + op + ")!");
                return false;
            }

            handler = object.getTypeHandler();

            if (op instanceof SetOperation) {
                return handler.set(object, ((SetOperation) op).getProperties());
            } else if (op instanceof CallOperation) {
                return handler.call(object, ((CallOperation) op).getMethodName(),
                        ((CallOperation) op).getArguments());
            } else if (op instanceof ListenOperation) {
                return handler.listen(object, ((ListenOperation) op).getEventStates());
            } else if (op instanceof NotifyOperation) {
                return handler.notify(object, ((NotifyOperation) op).getEvent(),
                        ((NotifyOperation) op).getProperties());
            } else if (op instanceof DestroyOperation) {
                handler.destroy(object);
                lifecycle.getObjectRegistry().remove(target);
                return true;
            }
        }
        throw new IllegalStateException("unknown operation " + op);
    }
}
