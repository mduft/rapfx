/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.transport.http.gson.serialization;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.rapfx.client.protocol.theme.ThemeData;
import org.rapfx.client.widgets.jfx.impl.JfxStyleHolder.Style;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class ThemeDataMarshaller implements JsonDeserializer<ThemeData> {

    @Override
    public ThemeData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        ThemeData data = new ThemeData();

        if (json.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
                String className = entry.getKey();

                Map<String, List<List<?>>> values = context
                        .deserialize(entry.getValue(), Map.class);

                for (Map.Entry<String, List<List<?>>> val : values.entrySet()) {
                    String propertyName = val.getKey();

                    for (List<?> prop : val.getValue()) {
                        if (prop.size() != 2) {
                            throw new IllegalStateException(
                                    "touples does not contain exactly 2 values");
                        }

                        @SuppressWarnings("unchecked")
                        List<String> states = (List<String>) prop.get(0);
                        String valueId = (String) prop.get(1);

                        data.addData(className, propertyName, getStyles(states),
                                getPseudoClasses(states), valueId);
                    }
                }
            }
        }

        return data;
    }

    private Set<String> getPseudoClasses(List<String> allStates) {
        Set<String> result = new TreeSet<>();
        for (String state : allStates) {
            if (state.startsWith(":")) {
                result.add(state);
            }
        }
        return result;
    }

    private static Set<Style> getStyles(List<String> allStates) {
        Set<Style> result = new TreeSet<>();
        for (String state : allStates) {
            if (state.startsWith("[")) {
                result.add(Style.valueOf(state.substring(1)));
            }
        }
        return result;
    }
}
