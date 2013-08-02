/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.transport.http.gson.serialization;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import org.rapfx.client.protocol.Message;
import org.rapfx.client.protocol.types.Operation;
import org.rapfx.client.protocol.types.operations.CallOperation;
import org.rapfx.client.protocol.types.operations.CreateOperation;
import org.rapfx.client.protocol.types.operations.DestroyOperation;
import org.rapfx.client.protocol.types.operations.ListenOperation;
import org.rapfx.client.protocol.types.operations.NotifyOperation;
import org.rapfx.client.protocol.types.operations.SetOperation;

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
 * Responsible (GSON specific) Marshal for serializing and deserializing {@link Operation} objects
 * attached to {@link Message}s.
 */
public class OperationMarshaller implements JsonSerializer<Operation>, JsonDeserializer<Operation> {

    @Override
    public Operation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json.isJsonArray()) {
            JsonArray data = json.getAsJsonArray();

            if (data.size() < 1) {
                throw new IllegalStateException("malformed operation: " + json);
            }

            String opcode = data.get(0).getAsString();
            switch (opcode) {
            case "create":
                return new CreateOperation(data.get(1).getAsString(), data.get(2).getAsString(),
                        deserializeProperties(context, data, 3));
            case "set":
                return new SetOperation(data.get(1).getAsString(), deserializeProperties(context,
                        data, 2));
            case "call":
                return new CallOperation(data.get(1).getAsString(), data.get(2).getAsString(),
                        deserializeProperties(context, data, 3));
            case "listen":
                return new ListenOperation(data.get(1).getAsString(),
                        this.<Boolean> deserializeProperties(context, data, 2));
            case "notify":
                return new NotifyOperation(data.get(1).getAsString(), data.get(2).getAsString(),
                        deserializeProperties(context, data, 3));
            case "destroy":
                return new DestroyOperation(data.get(1).getAsString());
            }
        }

        throw new IllegalStateException("unknown operation: " + json);
    }

    @SuppressWarnings("unchecked")
    private <T> Map<String, T> deserializeProperties(JsonDeserializationContext context,
            JsonArray data, int index) {
        return (Map<String, T>) (data.size() > index ? context.deserialize(data.get(index),
                Map.class) : Collections.emptyMap());
    }

    @Override
    public JsonElement serialize(Operation src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();

        if (src instanceof CallOperation) {
            array.add(new JsonPrimitive("call"));
            array.add(new JsonPrimitive(src.getTargetId()));
            array.add(new JsonPrimitive(((CallOperation) src).getMethodName()));
            array.add(serializeProperties(context, ((CallOperation) src).getArguments()));
        } else if (src instanceof SetOperation) {
            array.add(new JsonPrimitive("set"));
            array.add(new JsonPrimitive(src.getTargetId()));
            array.add(serializeProperties(context, ((SetOperation) src).getProperties()));
        } else if (src instanceof NotifyOperation) {
            array.add(new JsonPrimitive("notify"));
            array.add(new JsonPrimitive(src.getTargetId()));
            array.add(new JsonPrimitive(((NotifyOperation) src).getEvent()));
            array.add(serializeProperties(context, ((NotifyOperation) src).getProperties()));
        } else {
            throw new IllegalStateException("operation not serializable: " + src);
        }

        return array;
    }

    private JsonElement serializeProperties(JsonSerializationContext context,
            Map<String, ?> properties) {
        if (properties == null) {
            return new JsonObject();
        } else {
            return context.serialize(properties);
        }
    }
}
