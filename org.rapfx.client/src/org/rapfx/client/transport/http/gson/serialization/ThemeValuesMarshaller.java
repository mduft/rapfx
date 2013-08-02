/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.transport.http.gson.serialization;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rapfx.client.protocol.theme.ThemeValues;
import org.rapfx.client.protocol.theme.ThemeValues.BorderDefinition;
import org.rapfx.client.protocol.theme.ThemeValues.ImageDefinition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ThemeValuesMarshaller implements JsonDeserializer<ThemeValues> {

    private static final Log log = LogFactory.getLog(ThemeValuesMarshaller.class);

    @Override
    public ThemeValues deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        ThemeValues v = new ThemeValues();

        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();

            JsonElement dim = obj.get("dimensions");
            JsonElement boxdim = obj.get("boxdims");
            JsonElement images = obj.get("images");
            JsonElement gradients = obj.get("gradients");
            JsonElement fonts = obj.get("fonts");
            JsonElement colors = obj.get("colors");
            JsonElement borders = obj.get("borders");
            JsonElement cursors = obj.get("cursors");
            JsonElement animations = obj.get("animations");
            JsonElement shadows = obj.get("shadows");

            if (dim != null && dim.isJsonObject()) {
                for (Entry<String, JsonElement> entry : dim.getAsJsonObject().entrySet()) {
                    log.trace("dimension: " + entry.getKey() + "=" + entry.getValue());
                    v.dimensions.put(entry.getKey(), entry.getValue().getAsInt());
                }
            }

            if (boxdim != null && boxdim.isJsonObject()) {
                for (Entry<String, JsonElement> entry : boxdim.getAsJsonObject().entrySet()) {
                    log.trace("boxdimension: " + entry.getKey() + "=" + entry.getValue());
                    List<Double> dimvalues = context.deserialize(entry.getValue(), List.class);
                    v.boxdimensions.put(entry.getKey(), dimvalues);
                }
            }

            if (images != null && images.isJsonObject()) {
                for (Entry<String, JsonElement> entry : images.getAsJsonObject().entrySet()) {
                    log.trace("image: " + entry.getKey() + "=" + entry.getValue());
                    List<Double> values = context.deserialize(entry.getValue(), List.class);
                    v.images.put(entry.getKey(), new ImageDefinition(entry.getKey(), values.get(0)
                            .intValue(), values.get(1).intValue()));
                }
            }

            if (gradients != null && gradients.isJsonObject()) {
                for (Entry<String, JsonElement> entry : gradients.getAsJsonObject().entrySet()) {
                    log.trace("gradient: " + entry.getKey() + "=" + entry.getValue());
                    Map<String, Object> properties = context.deserialize(entry.getValue(),
                            Map.class);
                    v.gradients.put(entry.getKey(), properties);
                }
            }

            if (fonts != null && fonts.isJsonObject()) {
                for (Entry<String, JsonElement> entry : fonts.getAsJsonObject().entrySet()) {
                    log.trace("font: " + entry.getKey() + "=" + entry.getValue());
                    Map<String, Object> properties = context.deserialize(entry.getValue(),
                            Map.class);
                    v.fonts.put(entry.getKey(), properties);
                }
            }

            if (colors != null && colors.isJsonObject()) {
                for (Entry<String, JsonElement> entry : colors.getAsJsonObject().entrySet()) {
                    log.trace("color: " + entry.getKey() + "=" + entry.getValue());
                    if (entry.getValue().isJsonArray()) {
                        List<Double> colvalues = context.deserialize(entry.getValue(), List.class);
                        v.colors.put(entry.getKey(), colvalues);
                    } else {
                        v.colors.put(entry.getKey(), entry.getValue().getAsString());
                    }
                }
            }

            if (borders != null && borders.isJsonObject()) {
                for (Entry<String, JsonElement> entry : borders.getAsJsonObject().entrySet()) {
                    log.trace("border: " + entry.getKey() + "=" + entry.getValue());
                    BorderDefinition def = new BorderDefinition();
                    Map<String, Object> properties = context.deserialize(entry.getValue(),
                            Map.class);

                    def.type = (String) properties.get("style");
                    def.width = (double) properties.get("width");
                    def.color = properties.get("color");

                    v.borders.put(entry.getKey(), def);
                }
            }

            if (cursors != null && cursors.isJsonObject()) {
                for (Entry<String, JsonElement> entry : cursors.getAsJsonObject().entrySet()) {
                    log.trace("cursor: " + entry.getKey() + "=" + entry.getValue());
                    v.cursors.put(entry.getKey(), entry.getValue().getAsString());
                }
            }

            if (animations != null && animations.isJsonObject()) {
                for (Entry<String, JsonElement> entry : animations.getAsJsonObject().entrySet()) {
                    log.trace("animation: " + entry.getKey() + "=" + entry.getValue());
                    // TODO:
                }
            }

            if (shadows != null && shadows.isJsonObject()) {
                for (Entry<String, JsonElement> entry : shadows.getAsJsonObject().entrySet()) {
                    log.trace("shadow: " + entry.getKey() + "=" + entry.getValue());
                    // TODO:
                }
            }
        }
        return v;
    }

}
