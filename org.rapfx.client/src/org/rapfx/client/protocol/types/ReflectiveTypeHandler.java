/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.types;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rapfx.client.ApplicationGlobals;
import org.rapfx.client.lifecycle.LifeCycle;

/**
 * {@link TypeHandler} base implementation that reflectively calls methods on handled objects for
 * setting properties, etc.
 * <p>
 * Implementations of widgets handled by a {@link ReflectiveTypeHandler} have to name their methods
 * in a certain way for the handler to find them when required:
 * <ul>
 * <li>Properties (example: "bounds"): <code>setBounds(?)</code> where ? is a basic protocol type
 * depending on the property being set.</li>
 * <li>Listen to Events (example: "active"): <code>setEventEnablement(Map { active: true })</code></li>
 * <li>Notify about event (example: "resize"): <code>onResize()</code></li>
 * <li>Call method (example: "storeData"): <code>storeData(?...)</code>. Arguments are extracted
 * from the protocol argument list.</li>
 * </ul>
 * 
 * @param <T>
 *            the type of the "type" handled by this {@link TypeHandler}.
 */
public abstract class ReflectiveTypeHandler<T extends RemoteObject> implements TypeHandler<T> {

    private static final Log log = LogFactory.getLog(ReflectiveTypeHandler.class);

    @Override
    public LifeCycle getLifeCycle() {
        return ApplicationGlobals.getInstance().getLifeCycle();
    }

    @Override
    public boolean set(T object, Map<String, ?> properties) {
        return callReflective(object, "set", properties);
    }

    @Override
    public boolean call(T object, String methodName, Map<String, ?> arguments) {
        List<Class<?>> argTypes = new ArrayList<>();
        List<Object> argValues = new ArrayList<>();
        for (Map.Entry<String, ?> arg : arguments.entrySet()) {
            argTypes.add(arg.getValue().getClass());
            argValues.add(arg.getValue());
        }

        Method m = getMethod(object, null, methodName,
                argTypes.toArray(new Class<?>[argTypes.size()]));
        invokeMethod(object, m, argValues.toArray());

        return true;
    }

    @Override
    public boolean listen(T object, Map<String, Boolean> events) {
        return callReflective(object, "set", Collections.singletonMap("eventEnablement", events));
    }

    @Override
    public boolean notify(T object, String event, Map<String, ?> properties) {
        return callReflective(object, "on", Collections.singletonMap(event, properties));
    }

    @Override
    public void destroy(T object) {
    }

    /**
     * For each "thing", find a method that is responsible for handling it, and call it through
     * reflection.
     * 
     * @param object
     *            the object instance to call on
     * @param prefix
     *            the optional prefix for the method
     * @param things
     *            the things to handle.
     * @return
     */
    private boolean callReflective(T object, String prefix, Map<String, ?> things) {
        boolean result = true;
        for (Map.Entry<String, ?> entry : things.entrySet()) {
            Object value = entry.getValue();
            Method m = getMethod(object, prefix, entry.getKey(), value != null ? value.getClass()
                    : null);

            if (m == null) {
                result = false;
                log.debug("cannot find method for property " + entry.getKey() + " on " + object);
                continue;
            }

            try {
                invokeMethod(object, m, entry.getValue());
            } catch (Exception e) {
                log.debug("error invoking " + m.getName() + " on " + object);
                result = false;
            }
        }
        return result;
    }

    /**
     * Tries to invoke a method, and writes a warning if it could not. Never throws.
     * 
     * @param object
     *            the object instance to call on
     * @param m
     *            the method to call
     * @param arguments
     *            the arguments to pass
     */
    private void invokeMethod(T object, Method m, Object... arguments) {
        try {
            m.invoke(object, arguments);
        } catch (Exception e) {
            log.warn("error invoking method " + m.getName() + " on " + object, e);
        }
    }

    /**
     * Tries to find a method on the given object that is suitable for the given (optional) prefix,
     * name (key) and the argument types (in the given order).
     * 
     * @param object
     *            the object instance to get the method from
     * @param prefix
     *            the optional prefix
     * @param key
     *            the method to look for. this is optionally prepended with the prefix (if not
     *            <code>null</code>; in this case the first letter of key is upper-cased)
     * @param args
     *            the argument types which the target method must accept
     * @return the found method or <code>null</code> if nothing matches
     */
    private Method getMethod(T object, String prefix, String key, Class<?>... args) {
        String name = prefix == null ? key : (prefix + (Character.toUpperCase(key.charAt(0))) + key
                .substring(1));
        for (Method m : object.getClass().getMethods()) {
            boolean ok = true;
            Class<?>[] parameterTypes = m.getParameterTypes();

            if (!m.getName().equals(name) || parameterTypes.length != args.length) {
                continue;
            }

            for (int i = 0; i < parameterTypes.length; ++i) {
                if (args[i] != null && !parameterTypes[i].isAssignableFrom(args[i])) {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                return m;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("cannot find " + name + "(" + toTypeList(args) + ") on " + object);
        }
        return null;
    }

    /**
     * Converts the given {@link Class}es to an argument list style {@link String} for debugging
     * purposes.
     * 
     * @param classes
     *            the {@link Class}es to stringify
     * @return the argument list style string
     */
    private static String toTypeList(Class<?>... classes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < classes.length; ++i) {
            if (i != 0) {
                builder.append(", ");
            }

            builder.append((classes[i] == null ? "null" : classes[i].getSimpleName()));
        }

        return builder.toString();
    }
}
