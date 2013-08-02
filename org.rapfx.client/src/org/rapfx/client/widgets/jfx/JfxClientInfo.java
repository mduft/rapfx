/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx;

import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import org.rapfx.client.ApplicationGlobals;
import org.rapfx.client.protocol.types.AbstractRemoteObject;
import org.rapfx.client.protocol.types.ReflectiveTypeHandler;
import org.rapfx.client.protocol.types.TypeHandler;
import org.rapfx.client.protocol.types.operations.SetOperation;

/**
 * Global object that provides Client specific information to the RAP server.
 */
public class JfxClientInfo extends AbstractRemoteObject {

    @Override
    public void initialize(TypeHandler<?> handler, String targetId, Map<String, ?> properties) {
        super.initialize(handler, targetId, properties);

        int tzOff = -(Calendar.getInstance().get(Calendar.ZONE_OFFSET) + Calendar.getInstance()
                .get(Calendar.DST_OFFSET)) / (60 * 1000);

        Map<String, Object> info = new TreeMap<>();
        info.put("timezoneOffset", tzOff);

        SetOperation op = new SetOperation(targetId, info);
        ApplicationGlobals.getInstance().getLifeCycle().send(op);
    }

    public static class Handler extends ReflectiveTypeHandler<JfxClientInfo> {

        public static final String ID = "rwt.client.ClientInfo";

        @Override
        public JfxClientInfo create(String target, Map<String, ?> properties) {
            return new JfxClientInfo();
        }

    }

}
