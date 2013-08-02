/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rapfx.client.ApplicationGlobals;
import org.rapfx.client.protocol.types.RemoteObject;
import org.rapfx.client.widgets.jfx.impl.JfxShell;

public class JfxDebugHelper {

    private static final Log log = LogFactory.getLog(JfxDebugHelper.class);

    public static void traceWidgetTree() {
        try {
            for (JfxShell shell : traceFindShells()) {
                traceRecursive("", shell);
            }
        } catch (Exception ex) {
            log.trace("exception during trace: " + ex.getMessage());
        }
    }

    private static void traceRecursive(String indent, JfxBaseObject<?> base) {
        log.trace(indent + base);
        for (RemoteObject o : base.getChildrenAsObjects()) {
            if (o instanceof JfxBaseObject) {
                traceRecursive(indent + "  ", (JfxBaseObject<?>) o);
            } else {
                log.trace(indent + "  [foreign] " + o);
            }
        }
    }

    static List<JfxShell> traceFindShells() {
        List<JfxShell> shells = new ArrayList<>();
        for (Map.Entry<String, ?> entry : ApplicationGlobals.getInstance().getLifeCycle()
                .getObjectRegistry().getAll()) {
            if (entry.getValue() instanceof JfxShell) {
                shells.add((JfxShell) entry.getValue());
            }
        }
        return shells;
    }

}
