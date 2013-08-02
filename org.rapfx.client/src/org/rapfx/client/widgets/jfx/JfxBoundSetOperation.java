/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import org.rapfx.client.protocol.types.operations.SetOperation;

/**
 * {@link SetOperation} that memorizes Properties, so that the values for delayed sending are
 * queried at send time, and not before.
 */
public class JfxBoundSetOperation extends SetOperation {

    private final Class<?> targetClass;

    public JfxBoundSetOperation(Class<?> targetClass, String targetId, Map<String, ?> properties) {
        super(targetId, properties);
        this.targetClass = targetClass;
    }

    @Override
    public Map<String, ?> getProperties() {
        Map<String, Object> result = new TreeMap<>();

        for (Entry<String, ?> entry : super.getProperties().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof ObservableValue) {
                ObservableValue<?> prop = (Property<?>) value;
                value = getConvertedValue(prop.getValue());
            } else if (value instanceof ObservableValue<?>[]) {
                ObservableValue<?>[] arr = (ObservableValue<?>[]) value;
                Object[] resArr = new Object[arr.length];
                for (int i = 0; i < arr.length; ++i) {
                    resArr[i] = getConvertedValue(arr[i].getValue());
                }
                value = resArr;
            }
            result.put(key, value);
        }

        return result;
    }

    private Object getConvertedValue(Object val) {
        if (targetClass.isAssignableFrom(val.getClass())) {
            return targetClass.cast(val);
        }

        if (targetClass.equals(Integer.class)) {
            if (val instanceof Number) {
                return ((Number) val).intValue();
            } else {
                return Integer.valueOf(val.toString());
            }
        } else if (targetClass.equals(Double.class)) {
            if (val instanceof Number) {
                return ((Number) val).doubleValue();
            } else {
                return Double.valueOf(val.toString());
            }
        }

        throw new IllegalStateException("unsupported bound value type (source=" + val.getClass()
                + ", target=" + targetClass + ")");
    }

}
