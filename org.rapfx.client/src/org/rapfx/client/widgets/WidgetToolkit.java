/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets;

import org.rapfx.client.lifecycle.LifeCycle;
import org.rapfx.client.protocol.Message;

/**
 * Entry point to the widget toolkit used by the Client to render the UI.
 */
public interface WidgetToolkit {

    /**
     * Initializes the toolkit to be ready to work for the given {@link LifeCycle}. This means it
     * should register required handlers with the given {@link LifeCycle}.
     * 
     * @param lifecycle
     *            the target {@link LifeCycle}.
     */
    public void initialize(LifeCycle lifecycle);

    /**
     * Runs the main loop of the {@link WidgetToolkit}. This method returning means end of the
     * Application.
     */
    public void run();

    /**
     * Dispatches the given {@link Message}, possibly moving processing into another thread.
     * 
     * @param msg
     *            the message to process.
     */
    public void dispatch(Message msg);

    /**
     * Execute a {@link Runnable} in the context of the Toolkit. This may or may not schedule the
     * {@link Runnable} to another {@link Thread}. The {@link Runnable} is executed after the
     * specified timeout. If the method is called again with the same runnable during that time, the
     * timeout is re-set to the given value.
     * 
     * @param runnable
     *            the {@link Runnable} to execute.
     */
    public void execute(Runnable runnable, int millis);
}
