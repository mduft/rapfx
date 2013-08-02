/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rapfx.client.lifecycle.LifeCycle;
import org.rapfx.client.transport.http.gson.HttpGsonTransport;
import org.rapfx.client.widgets.WidgetToolkit;
import org.rapfx.client.widgets.jfx.JfxToolkit;

/**
 * Entry point into the Client. Creates all required parts and wires them together.
 */
public class ApplicationClient {

    private static final Log log = LogFactory.getLog(ApplicationClient.class);
    private static final String DEFAULT_USER_AGENT = "rapfx";

    private final LifeCycle lifecycle;
    private final HttpGsonTransport transport;
    private final WidgetToolkit toolkit;

    /**
     * @param application
     *            the base {@link URL} where the RAP server is running
     */
    public ApplicationClient(URL application, String agent) {
        toolkit = new JfxToolkit();
        transport = new HttpGsonTransport(application, agent);
        lifecycle = new LifeCycle(transport, toolkit);

        ApplicationGlobals.initizlize(lifecycle, transport, toolkit);
    }

    /**
     * Runs the application. Only returns after the application has quit.
     */
    public void run() {
        lifecycle.start();
    }

    /**
     * @param args
     *            arguments to the application. currently only one argument is supported: the
     *            {@link URL} to the RAP server.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            usage();
            return;
        }

        String agent = DEFAULT_USER_AGENT;
        if (args.length > 1) {
            agent = args[1];
        }

        try {
            new ApplicationClient(new URL(args[0]), agent).run();
        } catch (MalformedURLException e) {
            log.error("failed to parse URL", e);
        }
    }

    /**
     * Print argument information to the console.
     */
    private static void usage() {
        log.info("Usage: " + ApplicationClient.class.getName() + " <URL> [<userAgent>]");
    }

}
