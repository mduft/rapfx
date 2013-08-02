/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.transport.http.gson.serialization;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import org.rapfx.client.protocol.Message;
import org.rapfx.client.protocol.types.Header;
import org.rapfx.client.protocol.types.Operation;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Responsible (GSON specific) Marshal for serializing and deserializing {@link Message} objects.
 */
public class MessageMarshaller implements JsonSerializer<Message>, JsonDeserializer<Message> {

    private static final String MEMBER_OPS = "operations";
    private static final String MEMBER_HEAD = "head";

    @Override
    public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        Message msg = new Message();

        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            if (obj.has(MEMBER_HEAD)) {
                JsonElement head = obj.get(MEMBER_HEAD);
                if (head.isJsonObject()) {
                    for (Entry<String, JsonElement> entry : head.getAsJsonObject().entrySet()) {
                        msg.addHeader(new Header(entry.getKey(), entry.getValue().getAsString()));
                    }
                }
            }

            if (obj.has(MEMBER_OPS)) {
                JsonElement ops = obj.get(MEMBER_OPS);
                if (ops.isJsonArray()) {
                    for (JsonElement element : ops.getAsJsonArray()) {
                        Operation op = context.deserialize(element, Operation.class);
                        msg.addOperation(op);
                    }
                }
            }
        }

        return msg;
    }

    @Override
    public JsonElement serialize(Message src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        JsonObject head = new JsonObject();
        JsonArray operations = new JsonArray();

        for (Header header : src.getHeaders()) {
            Object value = header.getValue();
            if (value instanceof String) {
                head.add(header.getName(), new JsonPrimitive((String) value));
            } else if (value instanceof Boolean) {
                head.add(header.getName(), new JsonPrimitive((Boolean) value));
            } else if (value instanceof Number) {
                head.add(header.getName(), new JsonPrimitive((Number) value));
            } else {
                throw new IllegalStateException("illegal value type in header: " + header);
            }
        }

        for (Operation op : src.getOperations()) {
            operations.add(context.serialize(op, Operation.class));
        }

        obj.add(MEMBER_HEAD, head);
        obj.add(MEMBER_OPS, operations);

        return obj;
    }
}
