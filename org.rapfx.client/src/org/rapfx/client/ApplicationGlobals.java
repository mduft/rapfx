/*
 * Copyright (c) Salomon Automation GmbH
 */
package org.rapfx.client;

import org.rapfx.client.lifecycle.LifeCycle;
import org.rapfx.client.transport.Transport;
import org.rapfx.client.widgets.WidgetToolkit;

public class ApplicationGlobals {

    private static ApplicationGlobals instance;

    private final Transport transport;
    private final WidgetToolkit toolkit;
    private final LifeCycle lifecycle;

    private ApplicationGlobals(LifeCycle lifecycle, Transport transport, WidgetToolkit toolkit) {
        this.lifecycle = lifecycle;
        this.transport = transport;
        this.toolkit = toolkit;
    }

    static void initizlize(LifeCycle lifecycle, Transport transport, WidgetToolkit toolkit) {
        instance = new ApplicationGlobals(lifecycle, transport, toolkit);
    }

    public static ApplicationGlobals getInstance() {
        return instance;
    }

    public LifeCycle getLifeCycle() {
        return lifecycle;
    }

    public WidgetToolkit getToolkit() {
        return toolkit;
    }

    public Transport getTransport() {
        return transport;
    }

}
