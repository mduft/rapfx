/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.transport.http.gson.serialization;

import java.lang.reflect.Type;

import org.rapfx.client.protocol.theme.Theme;
import org.rapfx.client.protocol.theme.ThemeData;
import org.rapfx.client.protocol.theme.ThemeValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ThemeMarshaller implements JsonDeserializer<Theme> {

    public static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Theme.class, new ThemeMarshaller())
            .registerTypeAdapter(ThemeValues.class, new ThemeValuesMarshaller())
            .registerTypeAdapter(ThemeData.class, new ThemeDataMarshaller()).create();

    public static Theme fromJson(String json) {
        return gson.fromJson(json, Theme.class);
    }

    @Override
    public Theme deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        ThemeValues values = null;
        ThemeData data = null;

        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            if (obj.has("values")) {
                values = context.deserialize(obj.get("values"), ThemeValues.class);
            }
            if (obj.has("theme")) {
                data = context.deserialize(obj.get("theme"), ThemeData.class);
            }
        }

        return new Theme(values, data);
    }

}
